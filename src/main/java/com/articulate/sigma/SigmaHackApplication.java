package com.articulate.sigma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SigmaHackApplication {

    public static void main(String[] args) {
        SpringApplication.run(SigmaHackApplication.class, args);
    }

    @Bean
    public KBmanager kbManager() {
        KBmanager mgr = KBmanager.getMgr();
        mgr.initializeOnce();
        return mgr;
    }
}
