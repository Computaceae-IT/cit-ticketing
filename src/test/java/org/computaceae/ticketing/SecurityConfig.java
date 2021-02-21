package org.computaceae.ticketing;

import org.computaceae.lib.security.AuthenticationSuccessHandler;
import org.computaceae.lib.security.JwtAuthEntryPoint;
import org.computaceae.lib.security.TokenHelper;
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

  @Bean
  public AuthenticationSuccessHandler authenticationSuccessHandler() {
    return new AuthenticationSuccessHandler();
  }

  @Bean
  public JwtAuthEntryPoint jwtAuthEntryPoint() {
    return new JwtAuthEntryPoint();
  }

  @Bean
  public TokenHelper tokenHelper() {
    return new TokenHelper("SEED");
  }

}
