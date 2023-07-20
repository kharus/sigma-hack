package com.articulate.sigma;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties
public class KBConfigProperties {
    private Path sigmaHome;

    private Path baseDir;

    private Path kbDir;

    public Path getSigmaHome() {
        return sigmaHome;
    }

    public void setSigmaHome(Path sigmaHome) {
        this.sigmaHome = sigmaHome;
    }

    public Path getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(Path baseDir) {
        this.baseDir = baseDir;
    }

    public Path getKbDir() {
        return kbDir;
    }

    public void setKbDir(Path kbDir) {
        this.kbDir = kbDir;
    }
}