package xyz.sanvew.neogroup.demo;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
public class IntegrationTestBase {
    private static final String postgresImage = "postgres:16-alpine";
    private static final String toxiproxyImage = "ghcr.io/shopify/toxiproxy:2.9.0";

    private static final String postgresDbName = "neogroup-demo-test";
    private static final String postgresUser = "neogroup-demo-test";
    private static final String postgresPassword = "neogroup-demo-test";
    private static final long postgresConnectionTimeout = 300;

    protected static final Network network =  Network.newNetwork();
    protected static final String postgresNetAlias = "postgres";
    protected static Proxy postgresProxy;

    @Container
    protected static ToxiproxyContainer toxiproxyContainer = new ToxiproxyContainer(toxiproxyImage)
            .withNetwork(network)
//            .withReuse(true)
            ;

    @Container
    protected static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(postgresImage)
            .withDatabaseName(postgresDbName)
            .withUsername(postgresUser)
            .withPassword(postgresPassword)
            .withCommand("postgres -c log_statement=all")
            .withNetwork(network)
            .withNetworkAliases(postgresNetAlias)
//            .withReuse(true)
            ;

    @DynamicPropertySource
    static void configureDatabaseProperties(DynamicPropertyRegistry registry) throws IOException {
        final int postgresMappedPort = 8666;
        final var toxiproxyClient = new ToxiproxyClient(
                toxiproxyContainer.getHost(),
                toxiproxyContainer.getControlPort()
        );
        System.out.println("=============================================");
        if (toxiproxyClient.getProxyOrNull(postgresNetAlias) == null) {
            postgresProxy = toxiproxyClient.createProxy(
                    postgresNetAlias,
                    "0.0.0.0:%d".formatted(postgresMappedPort),
                    "%s:5432".formatted(postgresNetAlias)
            );
        }

        final var jdbcUrl = "jdbc:postgresql://%s:%d/%s".formatted(
                toxiproxyContainer.getHost(),
                toxiproxyContainer.getMappedPort(postgresMappedPort),
                postgreSQLContainer.getDatabaseName()
        );

        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.hikari.connection-timeout", () -> postgresConnectionTimeout);
    }

    protected void postgresDisconnect() throws IOException {
        postgresProxy.disable();
    }

    protected void postgresConnect() throws IOException {
        postgresProxy.enable();
    }
}
