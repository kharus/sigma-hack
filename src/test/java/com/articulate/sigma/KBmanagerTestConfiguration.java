package com.articulate.sigma;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.io.*;
import java.nio.file.Path;


@TestConfiguration
public class KBmanagerTestConfiguration {
    @Bean
    @Profile("TopOnly")
    public KBmanager kbManagerTop(KBConfigProperties kbConfigProperties) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config_topOnly.xml");
             Reader reader = new BufferedReader(new InputStreamReader(is))) {

            SimpleDOMParser sdp = new SimpleDOMParser();
            SimpleElement configuration = sdp.parse(reader);

            Path base = kbConfigProperties.getSigmaHome();
            String configFileDir = base.resolve("KBs").toString();

            KBmanager.getMgr().initializeOnce(configFileDir, configuration);

            return KBmanager.getMgr();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Profile("MidLevel")
    public KBmanager kbManagerMilo(KBConfigProperties kbConfigProperties) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config_topAndMid.xml");
             Reader reader = new BufferedReader(new InputStreamReader(is))) {

            SimpleDOMParser sdp = new SimpleDOMParser();
            SimpleElement configuration = sdp.parse(reader);

            Path base = kbConfigProperties.getSigmaHome();
            String configFileDir = base.resolve("KBs").toString();

            KBmanager.getMgr().initializeOnce(configFileDir, configuration);

            return KBmanager.getMgr();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
