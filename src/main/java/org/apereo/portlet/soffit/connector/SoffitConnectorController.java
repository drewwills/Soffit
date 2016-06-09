package org.apereo.portlet.soffit.connector;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apereo.portlet.soffit.model.v1_0.SoffitRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping(value={"VIEW","EDIT","HELP","CONFIG"})
public class SoffitConnectorController {

    /**
     * Preferences that begin with this String will not be shared with the remote soffit.
     */
    private static final String CONNECTOR_PREFERENCE_PREFIX = SoffitConnectorController.class.getName();
    private static final String SERVICE_URL_PREFERENCE = CONNECTOR_PREFERENCE_PREFIX + ".serviceUrl";
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

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

            final String json = objectMapper.writeValueAsString(buildSoffitRequest(req, res));
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
            logger.error("Failed to invoke serviceUrl '{}'", serviceUrl);
        }

    }

    /*
     * Implementation
     */

    private SoffitRequest buildSoffitRequest(final RenderRequest req, final RenderResponse res) {

        // TODO:  Use a PortletPreference for versioning the SoffitRequest

        final SoffitRequest rslt = new SoffitRequest();

        // PortletMode & WindowState
        rslt.setMode(req.getPortletMode().toString());
        rslt.setWindowState(req.getWindowState().toString());

        // PortletPreferences
        final Map<String,List<String>> preferences = new HashMap<>();
        for (Map.Entry<String,String[]> y : req.getPreferences().getMap().entrySet()) {
            if (y.getKey() != null && !y.getKey().startsWith(CONNECTOR_PREFERENCE_PREFIX)) {
                preferences.put(y.getKey(), Arrays.asList(y.getValue()));
            }
        }
        rslt.setPreferences(preferences);

        // Namespace
        rslt.setNamespace(res.getNamespace());

        // User
        rslt.setUser(req.getUserPrincipal());

        // Portal metadata
        rslt.getPortal().setProvider("uPortal");  // TODO:  Implement!
        rslt.getPortal().setVersion("4.3.0");  // TODO:  Implement!

        return rslt;

    }

}
