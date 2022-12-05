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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.apache.syncope.core.persistence.api.dao.AnyTypeClassDAO;
import org.apache.syncope.core.persistence.api.entity.AnyTypeClass;
import org.apache.syncope.core.persistence.api.entity.Schema;
import org.apache.syncope.core.spring.ApplicationContextProvider;

public abstract class AbstractSchema extends AbstractProvidedKeyEntity implements Schema {

    private static final long serialVersionUID = -9222344997225831269L;

    private String anyTypeClass;

    private Map<Locale, String> labels = new HashMap<>();

    @Override
    public AnyTypeClass getAnyTypeClass() {
        return Optional.ofNullable(anyTypeClass).
                map(c -> ApplicationContextProvider.getBeanFactory().getBean(AnyTypeClassDAO.class).find(c)).
                orElse(null);
    }

    @Override
    public void setAnyTypeClass(final AnyTypeClass anyTypeClass) {
        checkType(anyTypeClass, CassandraAnyTypeClass.class);
        this.anyTypeClass = anyTypeClass.getKey();
    }

    @Override
    public Optional<String> getLabel(final Locale locale) {
        return Optional.ofNullable(labels.get(locale));
    }

    @Override
    public Map<Locale, String> getLabels() {
        return labels;
    }
}
