package org.apereo.portlet.soffit.renderer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apereo.portlet.soffit.model.v1_0.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/soffit")
public class SoffitRendererController {

    private static final String MODEL_NAME = "soffit";

    @Value("${soffit.renderer.viewsLocation:/WEB-INF/soffit/}")
    private String viewsLocation;
    private final Map<ViewTuple,String> availableViews = new HashMap<>();

    final ObjectMapper objectMapper = new ObjectMapper();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value="/{module}", method=RequestMethod.POST)
    public ModelAndView render(final HttpServletRequest req, final @RequestBody String soffitJson,
            final @PathVariable String module) {

        logger.debug("Rendering for request URI '{}', soffitJson={}", req.getRequestURI(), soffitJson);

        try {
            final Payload soffit = objectMapper.readValue(soffitJson, Payload.class);
            final String viewName = selectView(req, module, soffit);
            return new ModelAndView(viewName.toString(), MODEL_NAME, soffit);
        } catch (IOException e) {
            throw new IllegalArgumentException("Request body was not JSON or was not a valid SoffitRequest", e);
        }

    }

    /*
     * Implementation
     */

    private String selectView(final HttpServletRequest req, final String module, final Payload soffit) {

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

        // Need to make a selection based on 3 things:  module (above), mode, & windowState
        final String modeLowercase = soffit.getRequest().getMode().toLowerCase();
        final String windowStateLowercase = soffit.getRequest().getWindowState().toLowerCase();

        final ViewTuple viewTuple = new ViewTuple(modulePath, modeLowercase, windowStateLowercase);
        String rslt = availableViews.get(viewTuple);
        if (rslt == null) {
            /*
             * This circumstance means that we haven't looked (yet);
             * check for a file named to match all 3.
             */
            final String pathBasedOnModeAndState = getCompletePathforParts(modulePath, modeLowercase, windowStateLowercase);
            if (moduleResources.contains(pathBasedOnModeAndState)) {
                // We have a winner!
                availableViews.put(viewTuple, pathBasedOnModeAndState);
                rslt = pathBasedOnModeAndState;
            } else {
                // Widen the search (within this module) based on PortletMode only
                final String pathBasedOnModeOnly = getCompletePathforParts(modulePath, modeLowercase);
                if (moduleResources.contains(pathBasedOnModeOnly)) {
                    // We still need to store the choice so we're not constantly looking
                    availableViews.put(viewTuple, pathBasedOnModeOnly);
                    rslt = pathBasedOnModeOnly;
                } else {
                    throw new IllegalStateException("Unable to select a view for PortletMode="
                            + soffit.getRequest().getMode() + " and WindowState=" + soffit.getRequest().getWindowState());
                }
            }
        }

        logger.info("Selected viewName='{}' for PortletMode='{}' and WindowState='{}'",
                                rslt, soffit.getRequest().getMode(), soffit.getRequest().getWindowState());

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

    /*
     * Nested Types
     */

    private static final class ViewTuple {

        private final String moduleName;
        private final String mode;
        private final String windowState;

        public ViewTuple(String moduleName, String mode, String windowState) {
            this.moduleName = moduleName;
            this.mode = mode;
            this.windowState = windowState;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((mode == null) ? 0 : mode.hashCode());
            result = prime * result + ((moduleName == null) ? 0 : moduleName.hashCode());
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
            ViewTuple other = (ViewTuple) obj;
            if (mode == null) {
                if (other.mode != null)
                    return false;
            } else if (!mode.equals(other.mode))
                return false;
            if (moduleName == null) {
                if (other.moduleName != null)
                    return false;
            } else if (!moduleName.equals(other.moduleName))
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
            return "ViewTuple [moduleName=" + moduleName + ", mode=" + mode + ", windowState=" + windowState + "]";
        }

    }

}
