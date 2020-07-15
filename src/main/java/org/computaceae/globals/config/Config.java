package org.computaceae.globals.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({@PropertySource(value = "config.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "file:/app/properties/config.properties",
        ignoreResourceNotFound = true)})
public class Config {

}
