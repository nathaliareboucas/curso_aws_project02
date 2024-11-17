package br.com.reboucas.nathalia.aws_project02.service;

import br.com.reboucas.nathalia.aws_project02.model.Envelope;
import br.com.reboucas.nathalia.aws_project02.model.ProductEvent;
import br.com.reboucas.nathalia.aws_project02.model.ProductEventLog;
import br.com.reboucas.nathalia.aws_project02.model.SnsMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductEventConsumer {
    private final ObjectMapper objectMapper;
    private final DynamoDbTemplate dynamoDbTemplate;

    @JmsListener(destination = "${aws.sqs.queue.product.events.name}")
    public void receiverProductEvent(TextMessage textMessage) throws JMSException, JsonProcessingException {
        SnsMessage snsMessage = objectMapper.readValue(textMessage.getText(), SnsMessage.class);
        Envelope envelope = objectMapper.readValue(snsMessage.getMessage(), Envelope.class);
        ProductEvent productEvent = objectMapper.readValue(envelope.getData(), ProductEvent.class);
        log.info("Product event received - Event: {} - ProductId: {} - MessageId: {}",
                envelope.getEventType(),
                productEvent.getProductId(),
                snsMessage.getMessageId());

        ProductEventLog productEventLog = buildProductEventlog(envelope, productEvent);

        ProductEventLog productEventLogSaved = dynamoDbTemplate.save(productEventLog);
        log.info("Product event log saved - PK: {} - SK: {} - ProductId: {} - EventType: {}",
                productEventLogSaved.getPk(),
                productEventLogSaved.getSk(),
                productEventLogSaved.getProductId(),
                productEventLogSaved.getEventType());
    }

    private ProductEventLog buildProductEventlog(Envelope eveEnvelope, ProductEvent productEvent) {
        long timestamp = Instant.now().toEpochMilli();

        ProductEventLog productEventLog = new ProductEventLog();
        productEventLog.setPk(productEvent.getCode());
        productEventLog.setSk(eveEnvelope.getEventType() + "_" + timestamp);
        productEventLog.setEventType(eveEnvelope.getEventType().name());
        productEventLog.setProductId(productEvent.getProductId());
        productEventLog.setUsername(productEvent.getUsername());
        productEventLog.setTimestamp(timestamp);
        productEventLog.setTtl(Instant.now().plus(Duration.ofMinutes(10)).getEpochSecond());

        return productEventLog;
    }
}
