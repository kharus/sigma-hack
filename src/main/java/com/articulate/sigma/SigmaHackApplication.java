package com.articulate.sigma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(KBConfigProperties.class)
public class SigmaHackApplication {

    public static void main(String[] args) {
        SpringApplication.run(SigmaHackApplication.class, args);
    }

    @Bean
    public KBmanager kbManager(KBConfigProperties kbConfigProperties) {
        KBmanager mgr = KBmanager.getMgr();
        mgr.setKbConfigProperties(kbConfigProperties);
        mgr.initializeOnce();
        return mgr;
    }
}
