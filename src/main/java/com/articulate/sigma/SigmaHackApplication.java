package com.articulate.sigma;

import com.articulate.sigma.trans.SUMOKBtoTPTPKB;
import com.articulate.sigma.utils.StringUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

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
