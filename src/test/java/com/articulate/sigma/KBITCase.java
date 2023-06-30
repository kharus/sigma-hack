package com.articulate.sigma;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Category(TopOnly.class)
public class KBITCase extends UnitTestBase {

    @Test
    public void testMostSpecificTerm() {

        String t = SigmaTestBase.kb.mostSpecificTerm(Arrays.asList("Entity", "RealNumber"));
        System.out.println("testMostSpecificTerm(): " + t);
        assertThat(t).isEqualTo("RealNumber");
    }

    @Test
    public void testAskWithTwoRestrictionsDirect1() {

        List<Formula> actual = SigmaTestBase.kb.askWithTwoRestrictions(0, "subclass", 1, "Driving", 2, "Guiding");
        assertThat(actual.size()).isNotEqualTo(0);
    }

    /**
     * Fails because askWithTwoRestrictions does not go up the class hierarchy but if caching is on will get "1".
     */
    @Test
    public void testAskWithTwoRestrictionsIndirect1() {

        List<Formula> actual = SigmaTestBase.kb.askWithTwoRestrictions(0, "subclass", 1, "Driving", 2, "Guiding");
        if (actual != null && actual.size() != 0)
            System.out.println("KBtest.testAskWithTwoRestrictionsIndirect1(): " + actual);
        assertThat(actual.size()).isEqualTo(1);
    }

    /**
     * Fails because askWithTwoRestrictions does not go up the class hierarchy.
     */
    @Test
    public void testAskWithTwoRestrictionsIndirect2() {

        List<Formula> actual = SigmaTestBase.kb.askWithTwoRestrictions(0, "subclass", 1, "Boy", 2, "Entity");
        assertThat(actual.size()).isEqualTo(0);
    }

    @Test
    public void testIsSubclass2() {
        assertThat(SigmaTestBase.kb.isSubclass("Driving", "Process")).isTrue();
    }

    @Test
    public void testRemoveSuperClassesEmptyInput() {

        Set<String> inputSet = Sets.newHashSet();
        Set<String> actualSet = SigmaTestBase.kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Sets.newHashSet();
        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void testRemoveSuperClassesOneElementInput() {

        Set<String> inputSet = Sets.newHashSet("nonsenseWord");
        Set<String> actualSet = SigmaTestBase.kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Sets.newHashSet("nonsenseWord");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void testRemoveSuperClassesTwoElementIdenticalInput1() {

        Set<String> inputSet = Sets.newHashSet("Entity", "Entity");
        Set<String> actualSet = SigmaTestBase.kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Sets.newHashSet("Entity");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void testRemoveSuperClassesTwoElementIdenticalInput2() {

        Set<String> inputSet = Sets.newHashSet("Process", "Process");
        Set<String> actualSet = SigmaTestBase.kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Sets.newHashSet("Process");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void testRemoveSuperClassesTwoElementIdenticalInput3() {

        Set<String> inputSet = Sets.newHashSet("Physical", "Physical");
        Set<String> actualSet = SigmaTestBase.kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Sets.newHashSet("Physical");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void testRemoveSuperClassesTwoElementInput() {

        Set<String> inputSet = Sets.newHashSet("Man", "Human");
        Set<String> actualSet = SigmaTestBase.kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Sets.newHashSet("Man");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void testRemoveSuperClassesTwoElementInputReverse() {

        Set<String> inputSet = Sets.newHashSet("Human", "Man");
        Set<String> actualSet = SigmaTestBase.kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Sets.newHashSet("Man");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void testRemoveSuperClassesTwoElementInputNoSubclass() {

        Set<String> inputSet = Sets.newHashSet("Man", "Woman");
        Set<String> actualSet = SigmaTestBase.kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Sets.newHashSet("Man", "Woman");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void testRemoveSuperClassesFiveElementInput() {

        Set<String> inputSet = Sets.newHashSet("Object", "CorpuscularObject", "Woman", "Human", "Man");
        Set<String> actualSet = SigmaTestBase.kb.removeSuperClasses(inputSet);
        Set<String> expectedSet = Sets.newHashSet("Man", "Woman");
        assertThat(actualSet).isEqualTo(expectedSet);
    }

}