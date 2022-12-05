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

import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.apache.syncope.common.lib.types.AttrSchemaType;
import org.apache.syncope.core.persistence.api.dao.AnyTypeDAO;
import org.apache.syncope.core.persistence.api.entity.AnyType;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.VirSchema;
import org.apache.syncope.core.spring.ApplicationContextProvider;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public class CassandraVirSchema extends AbstractSchema implements VirSchema {

    private static final long serialVersionUID = 3274006935328590141L;

    private boolean readonly = false;

    //@NotNull
    //private CassandraExternalResource resource;
    @NotNull
    private String anyType;

    @NotNull
    private String extAttrName;

    @Override
    public AttrSchemaType getType() {
        return AttrSchemaType.String;
    }

    @Override
    public String getMandatoryCondition() {
        return Boolean.FALSE.toString().toLowerCase();
    }

    @Override
    public boolean isMultivalue() {
        return true;
    }

    @Override
    public boolean isUniqueConstraint() {
        return false;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public void setReadonly(final boolean readonly) {
        this.readonly = readonly;
    }

    @Override
    public ExternalResource getResource() {
        return null;//resource;
    }

    @Override
    public void setResource(final ExternalResource resource) {
        //checkType(resource, CassandraExternalResource.class);
        //this.resource = (CassandraExternalResource) resource;
    }

    @Override
    public AnyType getAnyType() {
        return Optional.ofNullable(anyType).
                map(c -> ApplicationContextProvider.getBeanFactory().getBean(AnyTypeDAO.class).find(c)).
                orElse(null);
    }

    @Override
    public void setAnyType(final AnyType anyType) {
        checkType(anyType, CassandraAnyType.class);
        this.anyType = anyType.getKey();
    }

    @Override
    public String getExtAttrName() {
        return extAttrName;
    }

    @Override
    public void setExtAttrName(final String extAttrName) {
        this.extAttrName = extAttrName;
    }
}
