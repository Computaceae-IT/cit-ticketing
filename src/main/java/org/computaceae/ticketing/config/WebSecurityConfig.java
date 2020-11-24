package org.computaceae.ticketing.config;


import org.computaceae.lib.config.AbstractWebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends AbstractWebSecurityConfig {

  public WebSecurityConfig() {
    super();
  }

}
