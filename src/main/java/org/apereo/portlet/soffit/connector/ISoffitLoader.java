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

import org.springframework.core.Ordered;

/**
 * Concrete implementations of this interface are responsible for loading the
 * {@link SoffitRequest} with information.  They run in the same JVM as the
 * {@link SoffitConnectorController}.<p>
 *
 * Beans in the application context that implement this interface are gathered
 * and sorted by <code>order</code>.  Lower-order beans are invoked before
 * higher-order beans.  Beans with the same order are not guaranteed to run in a
 * specific sequence.  Instances of this interface that are provided by Soffit
 * itself have an order value of zero.
 *
 * @author drewwills
 */
public interface ISoffitLoader extends Ordered {

    public static final int DEFAULT_LOADER_ORDER = 0;

    /**
     * Load the specified soffit POJO with the information available from this
     * bean.  This overload of the load method exists so the
     * {@link SoffitConnectorController} can be agnostic of the version it's
     * using.
     */
    void load(Object soffit, RenderRequest renderRequest, RenderResponse renderResponse);

    /**
     * Load the specified v1.0 POJO with the information available from this
     * bean.  NOTE:  SoffitRequest is shown here fully-qualified as a visual
     * reminder that we could handle versioning by overloading the load()
     * method.  Alternately we could use a transformation approach to
     * "downgrade" a {@link SoffitRequest} where needed.
     */
    void load(org.apereo.portlet.soffit.model.v1_0.Payload soffit, RenderRequest renderRequest, RenderResponse renderResponse);

}
