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

package org.apereo.portlet.soffit.service;

/**
 * Set of all the JWT claims in use within Soffit.
 *
 * @author drewwills
 */
public enum JwtClaims {

    /*
     * Registered Claims (https://tools.ietf.org/html/rfc7519#section-4.1)
     *
     * All RFC 7519 registered claims, whether we use them or not
     */

    ISSUER("iss"),
    SUBJECT("sub"),
    AUDIENCE("aud"),
    EXPIRATION_TIME("exp"),
    NOT_BEFORE("nbf"),
    ISSUED_AT("iat"),
    JWT_ID("jti"),

    /*
     * Custom Claims
     */

    /**
     * Concrete Java class to which the JWT deserializes.
     */
    CLASS("class"),

    /**
     * List of group names to which the user belongs;  used by BearerService.
     */
    GROUPS("groups");

    /*
     * Implementation
     */

    private final String name;

    private JwtClaims(String name) {
        this.name = name;
    }

    public static JwtClaims forName(String name) {
        JwtClaims rslt = null;  // default
        for (JwtClaims claim : JwtClaims.values()) {
            if (claim.getName().equals(name)) {
                rslt = claim;
            }
        }
        return rslt;
    }

    public String getName() {
        return name;
    }

}
