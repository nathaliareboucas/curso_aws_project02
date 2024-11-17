package br.com.reboucas.nathalia.aws_project02.config.dev;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("dev")
@Slf4j
public class DynamoDBConfigDev {
    private static final String TABLE_NAME = "product_event_log";
    private final DynamoDbClient dynamoDbClient;

    public DynamoDBConfigDev() {
        this.dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .region(Region.US_EAST_1)
                .build();

        try {
            DynamoDbWaiter dbWaiter = dynamoDbClient.waiter();
            CreateTableResponse response = dynamoDbClient.createTable(createTableRequest());
            DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                    .tableName(TABLE_NAME)
                    .build();

            WaiterResponse<DescribeTableResponse> waiterResponse = dbWaiter.waitUntilTableExists(tableRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private CreateTableRequest createTableRequest() {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(AttributeDefinition.builder()
                .attributeName("pk")
                .attributeType(ScalarAttributeType.S)
                .build());
        attributeDefinitions.add(AttributeDefinition.builder()
                .attributeName("sk")
                .attributeType(ScalarAttributeType.S)
                .build());

        List<KeySchemaElement> keySchemaElements = new ArrayList<>();
        keySchemaElements.add(KeySchemaElement.builder()
                .attributeName("pk")
                .keyType(KeyType.HASH)
                .build());
        keySchemaElements.add(KeySchemaElement.builder()
                .attributeName("sk")
                .keyType(KeyType.RANGE)
                .build());

        return CreateTableRequest.builder()
                .attributeDefinitions(attributeDefinitions)
                .keySchema(keySchemaElements)
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(10L)
                        .writeCapacityUnits(10L)
                        .build())
                .tableName(TABLE_NAME)
                .build();
    }

    @Bean
    @Primary
    public DynamoDbClient dynamoDbClient() {
        return this.dynamoDbClient;
    }
}
