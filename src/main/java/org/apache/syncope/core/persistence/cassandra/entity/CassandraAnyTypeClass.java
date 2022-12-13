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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.syncope.core.persistence.api.entity.AnyTypeClass;
import org.apache.syncope.core.persistence.api.entity.DerSchema;
import org.apache.syncope.core.persistence.api.entity.PlainSchema;
import org.apache.syncope.core.persistence.api.entity.VirSchema;
import org.apache.syncope.core.persistence.cassandra.dao.CassandraDerSchemaDAO;
import org.apache.syncope.core.persistence.cassandra.dao.CassandraPlainSchemaDAO;
import org.apache.syncope.core.persistence.cassandra.dao.CassandraVirSchemaDAO;
import org.apache.syncope.core.spring.ApplicationContextProvider;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.util.CollectionUtils;

@Table(CassandraAnyTypeClass.TABLE)
public class CassandraAnyTypeClass extends AbstractProvidedKeyEntity implements AnyTypeClass {

    private static final long serialVersionUID = -1750247153774475453L;

    public static final String TABLE = "AnyTypeClass";

    private final List<String> plainSchemas = new ArrayList<>();

    private final List<String> derSchemas = new ArrayList<>();

    private final List<String> virSchemas = new ArrayList<>();

    @Override
    public boolean add(final PlainSchema schema) {
        checkType(schema, CassandraPlainSchema.class);
        return this.plainSchemas.add(schema.getKey());
    }

    @Override
    public List<? extends PlainSchema> getPlainSchemas() {
        return plainSchemas.stream().
                map(c -> ApplicationContextProvider.getBeanFactory().getBean(CassandraPlainSchemaDAO.class).
                findById(c).orElse(null)).
                filter(Objects::nonNull).
                collect(Collectors.toList());
    }

    public CassandraAnyTypeClass withPlainSchemas(final List<String> plainSchemas) {
        this.plainSchemas.clear();
        if (!CollectionUtils.isEmpty(plainSchemas)) {
            this.plainSchemas.addAll(plainSchemas);
        }
        return this;
    }

    @Override
    public boolean add(final DerSchema schema) {
        checkType(schema, CassandraDerSchema.class);
        return this.derSchemas.add(schema.getKey());
    }

    @Override
    public List<? extends DerSchema> getDerSchemas() {
        return derSchemas.stream().
                map(c -> ApplicationContextProvider.getBeanFactory().getBean(CassandraDerSchemaDAO.class).
                findById(c).orElse(null)).
                filter(Objects::nonNull).
                collect(Collectors.toList());
    }

    public CassandraAnyTypeClass withDerSchemas(final List<String> derSchemas) {
        this.derSchemas.clear();
        if (!CollectionUtils.isEmpty(derSchemas)) {
            this.derSchemas.addAll(derSchemas);
        }
        return this;
    }

    @Override
    public boolean add(final VirSchema schema) {
        checkType(schema, CassandraVirSchema.class);
        return this.virSchemas.add(schema.getKey());
    }

    @Override
    public List<? extends VirSchema> getVirSchemas() {
        return virSchemas.stream().
                map(c -> ApplicationContextProvider.getBeanFactory().getBean(CassandraVirSchemaDAO.class).
                findById(c).orElse(null)).
                filter(Objects::nonNull).
                collect(Collectors.toList());
    }

    public CassandraAnyTypeClass withVirSchemas(final List<String> virSchemas) {
        this.virSchemas.clear();
        if (!CollectionUtils.isEmpty(virSchemas)) {
            this.virSchemas.addAll(virSchemas);
        }
        return this;
    }
}
