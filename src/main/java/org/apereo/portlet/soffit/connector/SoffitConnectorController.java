/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apereo.portlet.soffit.connector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apereo.portlet.soffit.renderer.SoffitRendererController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping(value={"VIEW","EDIT","HELP","CONFIG"})
public class SoffitConnectorController implements ApplicationContextAware {

    /**
     * Preferences that begin with this String will not be shared with the remote soffit.
     */
    private static final String CONNECTOR_PREFERENCE_PREFIX = SoffitConnectorController.class.getName();
    private static final String SERVICE_URL_PREFERENCE = CONNECTOR_PREFERENCE_PREFIX + ".serviceUrl";
    private static final String PAYLOAD_CLASS_PREFERENCE = CONNECTOR_PREFERENCE_PREFIX + ".payloadClass";

    private static final String DEFAULT_PAYLOAD_CLASS = org.apereo.portlet.soffit.model.v1_0.Payload.class.getName();
    private static final int TIMEOUT_SECONDS = 10;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();

    private final RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(TIMEOUT_SECONDS * 1000)
            .setConnectTimeout(TIMEOUT_SECONDS * 1000)
            .build();

    private final CloseableHttpClient httpClient = HttpClientBuilder
            .create()
            .setDefaultRequestConfig(requestConfig)
            .setConnectionManager(poolingHttpClientConnectionManager)
            .build();

    private ApplicationContext applicationContext;
    private final List<ISoffitLoader> soffitLoaders = new ArrayList<>();

