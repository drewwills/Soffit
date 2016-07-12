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

package org.apereo.portlet.soffit.connector;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public abstract class AbstractSoffitLoader implements ISoffitLoader {

    private final int order;

    public AbstractSoffitLoader() {
        this(ISoffitLoader.DEFAULT_LOADER_ORDER);
    }

    public AbstractSoffitLoader(int order) {
        this.order = order;
    }

    @Override
    public final int getOrder() {
        return this.order;
    }

    public void load(Object soffit, RenderRequest renderRequest, RenderResponse renderResponse) {
        if (soffit.getClass().equals(org.apereo.portlet.soffit.model.v1_0.Payload.class)) {
            load((org.apereo.portlet.soffit.model.v1_0.Payload) soffit, renderRequest, renderResponse);
        } else {
            throw new IllegalArgumentException("Payload class not supported:  " + soffit.getClass());
        }
    }

}
