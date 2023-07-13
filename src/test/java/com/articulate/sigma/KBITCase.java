package com.articulate.sigma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("com.articulate.sigma.TopOnly")
@ActiveProfiles("TopOnly")
@Import(KBmanagerTestConfiguration.class)
public class KBITCase {

    private KB kb;

    @Autowired
    private KBmanager kbManager;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(kbManager.getPref("sumokbname"));
    }
    @Test
    public void testMostSpecificTerm() {

        String t = kb.mostSpecificTerm(List.of("Entity", "RealNumber"));
        System.out.println("testMostSpecificTerm(): " + t);
        assertThat(t).isEqualTo("RealNumber");
    }

    @Test
    public void testAskWithTwoRestrictionsDirect1() {

        List<Formula> actual = kb.askWithTwoRestrictions(0, "subclass", 1, "Driving", 2, "Guiding");
        assertThat(actual.size()).isNotEqualTo(0);
    }

    /**
     * Fails because askWithTwoRestrictions does not go up the class hierarchy but if caching is on will get "1".
     */
    @Test
    public void testAskWithTwoRestrictionsIndirect1() {

        List<Formula> actual = kb.askWithTwoRestrictions(0, "subclass", 1, "Driving", 2, "Guiding");

        assertThat(actual.size()).isEqualTo(1);
    }

    /**
     * Fails because askWithTwoRestrictions does not go up the class hierarchy.
     */
    @Test
    @Disabled
    public void testAskWithTwoRestrictionsIndirect2() {

        List<Formula> actual = kb.askWithTwoRestrictions(0, "subclass", 1, "Boy", 2, "Entity");
        assertThat(actual.size()).isEqualTo(0);
    }

    @Test
    public void testIsSubclass2() {
        assertThat(kb.isSubclass("Driving", "Process")).isTrue();
    }

    @Test
    public void testRemoveSuperClassesEmptyInput() {

        Set<String> actualSet = kb.removeSuperClasses(Set.of());
        assertThat(actualSet).isEqualTo(Set.of());
    }

    @Test
    public void testRemoveSuperClassesOneElementInput() {

        Set<String> inputSet = Set.of("nonsenseWord");
        Set<String> actualSet = kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Set.of("nonsenseWord");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void testRemoveSuperClassesTwoElementIdenticalInput1() {

        Set<String> inputSet = Set.of("Entity");
        Set<String> actualSet = kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Set.of("Entity");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void testRemoveSuperClassesTwoElementIdenticalInput2() {

        Set<String> inputSet = Set.of("Process");
        Set<String> actualSet = kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Set.of("Process");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void testRemoveSuperClassesTwoElementIdenticalInput3() {

        Set<String> inputSet = Set.of("Physical");
        Set<String> actualSet = kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Set.of("Physical");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void testRemoveSuperClassesTwoElementInput() {

        Set<String> inputSet = Set.of("Man", "Human");
        Set<String> actualSet = kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Set.of("Man");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void testRemoveSuperClassesTwoElementInputReverse() {

        Set<String> inputSet = Set.of("Human", "Man");
        Set<String> actualSet = kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Set.of("Man");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void testRemoveSuperClassesTwoElementInputNoSubclass() {

        Set<String> inputSet = Set.of("Man", "Woman");
        Set<String> actualSet = kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Set.of("Man", "Woman");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void testRemoveSuperClassesFiveElementInput() {

        Set<String> inputSet = Set.of("Object", "CorpuscularObject", "Woman", "Human", "Man");
        Set<String> actualSet = kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Set.of("Man", "Woman");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

}