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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides some information about the user's request to which the portal will
 * respond using output from the soffit.
 *
 * @author drewwills
 */
public class Request {

    /**
     * @since 5.0
     */
    public static final String MODE = "mode";

    /**
     * @since 5.0
     */
    public static final String SCHEME = "scheme";

    /**
     * @since 5.0
     */
    public static final String SERVER_NAME = "serverName";

    /**
     * @since 5.0
     */
    public static final String SERVER_PORT = "serverPort";

    /**
     * @since 5.0
     */
    public static final String SECURE = "secure";

    private String windowId;
    private String windowState;

    private Map<String,List<String>> attributes = new HashMap<>();
    private Map<String,List<String>> parameters = new HashMap<>();
    private Map<String,String> properties = new HashMap<>();

    /**
     * A unique, container-provided identifier for the content produced by this
     * Soffit.  The value must conform to the rules for Javascript variables,
     * element IDs, and CSS clasess.  It should be used in these capacities to
     * prevent naming collisions in the DOM.
     */
    public String getWindowId() {
        return windowId;
    }

    public Request setWindowId(String windowId) {
        this.windowId = windowId;
        return this;
    }

    public String getWindowState() {
        return windowState;
    }

    public Request setWindowState(String windowState) {
        this.windowState = windowState;
        return this;
    }

    public Map<String, List<String>> getAttributes() {
        // Defensive copy
        return Collections.unmodifiableMap(attributes);
    }

    public List<String> removeAttribute(String key) {
        return attributes.remove(key);
    }

    public void setAttribute(String key, List<String> values) {
        attributes.put(key, values);
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
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
        if (attributes == null) {
            if (other.attributes != null)
                return false;
        } else if (!attributes.equals(other.attributes))
            return false;
        if (parameters == null) {
            if (other.parameters != null)
                return false;
        } else if (!parameters.equals(other.parameters))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
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
        return "Request [windowId=" + windowId + ", windowState=" + windowState + ", attributes=" + attributes
                + ", parameters=" + parameters + ", properties=" + properties + "]";
    }

}
