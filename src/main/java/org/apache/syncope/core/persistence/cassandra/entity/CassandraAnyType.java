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
import javax.validation.constraints.NotNull;
import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.syncope.core.persistence.api.entity.AnyType;
import org.apache.syncope.core.persistence.api.entity.AnyTypeClass;
import org.apache.syncope.core.persistence.cassandra.dao.CassandraAnyTypeClassDAO;
import org.apache.syncope.core.spring.ApplicationContextProvider;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.util.CollectionUtils;

@Table(CassandraAnyType.TABLE)
public class CassandraAnyType extends AbstractProvidedKeyEntity implements AnyType {

    private static final long serialVersionUID = 2668267884059219835L;

    public static final String TABLE = "AnyType";

    @NotNull
    private AnyTypeKind kind;

    private final List<String> classes = new ArrayList<>();

    @Override
    public AnyTypeKind getKind() {
        return kind;
    }

    @Override
    public void setKind(final AnyTypeKind kind) {
        this.kind = kind;
    }

    @Override
    public boolean add(final AnyTypeClass anyTypeClass) {
        checkType(anyTypeClass, CassandraAnyTypeClass.class);
        return classes.contains(anyTypeClass.getKey()) || classes.add(anyTypeClass.getKey());
    }

    @Override
    public List<? extends AnyTypeClass> getClasses() {
        return classes.stream().
                map(c -> ApplicationContextProvider.getBeanFactory().getBean(CassandraAnyTypeClassDAO.class).
                findById(c).orElse(null)).
                filter(Objects::nonNull).
                collect(Collectors.toList());
    }

    public CassandraAnyType withClasses(final List<String> classes) {
        this.classes.clear();
        if (!CollectionUtils.isEmpty(classes)) {
            this.classes.addAll(classes);
        }
        return this;
    }
}
