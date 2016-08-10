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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apereo.portlet.soffit.model.v1_0.Bearer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Responsible for issuing and parsing Bearer tokens.
 *
 * @author drewwills
 */
@Service
public class BearerService {

    public static final String JWT_ISSUER = "Soffit";

    private enum Keys {

        /**
         * Concrete Java class to which the JWT deserializes.
         */
        CLASS("class"),

        /**
         * List of group names to which the user belongs.
         */
        GROUPS("groups");

        /*
         * Implementation
         */

        private final String name;

        private Keys(String name) {
            this.name = name;
        }

        public static Keys forName(String name) {
            Keys rslt = null;  // default
            for (Keys k : Keys.values()) {
                if (k.getName().equals(name)) {
                    rslt = k;
                }
            }
            return rslt;
        }

        public String getName() {
            return name;
        }

    }

    @Value("${org.apereo.portlet.soffit.model.v1_0.BearerService.signatureKey}")
    private String signatureKey;

    public Bearer createBearer(String username, Map<String,List<String>> attributes, List<String> groups) {

        // Registered claims
        final Claims claims = Jwts.claims()
                .setIssuer(JWT_ISSUER)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setId(UUID.randomUUID().toString());

        // Deserialization class
        claims.put(Keys.CLASS.getName(), Bearer.class.getName());

        /*
         * User attributes; attribute names that match registered attributes
         * (https://www.iana.org/assignments/jwt/jwt.xhtml) will be
         * automatically portable.
         */
        for (Map.Entry<String,List<String>> y : attributes.entrySet()) {
            final String name = y.getKey();
            switch (y.getValue().size()) {
                case 0:
                    // Do nothing...
                    break;
                case 1:
                    // Model as a single value (in this a good idea?)
                    claims.put(name, y.getValue().get(0));
                    break;
                default:
                    // Retain the collection
                    claims.put(name, y.getValue());
                    break;
            }
        }

        // Groups
        claims.put(Keys.GROUPS.getName(), groups);

        final String bearerToken = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, signatureKey)
                .compact();

        // TODO:  Encryption

        return new Bearer(bearerToken, username, attributes, groups);

    }

    public Bearer parseBearerToken(String bearerToken) {

        final Jws<Claims> claims = Jwts.parser()
                .setSigningKey(signatureKey)
                .parseClaimsJws(bearerToken);

        // Sanity check
        final String clazz = (String) claims.getBody().get(Keys.CLASS.getName());
        if (!Bearer.class.getName().equals(clazz)) {
            // Opportunity for future versioning of the data model...
            String msg = "Unsuppored Bearer token class:  " + clazz;
            throw new RuntimeException(msg);
        }

        final String username = claims.getBody().getSubject();

        final Map<String,List<String>> attributes = new HashMap<>();
        for (Map.Entry<String,Object> y : claims.getBody().entrySet()) {
            final String key = y.getKey();
            if (Keys.forName(key) != null) {
                // Skip these;  we handle these differently
                continue;
            }

            if (y.getValue() instanceof List) {
                @SuppressWarnings("unchecked")
                final List<String> values = (List<String>) y.getValue();
                attributes.put(key, values);
            } else if (y.getValue() instanceof String) {
                // Convert (back) to a single-item list
                final String value = (String) y.getValue();
                attributes.put(key, Collections.singletonList(value));
            }
        }

        @SuppressWarnings("unchecked")
        final List<String> groups = (List<String>) claims.getBody().get(Keys.GROUPS.getName());

        return new Bearer(bearerToken, username, attributes, groups);

    }

}
