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

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import org.apache.syncope.common.lib.SyncopeConstants;
import org.apache.syncope.core.persistence.api.entity.EntityFactory;
import org.apache.syncope.core.persistence.cassandra.AbstractTest.Initializer;
import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.cassandra.core.cql.generator.CreateKeyspaceCqlGenerator;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringJUnitConfig(
        classes = PersistenceTestContext.class,
        initializers = Initializer.class)
public abstract class AbstractTest {

    private static CassandraContainer<?> CASSANDRA;

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(final ConfigurableApplicationContext ctx) {
            CASSANDRA = new CassandraContainer<>("cassandra:4.1").withExposedPorts(9042).withReuse(true);

            CASSANDRA.start();

            CqlSession session = CqlSession.builder().
                    addContactPoint(CASSANDRA.getContactPoint()).
                    withLocalDatacenter(CASSANDRA.getLocalDatacenter()).
                    withKeyspace((CqlIdentifier) null).
                    build();

            session.execute(CreateKeyspaceCqlGenerator.toCql(
                    CreateKeyspaceSpecification.createKeyspace(SyncopeConstants.MASTER_DOMAIN).ifNotExists()));

            TestPropertyValues.of(
                    "spring.data.cassandra.contact-points=" + CASSANDRA.getHost(),
                    "spring.data.cassandra.port=" + CASSANDRA.getMappedPort(9042),
                    "spring.data.cassandra.local-datacenter=" + CASSANDRA.getLocalDatacenter()
            ).applyTo(ctx);
        }
    }

    @AfterAll
    public static void tearDown() {
        if (CASSANDRA != null) {
            CASSANDRA.stop();
        }
    }

    @Autowired
    protected EntityFactory entityFactory;

}
