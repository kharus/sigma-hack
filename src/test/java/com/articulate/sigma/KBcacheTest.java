package com.articulate.sigma;

/**
 * Note that this class, and therefore, Sigma, depends upon several terms
 * being present in the ontology in order to function as intended.  They are:
 * <p>
 * subclass
 * subAttribute
 * subrelation
 * instance
 * <p>
 * partition
 * disjoint
 * disjointDecomposition
 * exhaustiveDecomposition
 * exhaustiveAttribute
 * <p>
 * domain
 * domainSubclass
 * Entity
 * TransitiveRelation
 * Relation
 */

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class KBcacheTest {

    public static KB kb = new KB("TestKB");

    @BeforeAll
    public static void setup() {

        kb.kbCache = new KBcache(kb);
        KBmanager.getMgr().setPref("cacheDisjoint", "true");
        KIF kif = new KIF();
        kif.parseStatement("(subAttribute Attorney Lawyer)");
        kif.parseStatement("(instance Attorney Profession)");
        kif.parseStatement("(instance Lawyer Profession)");
        kif.parseStatement("(subclass Profession Attribute)");
        kif.parseStatement("(subclass Attribute Entity)");
        kif.parseStatement("(instance rel Relation)");
        kif.parseStatement("(instance subclass TransitiveRelation)");
        kif.parseStatement("(instance subAttribute TransitiveRelation)");
        kif.parseStatement("(instance subrelation TransitiveRelation)");
        kif.parseStatement("(instance var VariableArityRelation)");
        kif.parseStatement("(domain var 1 Object)");
        kif.parseStatement("(domain var 2 Object)");
        kif.parseStatement("(domain rel 1 Object)");
        kif.parseStatement("(domain rel 2 Object)");
        kif.parseStatement("(subclass Object Entity)");
        kif.parseStatement("(subclass Furniture Object)");
        kif.parseStatement("(subclass Table Furniture)");
        kif.parseStatement("(subclass Chair Furniture)");
        kif.parseStatement("(subclass LadderBackChair Chair)");
        kif.parseStatement("(subrelation relsub rel)");
        kif.parseStatement("(subclass TransitiveRelation Relation)");
        kif.parseStatement("(subclass VariableArityRelation Relation)");
        //kif.parseStatement("(instance relsub TransitiveRelation)");
        kif.parseStatement("(subclass Relation Entity)");
        kif.parseStatement("(subrelation CitizenryFn ResidentFn)");
        kif.parseStatement("(instance CitizenryFn Function)");
        kif.parseStatement("(instance ResidentFn Function)");
        kif.parseStatement("(subclass Function Relation)");
        kif.parseStatement("(partition Animal Vertebrate Invertebrate)");
        kif.parseStatement("(subclass Dog Vertebrate)");
        kif.parseStatement("(subclass Jellyfish Invertebrate)");
        kb.merge(kif, "");
        for (Formula f : kb.formulaMap.values())
            f.sourceFile = "test"; // without a source file kbCache assumes it's a cached formula and ignores it
        kb.kbCache.buildCaches();
        KBcache.showState(kb.kbCache);
    }

    @Test
    public void testRelations() {

        System.out.println("Test relations");
        Set<String> expected = new HashSet<>(Arrays.asList("subAttribute",
                "var", "rel", "subclass", "CitizenryFn", "ResidentFn", "relsub",
                "subrelation"));
        Set<String> actual = kb.kbCache.relations;
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testFunctions() {

        System.out.println("Test functions");
        Set<String> expected = new HashSet<>(Arrays.asList("CitizenryFn", "ResidentFn"));
        Set<String> actual = kb.kbCache.functions;
        System.out.println("functions:" + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testPredicates() {

        System.out.println("Test predicates");
        Set<String> expected = new HashSet<>(Arrays.asList("subAttribute",
                "var", "rel", "subclass", "relsub",
                "subrelation"));
        Set<String> actual = kb.kbCache.predicates;
        System.out.println("predicates:" + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void transRels() {

        System.out.println("Test transRels");
        Set<String> expected = new HashSet<>(Arrays.asList("subclass", "subAttribute", "subrelation"));
        Set<String> actual = kb.kbCache.transRels;
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testParents() {

        System.out.println("Test parents");
        Set<String> expected = new HashSet<>(Arrays.asList("Relation", "Entity"));
        Set<String> actual = kb.kbCache.getParentClassesOfInstance("rel");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testChildren() {

        System.out.println("Test children");
        Set<String> expected = new HashSet<>(List.of("relsub"));
        System.out.println("testChildren(): subrelations: " + kb.kbCache.children.get("subrelation"));
        Set<String> actual = null;
        if (kb.kbCache.children.get("subrelation") != null)
            actual = kb.kbCache.children.get("subrelation").get("rel");
        System.out.println("testChildren(): actual: " + actual);
        System.out.println("testChildren(): expected: " + expected);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testSignatures() {

        System.out.println("Test signatures");
        List<String> expected = new ArrayList<>(Arrays.asList("", "Object", "Object"));
        List<String> actual = kb.kbCache.signatures.get("rel");
        assertThat(actual.subList(1, 2)).isEqualTo(expected.subList(1, 2));
    }

    @Test
    public void testVarSignatures() {

        System.out.println("Test var signatures");
        String expected = "Object";
        System.out.println("testVarSignatures() expected: " + expected);
        String actual = kb.kbCache.variableArityType("var");
        System.out.println("testVarSignatures() actual: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testValences() {

        System.out.println("Test valences");
        int expected = 2;
        int actual = kb.kbCache.valences.get("rel");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testValences2() {

        System.out.println("Test valences 2");
        int expected = -1;
        int actual = kb.kbCache.valences.get("var");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testInsts() {

        System.out.println("Test insts");
        assertThat(kb.kbCache.insts.contains("rel")).isTrue();
    }

    @Test
    public void testInstances() {

        System.out.println("Test instances");
        Set<String> expected = new HashSet<>(Arrays.asList("subAttribute",
                "var", "rel", "relsub", "subclass",
                "subrelation", "CitizenryFn", "ResidentFn"));
        Set<String> actual = kb.kbCache.instances.get("Relation");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testTransInsts() {

        System.out.println("Test testTransInsts");
        System.out.println("kb.kbCache.transInstOf(\"Attorney\", \"Attribute\"): " +
                kb.kbCache.transInstOf("Attorney", "Attribute"));
        assertThat(kb.kbCache.transInstOf("Attorney", "Attribute")).isTrue();
    }

    @Test
    public void testIsChildOf() {

        System.out.println("Test testIsChildOf");
        assertThat(kb.isChildOf("CitizenryFn", "Function")).isTrue();
    }

    @Test
    public void testCommonParent() {

        System.out.println("Test testCommonParent");
        String actual = kb.kbCache.getCommonParent("LadderBackChair", "Table");
        String expected = "Furniture";
        System.out.println("Test testCommonParent(): result: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testInstancesForType() {

        System.out.println("Test testInstancesForType");
        Set<String> expected = new HashSet<>(Arrays.asList("subAttribute",
                "var", "subclass", "rel", "CitizenryFn", "ResidentFn", "relsub",
                "subrelation"));
        Set<String> actual = kb.kbCache.getInstancesForType("Relation");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testDisjoint() {

        System.out.println("Test testDisjoint");
        Set<String> classes = new HashSet<>(Arrays.asList("Dog", "Jellyfish"));
        System.out.println("KBcacheUnitITCase.testDisjoint(): Dog&Jellyfish");
        System.out.println("KBcacheUnitITCase.testDisjoint(): disjoint? " + kb.kbCache.checkDisjoint(kb, "Dog", "Jellyfish"));
        if (kb.kbCache.checkDisjoint(kb, "Dog", "Jellyfish"))
            System.out.println("KBcacheUnitITCase.testDisjoint(): pass");
        else
            System.out.println("KBcacheUnitITCase.testDisjoint(): fail");
        assertThat(kb.kbCache.checkDisjoint(kb, "Dog", "Jellyfish")).isTrue();

        System.out.println("KBcacheUnitITCase.testDisjoint(): classes: " + classes);
        System.out.println("KBcacheUnitITCase.testDisjoint(): disjoint? " + kb.kbCache.checkDisjoint(kb, classes));
        if (kb.kbCache.checkDisjoint(kb, classes))
            System.out.println("KBcacheUnitITCase.testDisjoint(): pass");
        else
            System.out.println("KBcacheUnitITCase.testDisjoint(): fail");
        assertThat(kb.kbCache.checkDisjoint(kb, classes)).isTrue();

        classes = new HashSet<>(Arrays.asList("Table", "Chair"));
        System.out.println("KBcacheUnitITCase.testDisjoint(): classes: " + classes);
        System.out.println("KBcacheUnitITCase.testDisjoint(): disjoint? " + kb.kbCache.checkDisjoint(kb, classes));
        if (!kb.kbCache.checkDisjoint(kb, classes))
            System.out.println("KBcacheUnitITCase.testDisjoint(): pass");
        else
            System.out.println("KBcacheUnitITCase.testDisjoint(): fail");
        assertThat(kb.kbCache.checkDisjoint(kb, classes)).isFalse();
    }

    @Test
    public void testCollectArgsFromFormulas() {

        System.out.println("Test testCollectArgsFromFormulas");
        String rel = "TransitiveRelation";
        List<Formula> forms = kb.askWithRestriction(0, "instance", 2, rel);
        System.out.println("INFO in KBcache.testCollectArgsFromFormulas(): forms2: " + forms);
        Set<String> actual = new HashSet<>();
        if (forms != null)
            actual.addAll(KBcache.collectArgFromFormulas(1, forms));
        Set<String> expected = new HashSet<>(Arrays.asList("subAttribute",
                "subclass", "subrelation"));
        System.out.println("INFO in KBcache.testCollectArgsFromFormulas(): actual: " + actual);
        System.out.println("INFO in KBcache.testCollectArgsFromFormulas(): expected: " + expected);
        assertThat(actual).isEqualTo(expected);
    }
}
