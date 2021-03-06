package org.computaceae.ticketing;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@TestConfiguration
@Profile({"test"})
public class ExtraConfig {

  @Bean()
  public FreeMarkerConfigurer freeMarkerConfigurer() {
    FreeMarkerConfigurer freemarkerConfig = new FreeMarkerConfigurer();

    freemarkerConfig.setTemplateLoaderPath("classpath:/templates");
    freemarkerConfig.setDefaultEncoding("UTF-8");

    return freemarkerConfig;
  }
  
}
