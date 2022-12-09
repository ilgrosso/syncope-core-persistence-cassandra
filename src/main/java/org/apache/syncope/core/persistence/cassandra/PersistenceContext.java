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

import java.util.List;
import org.apache.syncope.common.lib.SyncopeConstants;
import org.apache.syncope.core.persistence.api.DomainRegistry;
import org.apache.syncope.core.persistence.api.entity.EntityFactory;
import org.apache.syncope.core.persistence.cassandra.converter.LocaleReadConverter;
import org.apache.syncope.core.persistence.cassandra.converter.LocaleWriteConverter;
import org.apache.syncope.core.persistence.cassandra.entity.CassandraEntityFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@EnableCassandraRepositories("org.apache.syncope.core.persistence.cassandra.dao")
@Configuration(proxyBeanMethods = false)
public class PersistenceContext extends AbstractCassandraConfiguration {

    @Autowired
    private Environment env;

    @Override
    protected String getContactPoints() {
        return env.getProperty("spring.data.cassandra.contact-points");
    }

    @Override
    protected int getPort() {
        return env.getProperty("spring.data.cassandra.port", int.class);
    }

    @Override
    protected String getLocalDataCenter() {
        return env.getProperty("spring.data.cassandra.local-datacenter");
    }

    @Override
    protected String getKeyspaceName() {
        return SyncopeConstants.MASTER_DOMAIN;
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Bean
    @Override
    public CassandraCustomConversions customConversions() {
        return new CassandraCustomConversions(List.of(new LocaleReadConverter(), new LocaleWriteConverter()));
    }

    @ConditionalOnMissingBean
    @Bean
    public EntityFactory entityFactory() {
        return new CassandraEntityFactory();
    }

    @ConditionalOnMissingBean
    @Bean
    public DomainRegistry domainRegistry() {
        return new CassandraDomainRegistry();
    }
}
