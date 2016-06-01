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
import java.util.List;
import java.util.Map;

public class SoffitRequest {

    private String mode;
    private String windowState;
    private Map<String,List<String>> preferences;
    private String namespace;
    private final Portal portal = new Portal();

    public Portal getPortal() {
        return portal;
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

    public Map<String, List<String>> getPreferences() {
        return preferences;
    }

    public void setPreferences(Map<String, List<String>> preferences) {
        this.preferences = Collections.unmodifiableMap(preferences);  // defensive copy
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String toString() {
        return "SoffitRequest [mode=" + mode + ", windowState=" + windowState + ", preferences=" + preferences
                + ", namespace=" + namespace + ", portal=" + portal + "]";
    }

}
