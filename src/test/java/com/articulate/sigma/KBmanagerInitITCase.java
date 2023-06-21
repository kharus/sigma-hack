package com.articulate.sigma;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KBmanagerInitITCase extends UnitTestBase {

    /**
     * Help verify that the correct config file is being run by checking how many kif files have been loaded. You could think
     * you're running with a new config file when you are not by, for example, modifying the test config file without
     * doing a full build (which puts the new config file into the build output directory). This test isn't great, but it
     * may save developers time when they encounter unexpected results.
     */
    @Test
    public void testNbrKifFilesLoaded() {

        int expected = UnitTestBase.NUM_KIF_FILES;
        int actual = SigmaTestBase.kb.constituents.size();
        assertEquals(expected, actual);
    }

}