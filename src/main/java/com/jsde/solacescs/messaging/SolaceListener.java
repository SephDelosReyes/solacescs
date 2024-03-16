package com.jsde.solacescs.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static java.lang.String.format;

@Component
public class SolaceListener {

  private static final Logger LOG = LoggerFactory.getLogger(SolaceListener.class);

  @Autowired
  private StreamBridgePublisher streamBridgePublisher;

  @Bean
  public Consumer<Message<String>> messageListener() {
    return message -> {
      LOG.info("Listening to Solace test-consumer-queue-1");
      LOG.info(
          format("Message received with Headers: %s and Payload: %s",
              message.getHeaders(), message.getPayload()));
      Message<String> processedMessage =
          MessageBuilder.withPayload("Message Processed by Listener")
              .copyHeaders(message.getHeaders()).build();
      streamBridgePublisher.sendToExternalService("sending to 3rd party");
      LOG.info(
          format("Publishing message to test-publisher-topic-1 " +
                  "with Headers: %s and Payload: %s",
              processedMessage.getHeaders(), processedMessage.getPayload()));
      streamBridgePublisher.sendToDownstreamService(processedMessage);
    };
  }

}
