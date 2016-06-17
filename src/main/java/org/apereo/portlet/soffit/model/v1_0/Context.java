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
import java.util.Map;
import java.util.Set;

public class Context {

    private String serverInfo;

    private Map<String,String> initParameters = new HashMap<>();
    private Set<String> containerRuntimeOptions = new HashSet<>();

    public String getServerInfo() {
        return serverInfo;
    }

    public Context setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
        return this;
    }

    public Map<String, String> getInitParameters() {
        // Defensive copy
        return Collections.unmodifiableMap(initParameters);
    }

    public String removeInitParameter(String key) {
        return initParameters.remove(key);
    }

    public void setInitParameter(String key, String value) {
        initParameters.put(key, value);
    }

    public Set<String> getContainerRuntimeOptions() {
        return containerRuntimeOptions;
    }

    public void addContainerRuntimeOption(String containerRuntimeOption) {
        containerRuntimeOptions.add(containerRuntimeOption);
    }

    public boolean removeContainerRuntimeOption(String containerRuntimeOption) {
        return containerRuntimeOptions.remove(containerRuntimeOption);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((containerRuntimeOptions == null) ? 0 : containerRuntimeOptions.hashCode());
        result = prime * result + ((initParameters == null) ? 0 : initParameters.hashCode());
        result = prime * result + ((serverInfo == null) ? 0 : serverInfo.hashCode());
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
        if (containerRuntimeOptions == null) {
            if (other.containerRuntimeOptions != null)
                return false;
        } else if (!containerRuntimeOptions.equals(other.containerRuntimeOptions))
            return false;
        if (initParameters == null) {
            if (other.initParameters != null)
                return false;
        } else if (!initParameters.equals(other.initParameters))
            return false;
        if (serverInfo == null) {
            if (other.serverInfo != null)
                return false;
        } else if (!serverInfo.equals(other.serverInfo))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Context [serverInfo=" + serverInfo + ", initParameters=" + initParameters + ", containerRuntimeOptions="
                + containerRuntimeOptions + "]";
    }

}
