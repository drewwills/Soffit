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

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        final Map<String, ISoffitLoader> map = applicationContext.getBeansOfType(ISoffitLoader.class);
        soffitLoaders.addAll(map.values());
        Collections.sort(soffitLoaders);
    }

    @RenderMapping
    public void invokeService(final RenderRequest req, final RenderResponse res) {

        final PortletPreferences prefs = req.getPreferences();
        final String serviceUrl = prefs.getValue(SERVICE_URL_PREFERENCE, null);
        if (serviceUrl == null) {
            throw new IllegalStateException("Missing portlet prefernce value for " + SERVICE_URL_PREFERENCE);
        }

        logger.debug("Invoking serviceUrl '{}'", serviceUrl);

        final HttpPost postMethod = new HttpPost(serviceUrl);
        try {

            final String json = objectMapper.writeValueAsString(buildPayload(req, res));
            postMethod.setEntity(new StringEntity(json));

            final HttpResponse httpResponse = httpClient.execute(postMethod);
            final int statusCode = httpResponse.getStatusLine().getStatusCode();
            logger.debug("HTTP response code for url '{}' was '{}'", serviceUrl, statusCode);

            if (statusCode != HttpStatus.SC_OK) {
                logger.error("Failed to get content from remote service '{}';  HttpStatus={}", serviceUrl, statusCode);
                res.getWriter().write("FAILED!  statusCode="+statusCode);  // TODO:  Better message
                return;
            }

            final HttpEntity entity = httpResponse.getEntity();
            IOUtils.copy(entity.getContent(), res.getPortletOutputStream());

        } catch (IOException e) {
            logger.error("Failed to invoke serviceUrl '{}' with reason {}", serviceUrl, e.getMessage());
        }

    }

    /*
     * Implementation
     */

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

}
