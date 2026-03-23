package io.quarkiverse.rabbitmqclient;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.rabbitmq.client.Connection;

import io.quarkiverse.rabbitmqclient.util.RabbitMQTestContainer;
import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.test.common.QuarkusTestResource;

@QuarkusTestResource(RabbitMQTestContainer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuarkusRabbitMQConnectionTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest() // Start unit test with your extension loaded
            .setFlatClassPath(true)
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource(
                            QuarkusRabbitMQConnectionTest.class
                                    .getResource("/rabbitmq/rabbitmq-properties.properties"),
                            "application.properties")
                    .addAsResource(QuarkusRabbitMQConnectionTest.class.getResource("/rabbitmq/ca/cacerts.jks"),
                            "rabbitmq/ca/cacerts.jks")
                    .addAsResource(QuarkusRabbitMQConnectionTest.class.getResource("/rabbitmq/client/client.jks"),
                            "rabbitmq/client/client.jks"));

    @Inject
    RabbitMQClient rabbitMQClient;

    @Inject
    @NamedRabbitMQClient("ssl")
    RabbitMQClient rabbitMQClientSsl;

    @Inject
    @NamedRabbitMQClient("mtls")
    RabbitMQClient rabbitMQClientMtls;

    @Test
    @Order(1)
    public void testNonSSL() {
        Connection conn = rabbitMQClient.connect("test-connection-non-ssl");
        Assertions.assertNotNull(conn);
        Assertions.assertEquals("default", rabbitMQClient.getId());
    }

    @Test
    @Order(2)
    public void testRabbitMQSSLDefault() {
        Connection conn = rabbitMQClientSsl.connect("test-connection-ssl");
        Assertions.assertNotNull(conn);
        Assertions.assertEquals("ssl", rabbitMQClientSsl.getId());
    }

    @Test
    @Order(3)
    public void testRabbitMQSSLClientCert() {
        Connection conn = rabbitMQClientMtls.connect("test-connection-ssl-client-cert");
        Assertions.assertNotNull(conn);
        Assertions.assertEquals("mtls", rabbitMQClientMtls.getId());
    }

}
