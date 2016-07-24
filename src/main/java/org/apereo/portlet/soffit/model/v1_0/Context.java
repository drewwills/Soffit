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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides information about the portal or container in which the soffit is
 * running.
 *
 * @author drewwills
 */
public class Context {

    private String portalInfo;

    private Map<String,List<String>> attributes = new HashMap<>();
    private Set<String> supportedWindowStates = new HashSet<>();

    /**
     * Identifying platform and version information about the calling portal.
     */
    public String getPortalInfo() {
        return portalInfo;
    }

    public Context setPortalInfo(String portalInfo) {
        this.portalInfo = portalInfo;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((portalInfo == null) ? 0 : portalInfo.hashCode());
        result = prime * result + ((supportedWindowStates == null) ? 0 : supportedWindowStates.hashCode());
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
        Context other = (Context) obj;
        if (portalInfo == null) {
            if (other.portalInfo != null)
                return false;
        } else if (!portalInfo.equals(other.portalInfo))
            return false;
        if (supportedWindowStates == null) {
            if (other.supportedWindowStates != null)
                return false;
        } else if (!supportedWindowStates.equals(other.supportedWindowStates))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Context [portalInfo=" + portalInfo + ", supportedWindowStates=" + supportedWindowStates + "]";
    }

}
