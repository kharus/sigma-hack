package com.articulate.sigma;

import com.articulate.sigma.utils.FileUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("com.articulate.sigma.MidLevel")
public class KBmanagerInitIntegrationITCase extends IntegrationTestBase {

    private static final Set<String> kifSet = Sets.newHashSet();

    @BeforeAll
    public static void setKB() {

        //kifSet.add("ArabicCulture.kif");
        //kifSet.add("Biography.kif");
        //kifSet.add("Cars.kif");
        //kifSet.add("Catalog.kif");
        //kifSet.add("Communications.kif");
        //kifSet.add("CountriesAndRegions.kif");
        //kifSet.add("Dining.kif");
        //kifSet.add("Economy.kif");
        //kifSet.add("engineering.kif");
        kifSet.add("SUMO_Cache.kif");
        //kifSet.add("FinancialOntology.kif");
        //kifSet.add("domainEnglishFormat.kif");
        //kifSet.add("english_format.kif");
        //kifSet.add("Food.kif");
        //kifSet.add("Geography.kif");
        //kifSet.add("Government.kif");
        //kifSet.add("Hotel.kif");
        //kifSet.add("Justice.kif");
        //kifSet.add("Languages.kif");
        //kifSet.add("Media.kif");
        kifSet.add("Merge.kif");
        kifSet.add("Mid-level-ontology.kif");
        //kifSet.add("Military.kif");
        //kifSet.add("MilitaryDevices.kif");
        //kifSet.add("MilitaryPersons.kif");
        //kifSet.add("MilitaryProcesses.kif");
        //kifSet.add("Music.kif");
        //kifSet.add("naics.kif");
        //kifSet.add("People.kif");
        //kifSet.add("QoSontology.kif");
        //kifSet.add("Sports.kif");
        //kifSet.add("TransnationalIssues.kif");
        //kifSet.add("Transportation.kif");
        //kifSet.add("TransportDetail.kif");
        //kifSet.add("UXExperimentalTerms.kif");
        //kifSet.add("VirusProteinAndCellPart.kif");
        //kifSet.add("WMD.kif");
    }

    /**
     * Verify that you are running your tests with the expected configuration.
     */
    @Test
    @Disabled
    public void testNbrKifFilesLoaded() {

        Set<String> expectedKifFiles = Sets.newHashSet(kifSet);
        Set<String> actualKifFiles = new HashSet<>();
        for (String s : SigmaTestBase.kb.constituents)
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