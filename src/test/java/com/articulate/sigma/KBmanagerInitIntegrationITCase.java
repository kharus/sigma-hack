package com.articulate.sigma;

import com.articulate.sigma.utils.FileUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("com.articulate.sigma.MidLevel")
@ActiveProfiles("MidLevel")
@Import(KBmanagerTestConfiguration.class)
public class KBmanagerInitIntegrationITCase {
    private KB kb;

    @Autowired
    private KBmanager kbManager;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(kbManager.getPref("sumokbname"));
    }
    private static final Set<String> kifSet = Sets.newHashSet();

    @BeforeAll
    public static void setKB() {

        kifSet.add("SUMO_Cache.kif");
        kifSet.add("Merge.kif");
        kifSet.add("Mid-level-ontology.kif");
    }

    /**
     * Verify that you are running your tests with the expected configuration.
     */
    @Test
    @Disabled
    public void testNbrKifFilesLoaded() {

        Set<String> expectedKifFiles = Sets.newHashSet(kifSet);
        Set<String> actualKifFiles = new HashSet<>();
        for (String s : kb.constituents)
            actualKifFiles.add(FileUtil.noPath(s));
        System.out.println("testNbrKifFilesLoaded: actual: " + actualKifFiles);
        System.out.println("testNbrKifFilesLoaded: expected: " + expectedKifFiles);
        //filterExpectedKifs(actualKifFiles, expectedKifFiles);
        assertThat(actualKifFiles.size() > 2).isTrue();
        for (String f : expectedKifFiles) {
            System.out.println("testNbrKifFilesLoaded: contains: " + f + " : " + actualKifFiles.contains(f));
            assertThat(actualKifFiles.contains(f)).isTrue();
        }
    }

    private void filterExpectedKifs(List<String> actualKifFiles, Set<String> expectedKifFiles) {

        List<String> remainingActualKifFiles = Lists.newArrayList(actualKifFiles);
        for (String file : actualKifFiles) {
            String fileName = file.substring(file.lastIndexOf("/") + 1);
            if (kifSet.contains(fileName)) {
                remainingActualKifFiles.remove(file);
                expectedKifFiles.remove(fileName);
            } else if (fileName.startsWith("SUMO_")) {
                // Remove kif knowledge added after initialization--the cache as well as Interpreter assertions.
                remainingActualKifFiles.remove(file);
            }
        }
        actualKifFiles.clear();
        actualKifFiles.addAll(remainingActualKifFiles);
    }
}