package com.articulate.sigma;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties
public class KBConfigProperties {
    private Path sigmaHome;

    public Path getSigmaHome() {
        return sigmaHome;
    }

    public void setSigmaHome(Path sigmaHome) {
        this.sigmaHome = sigmaHome;
    }
}
