package org.computaceae;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableFeignClients(value = {})
@SpringBootApplication(scanBasePackages = {"org.computaceae"})
public class CitTicketingApplication {

  public static void main(String[] args) {
    SpringApplication.run(CitTicketingApplication.class, args);
  }

}
