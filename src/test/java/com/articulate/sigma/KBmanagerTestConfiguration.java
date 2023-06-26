package com.articulate.sigma;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.*;

import static com.articulate.sigma.SigmaTestBase.checkConfiguration;

@TestConfiguration
class KBmanagerTestConfiguration {
    @Bean
    public KBmanager topOnlyKBManager() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config_topOnly.xml");
             Reader reader = new BufferedReader(new InputStreamReader(is))) {

            if (!KBmanager.initialized) {
                SimpleDOMParser sdp = new SimpleDOMParser();
                SimpleElement configuration = sdp.parse(reader);

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
