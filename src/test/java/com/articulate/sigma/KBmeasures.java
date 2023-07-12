package com.articulate.sigma;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KBmeasures extends IntegrationTestBase {

    @Test
    public void testTermDepth1() {

        KB kb = KBmanager.getMgr().getKB("SUMO");
        assertThat(kb.termDepth("AudioRecorder") > kb.termDepth("Device")).isTrue();
    }

    @Test
    public void testTermDepth2() {

        KB kb = KBmanager.getMgr().getKB("SUMO");
        assertThat(kb.compareTermDepth("AudioRecorder", "Device")).isEqualTo(1);
    }

    @Test
    public void testTermDepth3() {

        KB kb = KBmanager.getMgr().getKB("SUMO");
        assertThat(kb.termDepth("VacuumCleaner") > kb.termDepth("Device")).isTrue();
    }

    @Test
    public void testTermDepth4() {

        KB kb = KBmanager.getMgr().getKB("SUMO");
        assertThat(kb.compareTermDepth("VacuumCleaner", "Device")).isEqualTo(1);
    }
}
