package org.computaceae.ticketing.config;


import org.computaceae.lib.config.AbstractWebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends AbstractWebSecurityConfig {

  public WebSecurityConfig() {
    super();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.exceptionHandling().authenticationEntryPoint(jwtAuthEntryPoint).and().formLogin()
        .successHandler(authenticationSuccessHandler);

    // @formatter:off
    http.authorizeRequests()
    .antMatchers("/actuator/**","/internum/**")
    .access("hasIpAddress('192.168.0.0/16') or "
          + "hasIpAddress('127.0.0.1/32') or "
          + "hasIpAddress('0:0:0:0:0:0:0:1') or "
          + "hasIpAddress('193.134.187.0/24') or "
          + "hasIpAddress('51.38.234.38') or "
          + "hasIpAddress('135.125.0.55') or "
          + "hasIpAddress('146.59.253.176') or "
          + "hasIpAddress('135.125.8.62') or "
          + "hasIpAddress('146.59.149.163') or "
          + "hasIpAddress('51.255.91.114')")
    .anyRequest().authenticated();

    // Session management
    http.sessionManagement()
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    // Custom JWT based security filter
    http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    http.csrf().disable();
    http.addFilterBefore(corsFilter(), ChannelProcessingFilter.class);
    // @formatter:on

  }


}
