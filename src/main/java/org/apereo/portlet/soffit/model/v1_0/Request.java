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

package org.apereo.portlet.soffit.model.v1_0;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Request {

    private String Etag;
    private String windowId;
    private String namespace;
    private String authType;
    private String portalInfo;
    private String mode;
    private String windowState;
    private String scheme;
    private String serverName;
    private int serverPort;
    private boolean secure;

    private Map<String,List<String>> preferences = new HashMap<>();
    private Map<String,List<String>> parameters = new HashMap<>();
    private Map<String,List<String>> properties = new HashMap<>();
    private Set<String> supportedModes = new HashSet<>();
    private Set<String> supportedWindowStates = new HashSet<>();
    private List<Locale> supportedLocales = new ArrayList<>();
    private List<String> supportedContentTypes = new ArrayList<>();

    public String getEtag() {
        return Etag;
    }

    public void setEtag(String etag) {
        Etag = etag;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public Map<String, List<String>> getParameters() {
        // Defensive copy
        return Collections.unmodifiableMap(parameters);
    }

    public List<String> removeParameter(String key) {
        return parameters.remove(key);
    }

    public void setParameter(String key, List<String> values) {
        parameters.put(key, values);
    }

    public List<Locale> getSupportedLocales() {
        // Defensive copy
        return Collections.unmodifiableList(supportedLocales);
    }

    public void addSupportedLocale(Locale locale) {
        supportedLocales.add(locale);
    }

    public boolean removeSupportedLocale(Locale locale) {
        return supportedLocales.remove(locale);
    }

    public Locale getPreferredLocale() {
        // The preferred Locale is the first in the list
        return supportedLocales.isEmpty()
                ? null
                : supportedLocales.get(0);
    }

    public String getWindowId() {
        return windowId;
    }

    public void setWindowId(String windowId) {
        this.windowId = windowId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getPortalInfo() {
        return portalInfo;
    }

    public void setPortalInfo(String portalInfo) {
        this.portalInfo = portalInfo;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getWindowState() {
        return windowState;
    }

    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public Map<String, List<String>> getPreferences() {
        // Defensive copy
        return Collections.unmodifiableMap(preferences);
    }

    public List<String> removePreference(String key) {
        return preferences.remove(key);
    }

    public void setPreference(String key, List<String> values) {
        preferences.put(key, values);
    }

    public Map<String, List<String>> getProperties() {
        // Defensive copy
        return Collections.unmodifiableMap(properties);
    }

    public List<String> removeProperty(String key) {
        return properties.remove(key);
    }

    public void setProperty(String key, List<String> values) {
        properties.put(key, values);
    }

    public Set<String> getSupportedModes() {
        // Defensive copy
        return Collections.unmodifiableSet(supportedModes);
    }

    public void addSupportedMode(String mode) {
        supportedModes.add(mode);
    }

    public boolean removeSupportedMode(String mode) {
        return supportedModes.remove(mode);
    }

    public Set<String> getSupportedWindowStates() {
        // Defensive copy
        return Collections.unmodifiableSet(supportedWindowStates);
    }

    public void addSupportedWindowState(String windowState) {
        supportedWindowStates.add(windowState);
    }

    public boolean removeSupportedWindowState(String windowState) {
        return supportedWindowStates.remove(windowState);
    }

    public List<String> getSupportedContentTypes() {
        // Defensive copy
        return Collections.unmodifiableList(supportedContentTypes);
    }

    public void addSupportedContentType(String contentType) {
        supportedContentTypes.add(contentType);
    }

    public boolean removeSupportedContentType(String contentType) {
        return supportedContentTypes.remove(contentType);
    }

}
