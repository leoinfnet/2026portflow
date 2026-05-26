package br.com.infnet.terminalService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;
@Configuration
public class ObjectMapperConfig {
    @Bean
    public ObjectMapper getMapper(){
        return new ObjectMapper();
    }
}
