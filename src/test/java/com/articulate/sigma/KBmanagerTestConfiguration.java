package com.articulate.sigma;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.io.*;

import static com.articulate.sigma.SigmaTestBase.checkConfiguration;

@TestConfiguration
public class KBmanagerTestConfiguration {
    @Bean
    @Profile("TopOnly")
    public KBmanager kbManager(KBConfigProperties kbConfigProperties) {
        System.out.println("KBmanagerTestConfiguration.kbManager");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config_topOnly.xml");
             Reader reader = new BufferedReader(new InputStreamReader(is))) {

            if (!KBmanager.initialized) {
                SimpleDOMParser sdp = new SimpleDOMParser();
                SimpleElement configuration = sdp.parse(reader);

                KBmanager.getMgr().setKbConfigProperties(kbConfigProperties);
                KBmanager.getMgr().setDefaultAttributes();
                KBmanager.getMgr().setConfiguration(configuration);
                KBmanager.initialized = true;
            }
            checkConfiguration();
            return KBmanager.getMgr();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
