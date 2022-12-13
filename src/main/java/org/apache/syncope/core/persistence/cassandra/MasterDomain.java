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
package org.apache.syncope.core.persistence.cassandra;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

@EnableConfigurationProperties(PersistenceProperties.class)
@Configuration(proxyBeanMethods = false)
public class MasterDomain {

    @ConditionalOnMissingBean(name = "MasterTransactionManager")
    @Bean(name = { "MasterTransactionManager", "Master" })
    public PlatformTransactionManager transactionManager() {
        return new AbstractPlatformTransactionManager() {

            private static final long serialVersionUID = -5550417903471186043L;

            @Override
            protected Object doGetTransaction() throws TransactionException {
                return null;
            }

            @Override
            protected void doBegin(
                    final Object transaction,
                    final TransactionDefinition definition) throws TransactionException {

                // nothing to do
            }

            @Override
            protected void doCommit(final DefaultTransactionStatus status) throws TransactionException {
                // nothing to do
            }

            @Override
            protected void doRollback(final DefaultTransactionStatus status) throws TransactionException {
                // nothing to do
            }
        };
    }

    @Bean(name = "MasterContentXML")
    public InputStream masterContentXML(
            final ResourceLoader resourceLoader,
            final PersistenceProperties props) throws IOException {

        return resourceLoader.getResource(props.getDomain().get(0).getContent()).getInputStream();
    }
}
