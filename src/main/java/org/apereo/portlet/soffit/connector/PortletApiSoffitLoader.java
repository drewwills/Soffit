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

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.portlet.PortalContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.commons.lang3.Validate;
import org.apereo.portlet.soffit.model.v1_0.Context;
import org.apereo.portlet.soffit.model.v1_0.Request;
import org.springframework.stereotype.Component;

/**
 * Concrete {@link ISoffitLoader} that knows how to read information from
 * the Portlet API -- e.g. {@link PortletRequest} and {@link PortletResponse} --
 * and share it with the {@link SoffitRequest}.
 *
 * @author drewwills
 */
@Component
public class PortletApiSoffitLoader extends AbstractSoffitLoader {

    public static final String WINDOW_ID_PREFIX = "w_";

    @Override
    public void load(org.apereo.portlet.soffit.model.v1_0.Payload soffit,
            RenderRequest renderRequest, RenderResponse renderResponse) {

        Validate.notNull(soffit, "The soffit must be instantiated before loading");
        Validate.notNull(renderRequest, "Argument 'renderRequest' cannot be null");
        Validate.notNull(renderResponse, "Argument 'renderResponse' cannot be null");

        /*
         * Request
         */
        final Request request = new Request()
                .setWindowId(WINDOW_ID_PREFIX + renderRequest.getWindowID())
                .setWindowState(renderRequest.getWindowState().toString());

        // Attributes
        request.setAttribute(Request.MODE, Collections.singletonList(renderRequest.getPortletMode().toString()));
        request.setAttribute(Request.SCHEME, Collections.singletonList(renderRequest.getScheme()));
        request.setAttribute(Request.SERVER_NAME, Collections.singletonList(renderRequest.getServerName()));
        final String serverPort = Integer.valueOf(renderRequest.getServerPort()).toString();
        request.setAttribute(Request.SERVER_PORT, Collections.singletonList(serverPort));
        final String secure = Boolean.valueOf(renderRequest.isSecure()).toString();
        request.setAttribute(Request.SECURE, Collections.singletonList(secure));

        // Parameters
        for (Map.Entry<String,String[]> y : renderRequest.getParameterMap().entrySet()) {
            request.setParameter(
                    y.getKey(),
                    Collections.unmodifiableList(Arrays.asList(y.getValue()))
            );
        }

        // Properties
        final Enumeration<String> propertyNames = renderRequest.getPropertyNames();
        while (propertyNames.hasMoreElements()) {
            final String name = propertyNames.nextElement();
            request.setProperty(name, renderRequest.getProperty(name));
        }

        soffit.setRequest(request);

        /*
         * Context
         */
        final PortalContext portalContext = renderRequest.getPortalContext();
        final Context context = new Context()
                .setPortalInfo(portalContext.getPortalInfo());

        // SupportedWindowStates
        final Enumeration<WindowState> supportedWindowStates = portalContext.getSupportedWindowStates();
        while (supportedWindowStates.hasMoreElements()) {
            context.addSupportedWindowState(supportedWindowStates.nextElement().toString());
        }

        soffit.setContext(context);
    }

}
