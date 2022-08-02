package org.computaceae.ticketing.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import com.rabbitmq.http.client.Client;

@Configuration
@PropertySource(value = "rabbit-config.properties", ignoreResourceNotFound = true)
public class RabbitConfig {

  private static Logger log = LoggerFactory.getLogger(RabbitConfig.class);

  private final List<String> queue_names = new ArrayList<String>();
  protected final String queue_instance;

  public static String FANOUT_EXCHANGE;

  @Autowired
  private AmqpAdmin admin;

  public RabbitConfig(@Value("${com.botalista.rabbitmq.queue.instance}") String queue_instance,
      @Value("${spring.rabbitmq.host}") String host,
      @Value("${spring.rabbitmq.username}") String username,
      @Value("${spring.rabbitmq.password}") String password) {
    this.queue_instance = queue_instance;
    FANOUT_EXCHANGE = "CIT_TICKETING.FANOUT_EXCHANGE." + queue_instance;

    try {
      Client client = new Client("http://" + host + ":15672/api/", username, password);
      client.getConnections();
      client.getQueues().forEach((q) -> {



        switch (queue_instance) {
          default:
          case "LOCAL":
            if (Optional.ofNullable(q.getName()).orElse("").contains("LOCAL.")
                && Optional.ofNullable(q.getName()).orElse("").endsWith(".BOTA_ORCHECTRATOR")) {
              this.queue_names.add(q.getName());
              log.info(q.toString());
            }
            break;
          case "DEV":
            if (Optional.ofNullable(q.getName()).orElse("").contains("DEV.")
                && Optional.ofNullable(q.getName()).orElse("").endsWith(".BOTA_ORCHECTRATOR")) {
              this.queue_names.add(q.getName());
              log.info(q.toString());
            }
            break;
          case "PRD":
            if (!Optional.ofNullable(q.getName()).orElse("").contains("DEV.")
                && !Optional.ofNullable(q.getName()).orElse("").contains("LOCAL.")
                && !Optional.ofNullable(q.getName()).orElse("").contains("PROGRESSIO.")
                && Optional.ofNullable(q.getName()).orElse("").endsWith(".BOTA_ORCHECTRATOR")) {
              this.queue_names.add(q.getName());
              log.info(q.toString());
            }
            break;
        }
      });
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

  }

  @Bean
  List<Queue> sendingQueue() {
    // @formatter:off
    return this.queue_names.stream()
        .map(q -> 
          QueueBuilder.durable(q)
          .build())
        .collect(Collectors.toList());
    // @formatter:on
  }

  @Bean
  Exchange fanoutExchange() {
    return new FanoutExchange(FANOUT_EXCHANGE);
  }

  @Bean
  List<Binding> bindingFanoutExchanges(final List<Queue> sendingQueue,
      final Exchange fanoutExchange) {
    // @formatter:off
    return sendingQueue.stream()
        .map(q -> {
          Binding bf = bindingFanoutExchange(q,fanoutExchange);
          admin.declareBinding(bf);
          return bf;
        })
        .collect(Collectors.toList());
    // @formatter:on
  }

  Binding bindingFanoutExchange(final Queue sendingQueue, final Exchange fanoutExchange) {
    return BindingBuilder.bind(sendingQueue).to(fanoutExchange)
        .with("org.computaceae.ticketing.entity.#").noargs();
  }

}
