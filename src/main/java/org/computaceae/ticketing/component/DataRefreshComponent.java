package org.computaceae.ticketing.component;

import org.computaceae.lib.core.message.TicketingMessage;
import org.computaceae.ticketing.config.RabbitDispatcherConfig;
import org.computaceae.ticketing.service.TicketingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DataRefreshComponent {

  private static Logger log = LoggerFactory.getLogger(DataRefreshComponent.class);

  @Autowired
  private RabbitDispatcherConfig rabbitDispatcherConfig;

  @Autowired
  private TicketingService ticketingService;

  @Async
  public synchronized void refreshUsersOnStartup() {

    try {
      this.wait(120000); // 2 minutes
    } catch (InterruptedException ex) {
      log.error(ex.getMessage(), ex);
      Thread.currentThread().interrupt();
    }
    this.rabbitDispatcherConfig.sendTicketing(new TicketingMessage(true));


    try {
      this.wait(120000); // 2 minutes
    } catch (InterruptedException ex) {
      log.error(ex.getMessage(), ex);
      Thread.currentThread().interrupt();
    }
    this.ticketingService.getLastUpdatedIssues();
  }

}
