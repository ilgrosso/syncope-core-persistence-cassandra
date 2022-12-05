package org.apache.syncope.core.persistence.cassandra;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import org.apache.syncope.common.lib.SyncopeConstants;
import org.apache.syncope.core.persistence.cassandra.ApacheSyncopeCorePersistenceCassandraApplicationTests.Initializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.cassandra.core.cql.generator.CreateKeyspaceCqlGenerator;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.CassandraContainer;

@SpringBootTest(classes = PersistenceTestContext.class)
@ContextConfiguration(initializers = Initializer.class)
class ApacheSyncopeCorePersistenceCassandraApplicationTests {

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(final ConfigurableApplicationContext ctx) {
            CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:4.1").withExposedPorts(9042);

            cassandra.start();

            CqlSession session = CqlSession.builder().
                    addContactPoint(cassandra.getContactPoint()).
                    withLocalDatacenter(cassandra.getLocalDatacenter()).
                    withKeyspace((CqlIdentifier) null).
                    build();

            session.execute(CreateKeyspaceCqlGenerator.toCql(
                    CreateKeyspaceSpecification.createKeyspace(SyncopeConstants.MASTER_DOMAIN).ifNotExists()));

            TestPropertyValues.of(
                    "spring.data.cassandra.contact-points=" + cassandra.getHost(),
                    "spring.data.cassandra.port=" + cassandra.getMappedPort(9042),
                    "spring.data.cassandra.local-datacenter=" + cassandra.getLocalDatacenter()
            ).applyTo(ctx);
        }
    }

    @Test
    void contextLoads() {
    }
}
