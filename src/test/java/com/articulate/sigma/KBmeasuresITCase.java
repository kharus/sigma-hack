package com.articulate.sigma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("com.articulate.sigma.MidLevel")
@ActiveProfiles("MidLevel")
@Import(KBmanagerTestConfiguration.class)
public class KBmeasuresITCase {

    private KB kb;

    @Autowired
    private KBmanager kbManager;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(kbManager.getPref("sumokbname"));
    }

    @Test
    public void testTermDepth1() {

        assertThat(kb.termDepth("AudioRecorder") > kb.termDepth("Device")).isTrue();
    }

    @Test
    public void testTermDepth2() {

        assertThat(kb.compareTermDepth("AudioRecorder", "Device")).isEqualTo(1);
    }

    @Test
    @Disabled("requires HouseholdAppliances.kif")
    public void testTermDepth3() {

        assertThat(kb.termDepth("VacuumCleaner") > kb.termDepth("Device")).isTrue();
    }

    @Test
    @Disabled("requires HouseholdAppliances.kif")
    public void testTermDepth4() {

        assertThat(kb.compareTermDepth("VacuumCleaner", "Device")).isEqualTo(1);
    }
}
