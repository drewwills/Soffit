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
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.commons.lang3.Validate;
import org.apereo.portlet.soffit.model.v1_0.Context;
import org.apereo.portlet.soffit.model.v1_0.Request;
import org.apereo.portlet.soffit.model.v1_0.Session;
import org.apereo.portlet.soffit.model.v1_0.User;
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
                .setAuthType(renderRequest.getAuthType())
                .setEtag(renderRequest.getETag())
                .setMode(renderRequest.getPortletMode().toString())
                .setNamespace(renderResponse.getNamespace())
                .setScheme(renderRequest.getScheme())
                .setSecure(renderRequest.isSecure())
                .setServerName(renderRequest.getServerName())
                .setServerPort(renderRequest.getServerPort())
                .setWindowId(renderRequest.getWindowID())
                .setWindowState(renderRequest.getWindowState().toString());

        // Parameters
        for (Map.Entry<String,String[]> y : renderRequest.getParameterMap().entrySet()) {
            request.setParameter(
                    y.getKey(),
                    Collections.unmodifiableList(Arrays.asList(y.getValue()))
            );
        }

        // Preferences
        for (Map.Entry<String,String[]> y : renderRequest.getPreferences().getMap().entrySet()) {
            /*
             * We ignore (skip) preferences that exist for the benefit of the
             * SoffitConnectorController.
             */
            if (y.getKey().startsWith(SoffitConnectorController.CONNECTOR_PREFERENCE_PREFIX)) {
                continue;
            }
            request.setPreference(
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

        // SupportedContentTypes
        final Enumeration<String> supportedContentTypes = renderRequest.getResponseContentTypes();
        while (supportedContentTypes.hasMoreElements()) {
            request.addSupportedContentType(supportedContentTypes.nextElement());
        }

        soffit.setRequest(request);

        /*
         * User
         */
        final User user = new User()
                .setUsername(renderRequest.getRemoteUser());

        // Session
        final PortletSession portletSession = renderRequest.getPortletSession(true);
        final Session session = new Session()
                .setCreationTime(portletSession.getCreationTime())
                .setMaxInactiveInterval(portletSession.getMaxInactiveInterval());
        user.setSession(session);

        soffit.setUser(user);

        /*
         * Context
         */
        final PortalContext portalContext = renderRequest.getPortalContext();
        final PortletContext portletContext = portletSession.getPortletContext();
        final Context context = new Context()
                .setPortalInfo(portalContext.getPortalInfo())
                .setServerInfo(portletContext.getServerInfo());

        // InitParameters
        final Enumeration<String> initParameterNames = portletContext.getInitParameterNames();
        while (initParameterNames.hasMoreElements()) {
            final String name = initParameterNames.nextElement();
            context.setInitParameter(name, portletContext.getInitParameter(name));
        }

        // ContainerRuntimeOptions
        final Enumeration<String> containerRuntimeOptions = portletContext.getContainerRuntimeOptions();
        while (containerRuntimeOptions.hasMoreElements()) {
            context.addContainerRuntimeOption(containerRuntimeOptions.nextElement());
        }

        // SupportedModes
        final Enumeration<PortletMode> supportedModes = portalContext.getSupportedPortletModes();
        while (supportedModes.hasMoreElements()) {
            context.addSupportedMode(supportedModes.nextElement().toString());
        }

        // SupportedWindowStates
        final Enumeration<WindowState> supportedWindowStates = portalContext.getSupportedWindowStates();
        while (supportedWindowStates.hasMoreElements()) {
            context.addSupportedWindowState(supportedWindowStates.nextElement().toString());
        }

        soffit.setContext(context);

    }

}
