package org.computaceae.ticketing.config;

import org.computaceae.lib.core.message.TicketingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Component
public class RabbitDispatcherConfig {

  private static Logger log = LoggerFactory.getLogger(RabbitDispatcherConfig.class);

  private final RabbitTemplate rabbitTemplate;

  public RabbitDispatcherConfig(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendTicketing(TicketingMessage message) {
    log.debug("Sending message to all orchestration... " + message.getInfo());

    rabbitTemplate.convertAndSend(RabbitConfig.FANOUT_EXCHANGE,
        "org.computaceae.ticketing.entity.#", message);
  }



}
