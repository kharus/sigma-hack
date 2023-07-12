package com.articulate.sigma;

import com.articulate.sigma.trans.SUMOKBtoTPTPKBRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.command.annotation.EnableCommand;

@SpringBootApplication
@EnableCommand(SUMOKBtoTPTPKBRunner.class)
@EnableConfigurationProperties(KBConfigProperties.class)
public class SigmaHackApplication {

    public static void main(String[] args) {
        SpringApplication.run(SigmaHackApplication.class, args);
    }

    @Bean
    @Profile("prod")
    public KBmanager kbManager(KBConfigProperties kbConfigProperties) {
        KBmanager mgr = KBmanager.getMgr();
        mgr.setKbConfigProperties(kbConfigProperties);
        mgr.initializeOnce();
        return mgr;
    }
}
