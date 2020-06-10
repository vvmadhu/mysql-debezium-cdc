package com.hcl.redhat.sor.message;

import com.hcl.redhat.sor.message.entity.Customer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.debezium.connector.mysql.MySqlConnectorConfig;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.relational.history.MemoryDatabaseHistory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Properties;

@EnableSwagger2
@ComponentScan(value = "com.hcl.redhat.sor.message.*")
@SpringBootApplication
public class ApplicationConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfiguration.class, args);
    }

    @Value("${bootstrap.url}")
    String kafkaBootstrapUrl;

    @Value("${schema.registry.url}")
    String schemaRegistryUrl;

    @Value("${client.id}")
    String clientId;

    @Value("${database.server}")
    String databaseServer;

    @Value("${database.port}")
    int databasePort;

    @Value("${database.user}")
    String databaseUser;

    @Value("${database.password}")
    String databasePassword;

    @Value("${database.schemaname}")
    String databaseSchemaName;

    @Value("${database.tablename}")
    String databaseTable;

    @Bean
    Producer<String, Customer> customerProducer() {
        return new KafkaProducer<>(getProperties());
    }

    public Properties getProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapUrl);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        return props;
    }

    @Bean
    public io.debezium.config.Configuration customerConnector() {

        return io.debezium.config.Configuration.create()
                .with(EmbeddedEngine.CONNECTOR_CLASS, "io.debezium.connector.mysql.MySqlConnector")
                .with(EmbeddedEngine.OFFSET_STORAGE, "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with(EmbeddedEngine.OFFSET_STORAGE_FILE_FILENAME, "c:/123/cdc/customer-offset.dat")
                .with("offset.flush.interval.ms", 60000)
                .with(EmbeddedEngine.ENGINE_NAME, "customer-mysql-connector")
                .with(MySqlConnectorConfig.SERVER_NAME, "223344")
                .with(MySqlConnectorConfig.HOSTNAME, databaseServer)
                .with(MySqlConnectorConfig.PORT, databasePort)
                .with(MySqlConnectorConfig.USER, databaseUser)
                .with(MySqlConnectorConfig.PASSWORD, databasePassword)
                .with(MySqlConnectorConfig.DATABASE_WHITELIST, databaseSchemaName)
                .with(MySqlConnectorConfig.TABLE_WHITELIST, databaseTable)
                .with(MySqlConnectorConfig.DATABASE_HISTORY,
                        MemoryDatabaseHistory.class.getName()).build();
    }

    @Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.hcl.redhat.sor.message.controller"))
                .build();
    }

}
