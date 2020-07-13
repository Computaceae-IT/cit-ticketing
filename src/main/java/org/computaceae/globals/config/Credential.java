package org.computaceae.globals.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({@PropertySource(value = "credentials.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "file:/app/properties/credentials.properties",
        ignoreResourceNotFound = true)})
public class Credential {

}
