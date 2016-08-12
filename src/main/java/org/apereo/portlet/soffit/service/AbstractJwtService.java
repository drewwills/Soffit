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

import java.util.Date;
import java.util.UUID;

import org.apereo.portlet.soffit.ITokenizable;
import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Base class for services that produce JASON Web Tokens.
 *
 * @author drewwills
 */
public class AbstractJwtService {

    public static final String JWT_ISSUER = "Soffit";

    protected enum Keys {

        /**
         * Concrete Java class to which the JWT deserializes.
         */
        CLASS("class");

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

    protected Claims createClaims(Class<? extends ITokenizable> clazz, Date expires) {

        // Registered claims
        final Claims rslt = Jwts.claims()
                .setIssuer(JWT_ISSUER)
                .setExpiration(expires)
                .setIssuedAt(new Date())
                .setId(UUID.randomUUID().toString());

        // Deserialization class
        rslt.put(Keys.CLASS.getName(), clazz.getName());

        return rslt;

    }

    protected String generateEncryptedToken(Claims claims) {

        final String rslt = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, signatureKey)
                .compact();

        // TODO:  Encryption

        return rslt;

    }

    protected Jws<Claims> parseEncrypteToken(String encryptedToken, Class<? extends ITokenizable> clazz) {

        final Jws<Claims> rslt = Jwts.parser()
                .setSigningKey(signatureKey)
                .parseClaimsJws(encryptedToken);

        // Token expired?
        final Date expires = rslt.getBody().getExpiration();
        if (expires.before(new Date())) {
            final String msg = "The specified token is expired:  " + rslt;
            throw new SecurityException(msg);
        }

        // Sanity check
        final String s = (String) rslt.getBody().get(Keys.CLASS.getName());
        if (!clazz.getName().equals(s)) {
            // Opportunity for future versioning of the data model... needs work
            String msg = "Token class mismatch;  expected '" + clazz.getName() + "' but was '" + s + "'";
            throw new RuntimeException(msg);
        }

        return rslt;

    }

}
