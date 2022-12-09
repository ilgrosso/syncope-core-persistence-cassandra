/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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
package org.apache.syncope.core.persistence.cassandra.entity;

import org.apache.syncope.core.persistence.api.dao.AnySearchDAO;
import org.apache.syncope.core.persistence.api.entity.AnyType;
import org.apache.syncope.core.persistence.api.entity.AnyTypeClass;
import org.apache.syncope.core.persistence.api.entity.ConnPoolConf;
import org.apache.syncope.core.persistence.api.entity.DerSchema;
import org.apache.syncope.core.persistence.api.entity.Entity;
import org.apache.syncope.core.persistence.api.entity.EntityFactory;
import org.apache.syncope.core.persistence.api.entity.Implementation;
import org.apache.syncope.core.persistence.api.entity.PlainSchema;
import org.apache.syncope.core.persistence.api.entity.VirSchema;
import org.apache.syncope.core.persistence.api.entity.anyobject.AnyObject;
import org.apache.syncope.core.persistence.api.entity.group.Group;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.spring.security.SecureRandomUtils;

public class CassandraEntityFactory implements EntityFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Entity> E newEntity(final Class<E> reference) {
        E result;

        if (reference.equals(AnyType.class)) {
            result = (E) new CassandraAnyType();
        } else if (reference.equals(AnyTypeClass.class)) {
            result = (E) new CassandraAnyTypeClass();
        } else if (reference.equals(PlainSchema.class)) {
            result = (E) new CassandraPlainSchema();
        } else if (reference.equals(DerSchema.class)) {
            result = (E) new CassandraDerSchema();
        } else if (reference.equals(VirSchema.class)) {
            result = (E) new CassandraVirSchema();
        } else if (reference.equals(Implementation.class)) {
            result = (E) new CassandraImplementation();
        } else {
            throw new IllegalArgumentException("Could not find a Cassandra implementation of " + reference.getName());
        }

        if (result instanceof AbstractGeneratedKeyEntity) {
            ((AbstractGeneratedKeyEntity) result).setKey(SecureRandomUtils.generateRandomUUID().toString());
        }

        return result;
    }

    @Override
    public ConnPoolConf newConnPoolConf() {
        return null;
    }

    @Override
    public Class<? extends User> userClass() {
        return null;
    }

    @Override
    public Class<? extends Group> groupClass() {
        return null;
    }

    @Override
    public Class<? extends AnyObject> anyObjectClass() {
        return null;
    }

    @Override
    public Class<? extends AnySearchDAO> anySearchDAOClass() {
        return null;
    }
}
