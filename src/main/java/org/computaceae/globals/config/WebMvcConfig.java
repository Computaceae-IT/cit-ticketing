package org.computaceae.globals.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    for (HttpMessageConverter<?> converter : converters) {
      if (converter instanceof org.springframework.http.converter.json.MappingJackson2HttpMessageConverter) {
        ObjectMapper mapper = ((MappingJackson2HttpMessageConverter) converter).getObjectMapper();
        mapper.setSerializationInclusion(Include.NON_EMPTY);
      }
    }
  }
}
