package com.articulate.sigma;

import com.articulate.sigma.trans.SUMOKBtoTPTPKBRunner;
import com.articulate.sigma.wordnet.WordNet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.command.annotation.EnableCommand;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@EnableCommand({SUMOKBtoTPTPKBRunner.class, KBRunner.class})
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

    @Bean
    public WordNet wordNet(KBConfigProperties kbConfigProperties) {
        WordNet.baseDir = kbConfigProperties.getKbDir().resolve("WordNetMappings").toString();
        WordNet.baseDirFile = new File(WordNet.baseDir);
        if (!WordNet.serializedExists()) {
            WordNet.loadFresh();
            WordNet.initNeeded = false;
        } else {
            WordNet.loadSerialized();
            if (WordNet.wn == null)
                WordNet.loadFresh();
        }
        DB.readSentimentArray(kbConfigProperties.getKbDir());

        return WordNet.wn;
    }
}
