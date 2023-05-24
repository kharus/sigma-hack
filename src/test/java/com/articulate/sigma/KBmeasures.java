package com.articulate.sigma;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KBmeasures extends IntegrationTestBase {

        @Test
    public void testTermDepth1() {

        KB kb = KBmanager.getMgr().getKB("SUMO");
        assertTrue(kb.termDepth("AudioRecorder") > kb.termDepth("Device"));
    }

        @Test
    public void testTermDepth2() {

        KB kb = KBmanager.getMgr().getKB("SUMO");
        assertEquals(1, kb.compareTermDepth("AudioRecorder", "Device"));
    }

        @Test
    public void testTermDepth3() {

        KB kb = KBmanager.getMgr().getKB("SUMO");
        assertTrue(kb.termDepth("VacuumCleaner") > kb.termDepth("Device"));
    }

        @Test
    public void testTermDepth4() {

        KB kb = KBmanager.getMgr().getKB("SUMO");
        assertEquals(1, kb.compareTermDepth("VacuumCleaner", "Device"));
    }
}
