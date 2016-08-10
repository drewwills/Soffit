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

import org.apereo.portlet.soffit.ITokenizable;

/**
 * Encapsulates username, attributes, and group affiliations.
 *
 * @author drewwills
 */
public class Bearer implements ITokenizable {

    private final String encryptedToken;
    private final String username;
    private final Map<String,List<String>> attributes;
    private final List<String> groups;

    public Bearer(String encryptedToken, String username, Map<String,List<String>> attributes, List<String> groups) {
        this.encryptedToken = encryptedToken;
        this.username = username;
        this.attributes = Collections.unmodifiableMap(attributes);
        this.groups = Collections.unmodifiableList(groups);
    }

    @Override
    public String getEncryptedToken() {
        return encryptedToken;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public List<String> getGroups() {
        return groups;
    }

    @Override
    public String toString() {
        return "Bearer [encryptedToken=" + encryptedToken + ", username=" + username + ", attributes=" + attributes
                + ", groups=" + groups + "]";
    }

}
