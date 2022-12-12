package org.apache.syncope.core.persistence.cassandra;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import org.apache.syncope.common.lib.SyncopeConstants;
import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.syncope.core.persistence.api.dao.AnyTypeDAO;
import org.apache.syncope.core.persistence.api.entity.AnyType;
import org.apache.syncope.core.persistence.api.entity.EntityFactory;
import org.apache.syncope.core.persistence.cassandra.ApacheSyncopeCorePersistenceCassandraApplicationTests.Initializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private EntityFactory entityFactory;

    @Autowired
    private AnyTypeDAO anyTypeDAO;

    @Test
    void contextLoads() {
        AnyType user = entityFactory.newEntity(AnyType.class);
        user.setKey(AnyTypeKind.USER.name());
        user.setKind(AnyTypeKind.USER);

        user = anyTypeDAO.save(user);
        assertNotNull(user);

        assertNotNull(anyTypeDAO.find(AnyTypeKind.USER.name()));

        assertNotNull(anyTypeDAO.findUser());
        assertNull(anyTypeDAO.findGroup());
    }
}
