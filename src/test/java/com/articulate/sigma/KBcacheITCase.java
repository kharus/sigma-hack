package com.articulate.sigma;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("com.articulate.sigma.MidLevel")
@ActiveProfiles("MidLevel")
@Import(KBmanagerTestConfiguration.class)
public class KBcacheITCase {

    private KB kb;

    @Autowired
    private KBmanager kbManager;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(kbManager.getPref("sumokbname"));
    }
    @BeforeAll
    public static void requiredKB() {

        List<String> reqFiles =
                Arrays.asList("Merge.kif", "Mid-level-ontology.kif");
        for (String s : reqFiles) {
            if (!KBmanager.getMgr().getKB(KBmanager.getMgr().getPref("sumokbname")).containsFile(s)) {
                System.out.println("Error in KBcacheITCase.requiredKB() required file " + s + " missing");
                System.exit(-1);
            }
        }
    }

    @Test
    public void testIsParentOf1() {

        KBcache cache = kb.kbCache;
        //System.out.println("parents of Shirt (as instance): " + cache.getParentClassesOfInstance("Shirt"));
        //System.out.println("parents of Shirt: " + cache.parents.get("subclass").get("Shirt"));
        //System.out.println("childOfP(\"Shirt\", \"WearableItem\"): " + cache.childOfP("subclass", "WearableItem","Shirt"));
        //System.out.println("SigmaTestBase.kb.isChildOf(\"Shirt\", \"WearableItem\"): " + SigmaTestBase.kb.isChildOf("Shirt", "WearableItem"));
        //System.out.println("SigmaTestBase.kb.childOf(Shirt, WearableItem): " + SigmaTestBase.kb.childOf("Shirt", "WearableItem"));
        assertThat(kb.kbCache.parents.get("subclass").get("Shirt").contains("WearableItem")).isTrue();
    }

    @Test
    public void testBuildParents() {

        KBcache cache = kb.kbCache;

        String child = "IrreflexiveRelation";
        Set<String> expected = new HashSet<>(Arrays.asList("Entity", "Relation", "InheritableRelation", "Abstract", "BinaryRelation"));
        Set<String> actual = cache.getParentClasses(child);
        assertThat(actual).isEqualTo(expected);

        child = "City";
        expected = new HashSet<>(Arrays.asList("Entity", "Physical", "Object", "Region", "GeographicArea", "AutonomousAgent", "GeopoliticalArea", "LandArea"));
        actual = cache.getParentClasses(child);
        assertThat(actual).isEqualTo(expected);

        child = "AsymmetricRelation";
        expected = new HashSet<>(Arrays.asList("Entity", "Abstract", "Relation", "InheritableRelation", "BinaryRelation", "AntisymmetricRelation", "IrreflexiveRelation"));
        actual = cache.getParentClasses(child);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Disabled
    public void testBuildChildren() {

        System.out.println("\n============= testBuildChildren ==================");
        KBcache cache = kb.kbCache;
        String kbName = KBmanager.getMgr().getPref("sumokbname");
        System.out.println("testbuildChildren(): KBs: " + KBmanager.getMgr().getKB(kbName).constituents);
        String parent = "BiologicalAttribute";

        TreeSet<String> expected = new TreeSet<>(Arrays.asList("AnimacyAttribute",
                "BacterialDisease",
                "BiologicalAttribute", "BodyPosition", "ChronicDisease", "ConsciousnessAttribute", "Depression",
                "DevelopmentalAttribute", "Disability", "DiseaseOrSyndrome", "EmotionalState", "Fingerprint",
                "FungalDisease", "HemorrhagicFever", "Hepatitis", "InfectiousDisease", "Influenza", "LifeThreateningDisease",
                "LiteracyAttribute", "Neurosis", "NonspecificDisease",
                "ParasiticDisease", "PhysicalDisability", "PhysicalDisease", "PostTraumaticStressDisorder",
                "PsychologicalAttribute", "PsychologicalDysfunction", "Psychosis", "RiftValleyFever", "SensoryDisability", "SexAttribute",
                "StateOfMind", "TickBorneEncephalitis", "TraitAttribute",
                "TyphoidFever", "VaccinatableDisease", "VenezuelanEquineEncephalitis", "ViralDisease", "VisualAcuityAttribute"));

        TreeSet<String> actual = new TreeSet<String>(cache.getChildClasses(parent));
        System.out.println("Expected: " + expected);
        System.out.println("Actual: " + actual);
        assertThat(actual).isEqualTo(expected);

        parent = "AsymmetricRelation";
        expected = new TreeSet<>(Arrays.asList("AsymmetricRelation", "PropositionalAttitude", "CaseRole"));
        actual = new TreeSet<String>(cache.getChildClasses(parent));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testBuildChildren2() {

        System.out.println("\n============= testBuildChildren2 ==================");
        KBcache cache = kb.kbCache;
        String parent = "RealNumber";

        TreeSet<String> expected = new TreeSet<>(Arrays.asList("RationalNumber", "Integer", "EvenInteger",
                "OddInteger", "PrimeNumber", "NonnegativeInteger", "PositiveInteger", "NegativeInteger",
                "IrrationalNumber", "NonnegativeRealNumber", "PositiveRealNumber", "PositiveInteger",
                "NegativeRealNumber", "NegativeInteger", "BinaryNumber"));

        TreeSet<String> actual = new TreeSet<String>(cache.getChildClasses(parent));

        System.out.println("KBcacheITCase.testBuildChildren2(): actual: " + actual);
        System.out.println("KBcacheITCase.testBuildChildren2(): expected: " + expected);
        if (actual.equals(expected))
            System.out.println("KBcacheITCase.testBuildChildren2(): pass");
        else
            System.out.println("KBcacheITCase.testBuildChildren2(): fail");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testTransitiveRelations() {

        System.out.println("\n============= testTransitiveRelations ==================");
        KBcache cache = kb.kbCache;

        String relation = "abbreviation";
        System.out.println("testbuildTransInstOf(): testing: " + relation);
        Set<String> expected = new HashSet<>(Arrays.asList("Entity", "Relation", "InheritableRelation",
                "Abstract", "BinaryPredicate", "BinaryRelation", "Predicate"));
        Set<String> actual = cache.getParentClassesOfInstance(relation);
        assertThat(actual).isEqualTo(expected);

        relation = "during";  // TODO: since during is a subrelation of temporalPart it should be a superset here - bad test
        System.out.println("testbuildTransInstOf(): testing: " + relation);
        expected = new HashSet<>(Arrays.asList("Entity", "TransitiveRelation", "Abstract", "Relation",
                "InheritableRelation", "IrreflexiveRelation", "BinaryPredicate", "BinaryRelation", "Predicate"));
        actual = cache.getParentClassesOfInstance(relation);
        assertThat(actual).isEqualTo(expected);

        relation = "temporalPart";
        System.out.println("testbuildTransInstOf(): testing: " + relation);
        //expected = new HashSet<>(Arrays.asList("Entity", "TransitiveRelation", "AntisymmetricRelation",
        //        "Abstract", "TemporalRelation", "Relation", "InheritableRelation", "ReflexiveRelation",
        //        "PartialOrderingRelation", "BinaryRelation", "BinaryPredicate", "Predicate"));
        expected = new HashSet<>(Arrays.asList("Entity", "TransitiveRelation", "AntisymmetricRelation",
                "Abstract", "TotalValuedRelation", "Predicate", "TemporalRelation", "Relation",
                "InheritableRelation", "ReflexiveRelation", "BinaryPredicate", "PartialOrderingRelation",
                "BinaryRelation"));
        actual = cache.getParentClassesOfInstance(relation);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testIsChildOf1() {

        KBcache cache = kb.kbCache;
        //System.out.println("parents of CitizenryFn (as instance): " + cache.getParentClassesOfInstance("CitizenryFn"));
        //System.out.println("parents of CitizenryFn: " + cache.parents.get("subclass").get("CitizenryFn"));
        assertThat(kb.isChildOf("CitizenryFn", "Function")).isTrue();
    }

    @Test
    public void testIsChildOf2() {

        KBcache cache = kb.kbCache;
        //System.out.println("parents of Attorney (as instance): " + cache.getParentClassesOfInstance("Attorney"));
        //System.out.println("parents of Attorney: " + cache.parents.get("subclass").get("Attorney"));
        assertThat(kb.isChildOf("Attorney", "Attribute")).isTrue();
    }

    @Test
    public void testIsChildOf3() {

        KBcache cache = kb.kbCache;
        //System.out.println("parents of Shirt (as instance): " + cache.getParentClassesOfInstance("Shirt"));
        //System.out.println("parents of Shirt: " + cache.parents.get("subclass").get("Shirt"));
        //System.out.println("childOfP(\"Shirt\", \"WearableItem\"): " + cache.childOfP("subclass", "WearableItem","Shirt"));
        //System.out.println("kb.isChildOf(\"Shirt\", \"WearableItem\"): " + kb.isChildOf("Shirt", "WearableItem"));
        assertThat(kb.isChildOf("Shirt", "WearableItem")).isTrue();
    }

    @Test
    public void testIsChildOf4() {

        KBcache cache = kb.kbCache;
        //System.out.println("parents of Shirt (as instance): " + cache.getParentClassesOfInstance("Shirt"));
        //System.out.println("parents of Shirt: " + cache.parents.get("subclass").get("Shirt"));
        //System.out.println("childOfP(\"Shirt\", \"Process\"): " + cache.childOfP("subclass", "Process","Shirt"));
        //System.out.println("kb.isChildOf(\"Shirt\", \"Process\"): " + kb.isChildOf("Shirt", "Process"));
        assertThat(kb.isChildOf("Shirt", "Process")).isFalse();
    }

    @Test
    public void testIsChildOf5() {

        KBcache cache = kb.kbCache;
        assertThat(kb.isChildOf("Integer", "RealNumber")).isTrue();
    }

    @Test
    public void testIsChildOf6() {

        KBcache cache = kb.kbCache;
        assertThat(kb.isChildOf("Writing", "ContentDevelopment")).isTrue();
    }

    @Test
    public void testTransitiveRelations2() {

        System.out.println("\n============= testTransitiveRelations2 ==================");
        KBcache cache = kb.kbCache;
        System.out.println("testTransitiveRelations2: " + cache.transRels);
        assertThat(cache.transRels.contains("subAttribute")).isTrue();
        assertThat(cache.transRels.contains("subrelation")).isTrue();
    }

    /**
     * TODO: try to fix this
     */
    @Test
    @Disabled
    public void testDisjoint() {

        System.out.println("\n============= testDisjoint ==================");
        System.out.println("Test testDisjoint");
        Set<String> classes = new HashSet<>(Arrays.asList("Arthropod", "Bird"));
        System.out.println("KBcacheITCase.testDisjoint(): Arthropod&Bird");
        System.out.println("KBcacheITCase.testDisjoint(): disjoint? " + kb.kbCache.checkDisjoint(kb, "Arthropod", "Bird"));
        if (kb.kbCache.checkDisjoint(kb, "Arthropod", "Bird"))
            System.out.println("KBcacheITCase.testDisjoint(): pass");
        else
            System.out.println("KBcacheITCase.testDisjoint(): fail");
        assertThat(kb.kbCache.checkDisjoint(kb, "Arthropod", "Bird")).isTrue();

        System.out.println("KBcacheITCase.testDisjoint(): classes: " + classes);
        System.out.println("KBcacheITCase.testDisjoint(): disjoint? " + kb.kbCache.checkDisjoint(kb, classes));
        if (kb.kbCache.checkDisjoint(kb, classes))
            System.out.println("KBcacheITCase.testDisjoint(): pass");
        else
            System.out.println("KBcacheITCase.testDisjoint(): fail");
        assertThat(kb.kbCache.checkDisjoint(kb, classes)).isTrue();

        classes = new HashSet<>(Arrays.asList("Table", "Chair"));
        System.out.println("KBcacheITCase.testDisjoint(): classes: " + classes);
        System.out.println("KBcacheITCase.testDisjoint(): disjoint? " + kb.kbCache.checkDisjoint(kb, classes));
        if (!kb.kbCache.checkDisjoint(kb, classes))
            System.out.println("KBcacheITCase.testDisjoint(): pass");
        else
            System.out.println("KBcacheITCase.testDisjoint(): fail");
        assertThat(kb.kbCache.checkDisjoint(kb, classes)).isFalse();

        classes = new HashSet<>(Arrays.asList("Table", "Agent"));
        System.out.println("KBcacheITCase.testDisjoint(): classes: " + classes);
        System.out.println("KBcacheITCase.testDisjoint(): disjoint? " + kb.kbCache.checkDisjoint(kb, classes));
        if (kb.kbCache.checkDisjoint(kb, classes))
            System.out.println("KBcacheITCase.testDisjoint(): pass");
        else
            System.out.println("KBcacheITCase.testDisjoint(): fail");
        assertThat(kb.kbCache.checkDisjoint(kb, classes)).isTrue();
    }

    @Test
    public void testSignature() {

        KBcache cache = kb.kbCache;
        System.out.println("testSignature(): cache.getSignature(memberTypeCount): " + cache.getSignature("memberTypeCount"));
        List<String> expected = List.of("", "Collection", "Class", "NonnegativeInteger");
        List<String> actual = cache.getSignature("memberTypeCount");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testTransInst() {

        System.out.println("\n============= testTransInst ==================");
        KBcache cache = kb.kbCache;
        System.out.println("testTransInst(): cache.transInstOf(Anger,Entity): " + cache.transInstOf("Anger", "Entity"));
        System.out.println("testTransInst(): insts.contains(Anger): " + cache.insts.contains("Anger"));
        //System.out.println("testTransInst(): insts.contains(Anger): " + cache.insts.contains("Anger"));
        //System.out.println("testTransInst(): instancesOf: " + cache.instanceOf);
        assertThat(cache.transInstOf("Anger", "Entity")).isTrue();
    }

    @Test
    public void testRealization() {

        System.out.println("\n============= testRealization ==================");
        KBcache cache = kb.kbCache;
        System.out.println("testRealization(): cache.isInstanceOf(realization,AntisymmetricRelation): " + cache.isInstanceOf("realization", "AntisymmetricRelation"));
        System.out.println("testRealization(): cache.isInstanceOf(realization,SymmetricRelation): " + cache.isInstanceOf("realization", "SymmetricRelation"));
        //System.out.println("testRealization(): cache.instances.get(AntisymmetricRelation): " + cache.instances.get("AntisymmetricRelation"));
        //System.out.println("testRealization(): cache.instances.get(SymmetricRelation): " + cache.instances.get("SymmetricRelation"));
        //System.out.println("testRealization(): cache.parents.get(intsance).get(realization): " + cache.parents.get("subrelation").get("realization"));
        //System.out.println("testRealization(): cache.instanceOf.get(realization): " + cache.instanceOf.get("realization"));
        assertThat(cache.isInstanceOf("realization", "SymmetricRelation")).isFalse();
        assertThat(cache.isInstanceOf("realization", "AntisymmetricRelation")).isTrue();
    }

    @Test
    public void testFunctions() {

        System.out.println("\n============= testFunctions ==================");
        KBcache cache = kb.kbCache;
        System.out.println("cache.functions.contains(\"AfternoonFn\"): " + cache.functions.contains("AfternoonFn"));
        assertThat(cache.functions.contains("AfternoonFn")).isTrue();

    }

    @Test
    @Disabled
    public void testPredicates() {

        System.out.println("\n============= testPredicates ==================");
        KBcache cache = kb.kbCache;
        Set<String> rels = cache.getChildInstances("Relation");
        for (String rel : rels) {
            if (!rel.endsWith("Fn")) {
                if (!cache.isInstanceOf(rel, "Predicate")) {
                    System.out.println("fail - " + rel + " not instance of Predicate");
                    System.out.println("parents of " + rel + " " + cache.instanceOf.get(rel));
                } else
                    System.out.println("success for predicate: " + rel);
            }
        }
        for (String rel : rels) {
            if (!rel.endsWith("Fn")) {
                if (!cache.isInstanceOf(rel, "Predicate")) {
                    System.out.println("fail - " + rel + " not instance of Predicate");
                    System.out.println("parents of " + rel + " " + cache.instanceOf.get(rel));
                } else
                    System.out.println("success for predicate: " + rel);
                assertThat(cache.isInstanceOf(rel, "Predicate")).isTrue();
            }
        }
        System.out.println("Success");
    }
}
