package org.computaceae.ticketing;

import java.util.Optional;
import org.computaceae.ticketing.component.DataRefreshComponent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableFeignClients(value = {"org.computaceae.lib.core"})
@SpringBootApplication(scanBasePackages = {"org.computaceae.ticketing", "org.computaceae.lib"})
public class CitTicketingApplication {

  public static void main(String[] args) {
    SpringApplication.run(CitTicketingApplication.class, args);
  }

  @Autowired(required = false)
  private DataRefreshComponent dataRefreshComponent;

  @Bean
  synchronized InitializingBean sendResquestOnStartUp() {
    return () -> {
      if (Optional.ofNullable(this.dataRefreshComponent).isPresent()) {
        this.dataRefreshComponent.refreshUsersOnStartup();
      }
    };
  }

}
