package com.articulate.sigma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("com.articulate.sigma.MidLevel")
@ActiveProfiles("MidLevel")
@Import(KBmanagerTestConfiguration.class)
public class CaseRoleITCase {

    private KB kb;

    @Autowired
    private KBmanager kbManager;

    @Value("${sumokbname}")
    private String sumokbname;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(sumokbname);
    }

    /**
     * This test is meant to detect errors in the writing of SUMO rules in kif files. It fails if it finds a case where a term
     * is declared to be a subrelation of another term that is a CaseRole, but the first term is not explicitly declared to be an
     * instance of CaseRole. For example, "(subrelation standardInputDevice instrument)" by itself will fail because instrument
     * is a CaseRole, but we haven't explicitly said standardInputDevice is a CaseRole. The test will pass if the kif file reads
     * "(subrelation standardInputDevice instrument) /n (instance standardInputDevice CaseRole)".
     */
    @Test
    public void testCaseRole() {

        KBcache cache = kb.kbCache;

        // Collect all expected instances for "CaseRole", by running KBcache.buildTransInstOf()
        cache.instanceOf = new HashMap<>();
        cache.buildTransInstOf();
        Map<String, Set<String>> expectedInstancesMap = cache.instanceOf;
        TreeSet<String> expectedInstancesForCaseRole = new TreeSet<>();
        for (String inst : expectedInstancesMap.keySet()) {
            Set<String> parentClasses = expectedInstancesMap.get(inst);
            if (parentClasses.contains("CaseRole"))
                expectedInstancesForCaseRole.add(inst);
        }

        // Collect all actual instances for "CaseRole", by running KBcache.buildDirectInstances()
        cache.instanceOf = new HashMap<>();
        cache.buildDirectInstances();
        Map<String, Set<String>> actualInstancesMap = cache.instanceOf;
        TreeSet<String> actualInstancesForCaseRole = new TreeSet<>();
        for (String inst : actualInstancesMap.keySet()) {
            Set<String> parentClasses = actualInstancesMap.get(inst);
            if (parentClasses.contains("CaseRole"))
                actualInstancesForCaseRole.add(inst);
        }

        assertThat(actualInstancesForCaseRole).isEqualTo(expectedInstancesForCaseRole);
    }
}
