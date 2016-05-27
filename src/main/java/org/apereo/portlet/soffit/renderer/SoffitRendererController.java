package org.apereo.portlet.soffit.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apereo.portlet.soffit.model.SoffitRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/soffit")
public class SoffitRendererController {

    private static final String VIEW_NOT_PROVIDED = SoffitRendererController.class.getName() + ".VIEW_NOT_PROVIDED";
    private static final String MODEL_NAME = "soffit";

    @Value("${soffit.renderer.viewsLocation:/WEB-INF/soffit/}")
    private String viewsLocation;
    private final Map<String,Map<String,String>> availableViews = new HashMap<>();

    final ObjectMapper objectMapper = new ObjectMapper();

    @Value("classpath:/soffit.properties")
    private Resource soffitPropertiesResource;

    private Map<String,String> soffitProperties = Collections.emptyMap();  // default

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void init() {
        Properties properties = new Properties();
        Map<String,String> map = new HashMap<>();
        try (final InputStream stream = soffitPropertiesResource.getInputStream()) {
            if (stream != null) {
                // A soffit.properties file was included, so we will use it
                logger.debug("File soffit.properties was provided");
                properties.load(stream);
                for (final String name : properties.stringPropertyNames()) {
                    map.put(name, properties.getProperty(name));
                }
            }
        } catch (IOException e) {
            logger.error("Classpath file soffit.properties was provided, but failed to load", e);
        }
        if (!map.isEmpty()) {
            soffitProperties = Collections.unmodifiableMap(map);
        }
        logger.debug("Initialized soffitProperties:  {}", soffitProperties);
    }

    @RequestMapping(value="/{module}", method=RequestMethod.POST)
    public ModelAndView render(final HttpServletRequest req, final @RequestBody String soffitJson,
            final @PathVariable String module) {

        logger.debug("Rendering for request URI '{}', soffitJson={}", req.getRequestURI(), soffitJson);

        try {
            final SoffitRequest soffit = objectMapper.readValue(soffitJson, SoffitRequest.class);
            final String viewName = selectView(req, module, soffit);
            return new ModelAndView(viewName.toString(), MODEL_NAME, soffit);
        } catch (IOException e) {
            throw new IllegalArgumentException("Request body was not JSON or was not a valid SoffitRequest", e);
        }

    }

    @ModelAttribute("properties")
    public Map<String,String> getProperties() {
        return soffitProperties;
    }

    /*
     * Implementation
     */

    private String selectView(final HttpServletRequest req, final String module, final SoffitRequest soffit) {

        final StringBuilder modulePathBuilder = new StringBuilder().append(viewsLocation);
        if (!viewsLocation.endsWith("/")) {
            // Final slash in the configs is optional
            modulePathBuilder.append("/");
        }
        modulePathBuilder.append(module).append("/");
        final String modulePath = modulePathBuilder.toString();

        logger.debug("Calculated modulePath of '{}'", modulePath);

        @SuppressWarnings("unchecked")
        final Set<String> moduleResources = req.getSession().getServletContext().getResourcePaths(modulePath);

        // Narrow the choices based on PortletMode
        final String modeLowercase = soffit.getMode().toLowerCase();
        Map<String,String> viewsForMode = availableViews.get(modeLowercase);
        if (viewsForMode == null) {
            // First time for this PortletMode;  seed the Map
            viewsForMode = new HashMap<>();
            availableViews.put(modeLowercase, viewsForMode);
        }

        // Narrow the choices further based on WindowState
        final String windowStateLowercase = soffit.getWindowState().toLowerCase();
        String rslt = viewsForMode.get(windowStateLowercase);

        if (rslt == null) {
            /*
             * This circumstance means that we haven't looked (yet);
             * check for a file named to match this pattern.
             */
            final String pathBasedOnModeAndState = getCompletePathforParts(modulePath, modeLowercase, windowStateLowercase);
            if (moduleResources.contains(pathBasedOnModeAndState)) {
                // We have a winner!
                viewsForMode.put(windowStateLowercase, pathBasedOnModeAndState);
                rslt = pathBasedOnModeAndState;
            } else {
                // Trigger the next resolution step
                viewsForMode.put(windowStateLowercase, VIEW_NOT_PROVIDED);
                rslt = VIEW_NOT_PROVIDED;
            }
        }

        if (rslt.equals(VIEW_NOT_PROVIDED)) {
            /*
             * This circumstance means that there isn't a specific view for this
             * PortletMode *and* WindowState;  widen the search to PortletMode only.
             */
            final String pathBasedOnModeOnly = getCompletePathforParts(modulePath, modeLowercase);
            if (moduleResources.contains(pathBasedOnModeOnly)) {
                // We still need to store the choice so we're not constantly looking
                viewsForMode.put(windowStateLowercase, pathBasedOnModeOnly);
                rslt = pathBasedOnModeOnly;
            } else {
                throw new IllegalStateException("Unable to select a view for PortletMode="
                        + soffit.getMode() + " and WindowState=" + soffit.getWindowState());
            }
        }

        logger.info("Selected viewName='{}' for PortletMode='{}' and WindowState='{}'",
                                rslt, soffit.getMode(), soffit.getWindowState());

        return rslt;

    }

    private String getCompletePathforParts(final String... parts) {

        StringBuilder rslt = new StringBuilder();

        for (String part : parts) {
            rslt.append(part);
            if (!part.endsWith("/")) {
                // First part will be a directory
                rslt.append(".");
            }
        }

        rslt.append("jsp");  // TODO:  Fix!

        logger.debug("Calculated path '{}' for parts={}", rslt, parts);

        return rslt.toString();

    }

}
