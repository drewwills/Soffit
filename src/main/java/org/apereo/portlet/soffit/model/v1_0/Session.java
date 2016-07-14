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

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Session {

    private long creationTime;
    private int maxInactiveInterval;

    public long getCreationTime() {
        return creationTime;
    }

    public Session setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public Session setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (creationTime ^ (creationTime >>> 32));
        result = prime * result + maxInactiveInterval;
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
        Session other = (Session) obj;
        if (creationTime != other.creationTime)
            return false;
        if (maxInactiveInterval != other.maxInactiveInterval)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
            append("creationTime", creationTime).
            append("maxInactiveInterval", maxInactiveInterval).
            toString();
    }

}
