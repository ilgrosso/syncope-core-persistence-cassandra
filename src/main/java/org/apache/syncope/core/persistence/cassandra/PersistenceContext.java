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

import com.datastax.oss.driver.api.core.CqlSession;
import org.apache.syncope.common.lib.SyncopeConstants;
import org.apache.syncope.core.persistence.api.DomainRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@EnableCassandraRepositories("org.apache.syncope.core.persistence.cassandra.dao")
@Configuration(proxyBeanMethods = false)
public class PersistenceContext {

    @ConditionalOnMissingBean
    @Bean
    public CqlSessionFactoryBean cqlSessionFactory(final Environment env) {
        CqlSessionFactoryBean session = new CqlSessionFactoryBean();
        session.setContactPoints(env.getProperty("spring.data.cassandra.contact-points"));
        session.setPort(env.getProperty("spring.data.cassandra.port", int.class));
        session.setLocalDatacenter(env.getProperty("spring.data.cassandra.local-datacenter"));
        session.setKeyspaceName(SyncopeConstants.MASTER_DOMAIN);
        return session;
    }

    @ConditionalOnMissingBean
    @Bean
    public CassandraTemplate cassandraTemplate(final CqlSession cqlSession) {
        return new CassandraTemplate(cqlSession);
    }

    @ConditionalOnMissingBean
    @Bean
    public DomainRegistry domainRegistry() {
        return new CassandraDomainRegistry();
    }
}
