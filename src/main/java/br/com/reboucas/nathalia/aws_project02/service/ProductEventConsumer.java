package br.com.reboucas.nathalia.aws_project02.service;

import br.com.reboucas.nathalia.aws_project02.model.Envelope;
import br.com.reboucas.nathalia.aws_project02.model.ProductEvent;
import br.com.reboucas.nathalia.aws_project02.model.SnsMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductEventConsumer {
    private final ObjectMapper objectMapper;

    @JmsListener(destination = "${aws.sqs.queue.product.events.name}")
    public void receiverProductEvent(TextMessage textMessage) throws JMSException, JsonProcessingException {
        SnsMessage snsMessage = objectMapper.readValue(textMessage.getText(), SnsMessage.class);
        Envelope envelope = objectMapper.readValue(snsMessage.getMessage(), Envelope.class);
        ProductEvent productEvent = objectMapper.readValue(envelope.getData(), ProductEvent.class);
        log.info("Product event received - Event: {} - ProductId: {} - MessageId: {}",
                envelope.getEventType(),
                productEvent.getProductId(),
                snsMessage.getMessageId());
    }
}