    @Autowired
    @Qualifier(value="org.apereo.portlet.soffit.connector.SoffitConnectorController.RESPONSE_CACHE")
    private Cache responseCache;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        final Map<String, ISoffitLoader> map = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ISoffitLoader.class);
        soffitLoaders.addAll(map.values());
        Collections.sort(soffitLoaders, new OrderComparator());
    }

    @RenderMapping
    public void invokeService(final RenderRequest req, final RenderResponse res) {

        final PortletPreferences prefs = req.getPreferences();
        final String serviceUrl = prefs.getValue(SERVICE_URL_PREFERENCE, null);
        if (serviceUrl == null) {
            throw new IllegalStateException("Missing portlet prefernce value for " + SERVICE_URL_PREFERENCE);
        }

        // First look in cache for an existing response that applies to this request
        ResponseWrapper responseValue = fetchContentFromCacheIfAvailable(req, serviceUrl);
        if (responseValue != null) {
            logger.debug("Response value obtained from cache for serviceUrl '{}'", serviceUrl);
        } else {

            logger.debug("No applicable response in cache;  invoking serviceUrl '{}'", serviceUrl);

            final HttpPost postMethod = new HttpPost(serviceUrl);
            try {

                // Provide a payload
                final Object payload = buildPayload(req, res);
                postMethod.setHeader(SoffitRendererController.PAYLOAD_CLASS_HEADER, payload.getClass().getName());
                final String json = objectMapper.writeValueAsString(payload);
                postMethod.setEntity(new StringEntity(json));

                // Send the request
                final HttpResponse httpResponse = httpClient.execute(postMethod);
                final int statusCode = httpResponse.getStatusLine().getStatusCode();
                logger.debug("HTTP response code for url '{}' was '{}'", serviceUrl, statusCode);

                if (statusCode != HttpStatus.SC_OK) {
                    logger.error("Failed to get content from remote service '{}';  HttpStatus={}", serviceUrl, statusCode);
                    res.getWriter().write("FAILED!  statusCode="+statusCode);  // TODO:  Better message
                    return;
                }

                responseValue = extractResponseAndCacheIfAppropriate(httpResponse, req, serviceUrl);

            } catch (IOException e) {
                logger.error("Failed to invoke serviceUrl '{}'", serviceUrl, e);
            }

        }

        try {
            res.getPortletOutputStream().write(responseValue.getBytes());
        } catch (IOException e) {
            logger.error("Failed to write the response for serviceUrl '{}'", serviceUrl, e);
        }

    }

    /*
     * Implementation
     */

    private ResponseWrapper fetchContentFromCacheIfAvailable(final RenderRequest req, final String serviceUrl) {

        ResponseWrapper rslt = null;  // default

        final List<CacheTuple> cacheKeysToTry = new ArrayList<>();
        // Don't use private-scope caching for anonymous users
        if (req.getRemoteUser() != null) {
            cacheKeysToTry.add(
                    // Private-scope cache key
                    new CacheTuple(serviceUrl, req.getPortletMode().toString(),
                            req.getWindowState().toString(), req.getRemoteUser())
            );
        }
        cacheKeysToTry.add(
                // Public-scope cache key
                new CacheTuple(serviceUrl, req.getPortletMode().toString(),
                        req.getWindowState().toString())
        );

        for (CacheTuple key : cacheKeysToTry) {
            final Element cacheElement = this.responseCache.get(key);
            if (cacheElement != null) {
                rslt = (ResponseWrapper) cacheElement.getObjectValue();
                break;
            }
        }

        return rslt;

    }

    private ResponseWrapper extractResponseAndCacheIfAppropriate(final HttpResponse httpResponse,
            final RenderRequest req, final String serviceUrl) {

        final HttpEntity entity = httpResponse.getEntity();
        ResponseWrapper rslt;
        try {
            rslt = new ResponseWrapper(IOUtils.toByteArray(entity.getContent()));
        } catch (UnsupportedOperationException | IOException e) {
            throw new RuntimeException("Failed to read the response", e);
        }

        // Cache the response if indicated by the remote service
        final Header cacheControlHeader = httpResponse.getFirstHeader(SoffitRendererController.CACHE_CONTROL_HEADER);
        if (cacheControlHeader != null) {
            final String cacheControlValue = cacheControlHeader.getValue();
            logger.debug("Soffit with serviceUrl='{}' specified cache-control header value='{}'",
                                                                serviceUrl, cacheControlValue);
            if (!cacheControlValue.equals(SoffitRendererController.CACHE_CONTROL_NOCACHE)) {
                CacheTuple cacheTuple = null;
                // TODO:  Need to find a polished utility that parses a cache-control header, or write one
                final String[] tokens = cacheControlValue.split(",");
                // At present, we expect all valid values to be either 'no-cache' or in the form 'public, max-age=300'
                if (tokens.length == 2) {
                    final String maxAge = tokens[1].trim().substring("max-age=".length());
                    int timeToLive = Integer.parseInt(maxAge);
                    if ("private".equals(tokens[0].trim())) {
                        cacheTuple = new CacheTuple(serviceUrl, req.getPortletMode().toString(),
                                req.getWindowState().toString(), req.getRemoteUser());
                    } else if ("public".equals(tokens[0].trim())) {
                        cacheTuple = new CacheTuple(serviceUrl, req.getPortletMode().toString(),
                                req.getWindowState().toString());
                    }
                    logger.debug("Produced cacheTuple='{}' for cacheControlValue='{}'", cacheTuple, cacheControlValue);
                    if (cacheTuple != null) {
                        final Element element = new Element(cacheTuple, rslt);
                        element.setTimeToLive(timeToLive);
                        responseCache.put(element);
                    } else {
                        logger.warn("The remote soffit specified cacheControlValue='{}', "
                                + "but SoffitConnectorController failed to generate a cacheTuple");
                    }
                }
            }
        }

        return rslt;

    }

    private Object buildPayload(final RenderRequest req, final RenderResponse res) {

        try {
            final String payloadClassName = req.getPreferences().getValue(PAYLOAD_CLASS_PREFERENCE, DEFAULT_PAYLOAD_CLASS);
            Class<?> payloadClass = Class.forName(payloadClassName);
            final Object rslt = payloadClass.newInstance();
            for (ISoffitLoader loader : soffitLoaders) {
                loader.load(rslt, req, res);
            }
            return rslt;
        } catch (Exception e) {
            final String msg = "Failed to load the soffit payload";
            throw new RuntimeException(msg, e);
        }

    }

    /*
     * Nested Types
     */

    private static final class CacheTuple {
        private final String serviceUrl;
        private final String mode;
        private final String windowState;
        private final String username;
        private final boolean publicScope;

        /**
         * Creates a CacheTuple for a public-scope soffit response.
         */
        public CacheTuple(String serviceUrl, String mode, String windowState) {
            this.serviceUrl = serviceUrl;
            this.mode = mode;
            this.windowState = windowState;
            this.username = null;
            this.publicScope = true;
        }

        /**
         * Creates a CacheTuple for a private-scope soffit response.
         */
        public CacheTuple(String serviceUrl, String mode, String windowState, String username) {
            this.serviceUrl = serviceUrl;
            this.mode = mode;
            this.windowState = windowState;
            this.username = username;
            this.publicScope = false;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((mode == null) ? 0 : mode.hashCode());
            result = prime * result + (publicScope ? 1231 : 1237);
            result = prime * result + ((serviceUrl == null) ? 0 : serviceUrl.hashCode());
            result = prime * result + ((username == null) ? 0 : username.hashCode());
            result = prime * result + ((windowState == null) ? 0 : windowState.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CacheTuple other = (CacheTuple) obj;
            if (mode == null) {
                if (other.mode != null)
                    return false;
            } else if (!mode.equals(other.mode))
                return false;
            if (publicScope != other.publicScope)
                return false;
            if (serviceUrl == null) {
                if (other.serviceUrl != null)
                    return false;
            } else if (!serviceUrl.equals(other.serviceUrl))
                return false;
            if (username == null) {
                if (other.username != null)
                    return false;
            } else if (!username.equals(other.username))
                return false;
            if (windowState == null) {
                if (other.windowState != null)
                    return false;
            } else if (!windowState.equals(other.windowState))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "CacheTuple [serviceUrl=" + serviceUrl + ", mode=" + mode + ", windowState=" + windowState
                    + ", username=" + username + ", publicScope=" + publicScope + "]";
        }

    }

    public static final class ResponseWrapper {
        private final byte[] bytes;

        public ResponseWrapper(byte[] bytes) {
            this.bytes = bytes;
        }

        public byte[] getBytes() {
            return bytes;
        }
    }

}
