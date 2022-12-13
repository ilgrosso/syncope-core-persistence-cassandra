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
import org.apache.syncope.common.lib.types.CipherAlgorithm;
import org.apache.syncope.common.lib.types.IdRepoImplementationType;
import org.apache.syncope.core.persistence.api.dao.ImplementationDAO;
import org.apache.syncope.core.persistence.api.entity.Implementation;
import org.apache.syncope.core.persistence.api.entity.PlainSchema;
import org.apache.syncope.core.spring.ApplicationContextProvider;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(CassandraPlainSchema.TABLE)
public class CassandraPlainSchema extends AbstractSchema implements PlainSchema {

    private static final long serialVersionUID = -8621028596062054739L;

    public static final String TABLE = "PlainSchema";

    @NotNull
    private AttrSchemaType type = AttrSchemaType.String;

    @NotNull
    private String mandatoryCondition = Boolean.FALSE.toString();

    private boolean multivalue = false;

    private boolean uniqueConstraint = false;

    private boolean readonly = false;

    private String conversionPattern;

    private String enumerationValues;

    private String enumerationKeys;

    private String secretKey;

    private CipherAlgorithm cipherAlgorithm;

    private String mimeType;

    private String validator;

    @Override
    public AttrSchemaType getType() {
        return type;
    }

    @Override
    public void setType(final AttrSchemaType type) {
        this.type = type;
    }

    @Override
    public String getMandatoryCondition() {
        return mandatoryCondition;
    }

    @Override
    public void setMandatoryCondition(final String condition) {
        this.mandatoryCondition = condition;
    }

    @Override
    public boolean isMultivalue() {
        return multivalue;
    }

    @Override
    public void setMultivalue(final boolean multivalue) {
        this.multivalue = multivalue;
    }

    @Override
    public boolean isUniqueConstraint() {
        return uniqueConstraint;
    }

    @Override
    public void setUniqueConstraint(final boolean uniquevalue) {
        this.uniqueConstraint = uniquevalue;
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
    public Implementation getValidator() {
        return Optional.ofNullable(validator).
                map(c -> ApplicationContextProvider.getBeanFactory().getBean(ImplementationDAO.class).find(c)).
                orElse(null);
    }

    @Override
    public void setValidator(final Implementation validator) {
        checkType(validator, CassandraImplementation.class);
        checkImplementationType(validator, IdRepoImplementationType.VALIDATOR);
        this.validator = validator.getKey();
    }

    @Override
    public String getEnumerationValues() {
        return enumerationValues;
    }

    @Override
    public void setEnumerationValues(final String enumerationValues) {
        this.enumerationValues = enumerationValues;
    }

    @Override
    public String getEnumerationKeys() {
        return enumerationKeys;
    }

    @Override
    public void setEnumerationKeys(final String enumerationKeys) {
        this.enumerationKeys = enumerationKeys;
    }

    @Override
    public String getConversionPattern() {
        return conversionPattern;
    }

    @Override
    public void setConversionPattern(final String conversionPattern) {
        this.conversionPattern = conversionPattern;
    }

    @Override
    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public void setSecretKey(final String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public CipherAlgorithm getCipherAlgorithm() {
        return cipherAlgorithm;
    }

    @Override
    public void setCipherAlgorithm(final CipherAlgorithm cipherAlgorithm) {
        this.cipherAlgorithm = cipherAlgorithm;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }
}
