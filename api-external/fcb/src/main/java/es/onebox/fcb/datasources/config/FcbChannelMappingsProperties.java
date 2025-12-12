package es.onebox.fcb.datasources.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("fcb.channels")
public class FcbChannelMappingsProperties extends HashMap<String, String> {

}
