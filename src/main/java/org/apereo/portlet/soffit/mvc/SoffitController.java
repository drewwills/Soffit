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

package org.apereo.portlet.soffit.mvc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@Controller
@RequestMapping
public class SoffitController implements ServletContextAware {

    public static final String VIEWS_LOCATION_INIT_PARAM = "viewsLocation";
    public static final String VIEWS_LOCATION_DEFAULT = "/WEB-INF/jsp/";

    private static final String VIEW_NOT_PROVIDED = SoffitController.class.getName() + ".VIEW_NOT_PROVIDED";

    private ServletContext servletContext;
    private final Map<String,Map<String,String>> availableViews = new HashMap<>();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @RenderMapping
    public String render(final PortletRequest req) {
        return selectView(req);
    }

    /*
     * Implementation
     */

    private String selectView(final PortletRequest req) {

        // Narrow the choices based on PortletMode
        final String portletModeLowercase = req.getPortletMode().toString().toLowerCase();
        Map<String,String> viewsForMode = availableViews.get(portletModeLowercase);
        if (viewsForMode == null) {
            // First time for this PortletMode;  seed the Map
            viewsForMode = new HashMap<>();
            availableViews.put(portletModeLowercase, viewsForMode);
        }

        // Narrow the choices further based on WindowState
        final String windowStateLowercase = req.getWindowState().toString().toLowerCase();
        String rslt = viewsForMode.get(windowStateLowercase);


        if (rslt == null) {
            /*
             * This circumstance means that we haven't looked (yet);
             * check for a file named to match this pattern.
             */
            final String pathBasedOnModeAndState = getCompletePathforParts(req, portletModeLowercase, windowStateLowercase);
            final File fileBasedOnModeAndState = new File(servletContext.getRealPath(pathBasedOnModeAndState));
            if (fileBasedOnModeAndState.exists()) {
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
            final String pathBasedOnModeOnly = getCompletePathforParts(req, portletModeLowercase);
            final File fileBasedOnModeOnly = new File(servletContext.getRealPath(pathBasedOnModeOnly));
            if (fileBasedOnModeOnly.exists()) {
                // We still need to store the choice so we're not constantly looking
                viewsForMode.put(windowStateLowercase, pathBasedOnModeOnly);
                rslt = pathBasedOnModeOnly;
            } else {
                // TODO:  Maybe insert some helpful instructions from the classpath?
                throw new IllegalStateException("Unable to select a view for PortletMode="
                        + req.getPortletMode() + " and WindowState=" + req.getWindowState());
            }
        }

        logger.debug("Selected viewName='{}' for PortletMode='{}' and WindowState='{}'",
                                rslt, req.getPortletMode(), req.getWindowState());

        return rslt;

    }

    private String getCompletePathforParts(final PortletRequest req, final String... parts) {
 
        String viewsLocation = req.getPortletSession().getPortletContext().getInitParameter(VIEWS_LOCATION_INIT_PARAM);
        if (viewsLocation == null) {
            /*
             * This circumstance means the viewsLocation portlet init
             * parameter was not set;  use VIEWS_LOCATION_DEFAULT.
             */
            viewsLocation = VIEWS_LOCATION_DEFAULT;
        }

        StringBuilder path = new StringBuilder().append(viewsLocation);

        if (!viewsLocation.endsWith("/")) {
            // Final slash in the init param is optional
            path.append("/");
        }

        for (String part : parts) {
            path.append(part).append(".");
        }

        path.append("jsp");

        return path.toString();

    }

}
