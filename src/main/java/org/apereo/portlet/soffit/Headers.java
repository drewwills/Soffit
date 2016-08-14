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

package org.apereo.portlet.soffit;

import org.apereo.portlet.soffit.connector.SoffitConnectorController;

public enum Headers {

    /*
     * Standard HTTP headers
     */

    /**
     * The Authorization field value consists of credentials containing the
     * authentication information of the user agent for the realm of the
     * resource being requested.
     *
     * @since 5.0
     */
    AUTHORIZATION("Authorization"),

    /**
     * Used to specify directives that MUST be obeyed by all caching mechanisms
     * along the request/response chain. The directives specify behavior
     * intended to prevent caches from adversely interfering with the request or
     * response.
     *
     * @since 5.0
     */
    CACHE_CONTROL("Cache-Control"),

    /*
     * Custom headers
     */

    /**
     * HTTP header sent by the {@link SoffitConnectorController} to
     * signal which POJO the JSON payload my be deserialized into.  This is a
     * strategy for versioning and backwards compatibility.  The receiver of an
     * older payload is free to transform it to a newer one, if a newer one is
     * available (and that's the tactic we'll likely emply when it comes to it).
     *
     * @since 5.0
     */
    PAYLOAD_CLASS("X-Soffit-PayloadClass"),

    /**
     * HTTP header sent by the {@link SoffitConnectorController} containing
     * preferences for the Soffit in the form of an encrypted JWT.
     *
     * @since 5.0
     */
    PREFERECES("X-Soffit-Preferences");

    /**
     * Prepended to the Authorization HTTP header to indicate that the value
     * (the rest of it) is a Bearer Token.
     */
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";

    /*
     * Implementation
     */

    private final String name;

    private Headers(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
