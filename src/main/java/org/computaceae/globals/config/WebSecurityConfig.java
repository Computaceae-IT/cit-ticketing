package org.computaceae.globals.config;


import javax.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  public WebSecurityConfig() {
    SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
  }

  @Bean
  protected Filter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("OPTIONS");
    config.addAllowedMethod("HEAD");
    config.addAllowedMethod("GET");
    config.addAllowedMethod("PUT");
    config.addAllowedMethod("POST");
    config.addAllowedMethod("DELETE");
    config.addAllowedMethod("PATCH");
    this.addExposedHeader(config);

    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
  }

  private void addExposedHeader(CorsConfiguration config) {
    config.addExposedHeader("Authorization");
    config.addExposedHeader("Content-Disposition");
    config.addExposedHeader("Content-Type");
  }

  @Bean
  protected Filter headerAllowFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    CorsConfiguration config = new CorsConfiguration();
    this.addExposedHeader(config);

    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
  }

  @Bean
  public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
    StrictHttpFirewall firewall = new StrictHttpFirewall();
    firewall.setAllowUrlEncodedPercent(true);
    firewall.setAllowSemicolon(true);
    return firewall;
  }



  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // @formatter:off
    http.authorizeRequests()
    .antMatchers("/actuator/**","/internum/**")
    .access("hasIpAddress('10.0.0.0/16') or hasIpAddress('127.0.0.1/32') or hasIpAddress('0:0:0:0:0:0:0:1') or hasIpAddress('193.134.189.0/24')")
    .anyRequest().authenticated();
    
    // Session management
    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    
    http.csrf().disable();
    http.addFilterBefore(corsFilter(), ChannelProcessingFilter.class);
    // @formatter:on

  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    super.configure(web);
    web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
  }


}
