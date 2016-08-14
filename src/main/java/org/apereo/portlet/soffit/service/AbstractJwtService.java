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

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apereo.portlet.soffit.ITokenizable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public static final String SIGNATURE_KEY_PROPERTY = "org.apereo.portlet.soffit.jwt.signatureKey";
    public static final String DEFAULT_SIGNATURE_KEY = "CHANGEME";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${" + SIGNATURE_KEY_PROPERTY + ":" + DEFAULT_SIGNATURE_KEY + "}")
    private String signatureKey;

    @PostConstruct
    public void init() {
        if (StringUtils.isBlank(signatureKey)) {
            logger.error("The value of required property {} is blank", SIGNATURE_KEY_PROPERTY);
            throw new IllegalStateException();
        } else if (DEFAULT_SIGNATURE_KEY.equals(signatureKey)) {
            logger.warn("Property {} is using the deafult value;  please change it", SIGNATURE_KEY_PROPERTY);
        }
    }

    protected Claims createClaims(Class<? extends ITokenizable> clazz, String username, Date expires) {

        // Registered claims
        final Claims rslt = Jwts.claims()
                .setIssuer(JWT_ISSUER)
                .setSubject(username)
                .setExpiration(expires)
                .setIssuedAt(new Date())
                .setId(UUID.randomUUID().toString());

        // Deserialization class
        rslt.put(JwtClaims.CLASS.getName(), clazz.getName());

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
        final String s = (String) rslt.getBody().get(JwtClaims.CLASS.getName());
        if (!clazz.getName().equals(s)) {
            // Opportunity for future versioning of the data model... needs work
            String msg = "Token class mismatch;  expected '" + clazz.getName() + "' but was '" + s + "'";
            throw new RuntimeException(msg);
        }

        return rslt;

    }

}
