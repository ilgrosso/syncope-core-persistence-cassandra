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
package org.apache.syncope.core.persistence.cassandra.dao;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.apache.commons.lang3.ClassUtils;
import org.apache.syncope.core.persistence.api.attrvalue.validation.InvalidEntityException;
import org.apache.syncope.core.persistence.api.entity.Any;
import org.apache.syncope.core.persistence.api.entity.DynMembership;
import org.apache.syncope.core.persistence.api.entity.Entity;
import org.apache.syncope.core.persistence.api.entity.GroupableRelatable;
import org.apache.syncope.core.persistence.api.entity.ProvidedKeyEntity;
import org.apache.syncope.core.persistence.api.entity.Schema;
import org.apache.syncope.core.persistence.api.entity.policy.Policy;
import org.apache.syncope.core.persistence.api.entity.task.Task;
import org.apache.syncope.core.spring.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.repository.query.CassandraEntityInformation;
import org.springframework.data.cassandra.repository.support.SimpleCassandraRepository;

public class SyncopeCassandraRepository<T, ID> extends SimpleCassandraRepository<T, ID> {

    private static final Logger LOG = LoggerFactory.getLogger(SyncopeCassandraRepository.class);

    private Validator validator;

    SyncopeCassandraRepository(
            final CassandraEntityInformation<T, ID> metadata,
            final CassandraOperations operations) {

        super(metadata, operations);
    }

    protected Validator validator() {
        synchronized (this) {
            if (validator == null) {
                validator = ApplicationContextProvider.getApplicationContext().getBean(Validator.class);
            }
        }
        return validator;
    }

    public T find(final ID key) {
        return findById(key).orElse(null);
    }

    @Override
    public <S extends T> S save(final S entity) {
        Set<ConstraintViolation<Object>> violations = validator().validate(entity);
        if (!violations.isEmpty()) {
            LOG.warn("Bean validation errors found: {}", violations);

            Class<?> entityInt = null;
            for (Class<?> interf : ClassUtils.getAllInterfaces(entity.getClass())) {
                if (!Entity.class.equals(interf)
                        && !ProvidedKeyEntity.class.equals(interf)
                        && !Schema.class.equals(interf)
                        && !Task.class.equals(interf)
                        && !Policy.class.equals(interf)
                        && !GroupableRelatable.class.equals(interf)
                        && !Any.class.equals(interf)
                        && !DynMembership.class.equals(interf)
                        && Entity.class.isAssignableFrom(interf)) {

                    entityInt = interf;
                }
            }

            throw new InvalidEntityException(entityInt == null
                    ? "Entity" : entityInt.getSimpleName(), violations);
        }

        return super.save(entity);
    }
}
