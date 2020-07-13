package org.computaceae;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@TestConfiguration
@Profile({"test"})
public class TestConfig {

  @Bean
  public FreeMarkerConfigurer freeMarkerConfigurer() {
    FreeMarkerConfigurer freemarkerConfig = new FreeMarkerConfigurer();
    freemarkerConfig.setTemplateLoaderPath("template");
    freemarkerConfig.setDefaultEncoding("UTF-8");

    Map<String, Object> freemarkerVariables = new HashMap<String, Object>();

    freemarkerConfig.setFreemarkerVariables(freemarkerVariables);
    return freemarkerConfig;
  }

}
