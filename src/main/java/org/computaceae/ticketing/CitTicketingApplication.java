package org.computaceae.ticketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
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

}
