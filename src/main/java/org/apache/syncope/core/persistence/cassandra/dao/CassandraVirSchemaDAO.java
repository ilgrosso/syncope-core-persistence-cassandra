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

import java.util.Collection;
import java.util.List;
import org.apache.syncope.core.persistence.api.dao.VirSchemaDAO;
import org.apache.syncope.core.persistence.api.entity.AnyTypeClass;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.VirSchema;
import org.apache.syncope.core.persistence.cassandra.entity.CassandraVirSchema;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CassandraVirSchemaDAO extends CassandraRepository<CassandraVirSchema, String>, VirSchemaDAO {

    @Override
    public default List<String> find(final ExternalResource resource) {
        return List.of();
    }

    @Override
    public default List<? extends VirSchema> find(String resource, String anyType) {
        return List.of();
    }

    @Override
    public default List<? extends VirSchema> findByKeyword(final String keyword) {
        return List.of();
    }

    @Override
    public default List<? extends VirSchema> findByAnyTypeClasses(final Collection<AnyTypeClass> anyTypeClasses) {
        return List.of();
    }
}
