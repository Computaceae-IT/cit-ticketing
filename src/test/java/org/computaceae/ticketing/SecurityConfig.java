package org.computaceae.ticketing;

import org.computaceae.lib.security.component.AuthenticationFacade;
import org.computaceae.lib.security.component.AuthenticationFacadeImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile({"test-with-mocked-security"})
public class SecurityConfig {

  @Bean
  public AuthenticationFacade authenticationFacade() {
    return new AuthenticationFacadeImpl();
  }

}
