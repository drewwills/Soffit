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
import java.util.List;
import java.util.Map;

public class Request {

    private String Etag;
    private String windowId;
    private String namespace;
    private String authType;
    private String mode;
    private String windowState;
    private String scheme;
    private String serverName;
    private int serverPort;
    private boolean secure;

    private Map<String,List<String>> preferences = new HashMap<>();
    private Map<String,List<String>> parameters = new HashMap<>();
    private Map<String,String> properties = new HashMap<>();
    private List<String> supportedContentTypes = new ArrayList<>();

    public String getEtag() {
        return Etag;
    }

    public Request setEtag(String etag) {
        Etag = etag;
        return this;
    }

    public String getAuthType() {
        return authType;
    }

    public Request setAuthType(String authType) {
        this.authType = authType;
        return this;
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

    public String getWindowId() {
        return windowId;
    }

    public Request setWindowId(String windowId) {
        this.windowId = windowId;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public Request setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getMode() {
        return mode;
    }

    public Request setMode(String mode) {
        this.mode = mode;
        return this;
    }

    public String getWindowState() {
        return windowState;
    }

    public Request setWindowState(String windowState) {
        this.windowState = windowState;
        return this;
    }

    public String getScheme() {
        return scheme;
    }

    public Request setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public Request setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public int getServerPort() {
        return serverPort;
    }

    public Request setServerPort(int serverPort) {
        this.serverPort = serverPort;
        return this;
    }

    public boolean isSecure() {
        return secure;
    }

    public Request setSecure(boolean secure) {
        this.secure = secure;
        return this;
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

    public Map<String,String> getProperties() {
        // Defensive copy
        return Collections.unmodifiableMap(properties);
    }

    public String removeProperty(String key) {
        return properties.remove(key);
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((Etag == null) ? 0 : Etag.hashCode());
        result = prime * result + ((authType == null) ? 0 : authType.hashCode());
        result = prime * result + ((mode == null) ? 0 : mode.hashCode());
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result + ((preferences == null) ? 0 : preferences.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + ((scheme == null) ? 0 : scheme.hashCode());
        result = prime * result + (secure ? 1231 : 1237);
        result = prime * result + ((serverName == null) ? 0 : serverName.hashCode());
        result = prime * result + serverPort;
        result = prime * result + ((supportedContentTypes == null) ? 0 : supportedContentTypes.hashCode());
        result = prime * result + ((windowId == null) ? 0 : windowId.hashCode());
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
        Request other = (Request) obj;
        if (Etag == null) {
            if (other.Etag != null)
                return false;
        } else if (!Etag.equals(other.Etag))
            return false;
        if (authType == null) {
            if (other.authType != null)
                return false;
        } else if (!authType.equals(other.authType))
            return false;
        if (mode == null) {
            if (other.mode != null)
                return false;
        } else if (!mode.equals(other.mode))
            return false;
        if (namespace == null) {
            if (other.namespace != null)
                return false;
        } else if (!namespace.equals(other.namespace))
            return false;
        if (parameters == null) {
            if (other.parameters != null)
                return false;
        } else if (!parameters.equals(other.parameters))
            return false;
        if (preferences == null) {
            if (other.preferences != null)
                return false;
        } else if (!preferences.equals(other.preferences))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        if (scheme == null) {
            if (other.scheme != null)
                return false;
        } else if (!scheme.equals(other.scheme))
            return false;
        if (secure != other.secure)
            return false;
        if (serverName == null) {
            if (other.serverName != null)
                return false;
        } else if (!serverName.equals(other.serverName))
            return false;
        if (serverPort != other.serverPort)
            return false;
        if (supportedContentTypes == null) {
            if (other.supportedContentTypes != null)
                return false;
        } else if (!supportedContentTypes.equals(other.supportedContentTypes))
            return false;
        if (windowId == null) {
            if (other.windowId != null)
                return false;
        } else if (!windowId.equals(other.windowId))
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
        return "Request [Etag=" + Etag + ", windowId=" + windowId + ", namespace=" + namespace + ", authType="
                + authType + ", mode=" + mode + ", windowState=" + windowState + ", scheme=" + scheme + ", serverName="
                + serverName + ", serverPort=" + serverPort + ", secure=" + secure + ", preferences=" + preferences
                + ", parameters=" + parameters + ", properties=" + properties + ", supportedContentTypes="
                + supportedContentTypes + "]";
    }

}
