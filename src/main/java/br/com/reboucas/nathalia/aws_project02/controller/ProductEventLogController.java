package br.com.reboucas.nathalia.aws_project02.controller;

import br.com.reboucas.nathalia.aws_project02.model.ProductEventLog;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;


@RestController
@RequestMapping("/api/product-event")
@RequiredArgsConstructor
public class ProductEventLogController {
    private final DynamoDbTemplate dynamoDbTemplate;

    @GetMapping
    public ResponseEntity<List<ProductEventLog>> getAllEvents() {
        List<ProductEventLog> productEventLogs = dynamoDbTemplate.scanAll(ProductEventLog.class).items().stream().toList();
        return ResponseEntity.ok(productEventLogs);
    }

    @GetMapping("/{code}")
    public ResponseEntity<List<ProductEventLog>> findByCode(@PathVariable String code) {
        var key = Key.builder().partitionValue(code).build();
        var conditional = QueryConditional.keyEqualTo(key);

        List<ProductEventLog> productEventLogs = dynamoDbTemplate.query(QueryEnhancedRequest.builder()
                        .queryConditional(conditional).build(),
                ProductEventLog.class).items().stream().toList();

        return ResponseEntity.ok(productEventLogs);
    }
}
