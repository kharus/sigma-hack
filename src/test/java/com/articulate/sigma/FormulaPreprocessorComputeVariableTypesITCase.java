package com.articulate.sigma;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FormulaPreprocessor tests focused on computeVariableTypes().
 */
@SpringBootTest
@Tag("com.articulate.sigma.TopOnly")
@ActiveProfiles("TopOnly")
@Import(KBmanagerTestConfiguration.class)
public class FormulaPreprocessorComputeVariableTypesITCase {

    private KB kb;

    @Autowired
    private KBmanager kBManager;

    @BeforeEach
    void init() {
        kb = kBManager.getKB(kBManager.getPref("sumokbname"));
    }

    @Test
    public void testComputeVariableTypesNoVariables() {
        String stmt = "(domain date 1 Physical)";
        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor formulaPre = new FormulaPreprocessor();
        Map<String, Set<String>> actual = formulaPre.computeVariableTypes(f, kb);

        Map<String, Set<String>> expected = new HashMap<>();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComputeVariableTypesNames() {
        String stmt = "(names \"John\" ?H)";
        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor formulaPre = new FormulaPreprocessor();
        Map<String, Set<String>> actual = formulaPre.computeVariableTypes(f, kb);

        Set<String> set = Sets.newHashSet("Entity");
        Map<String, Set<String>> expected = Maps.newHashMap();
        expected.put("?H", set);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComputeVariableTypesInstance() {
        String stmt = "(exists (?D ?H)\n" +
                "           (and\n" +
                "               (instance ?D Driving)\n" +
                "               (instance ?H Human)\n" +
                "               (agent ?D ?H)))";
        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor formulaPre = new FormulaPreprocessor();
        Map<String, Set<String>> actual = formulaPre.computeVariableTypes(f, kb);

        Map<String, Set<String>> expected = Maps.newHashMap();
        Set<String> set1 = Sets.newHashSet("AutonomousAgent");
        expected.put("?H", set1);
        Set<String> set2 = Sets.newHashSet("Process");
        expected.put("?D", set2);

        assertThat(actual).isEqualTo(expected);
    }

    /**
     * Result shows that the method does not implicitly ID the agent of a Driving as a human or an entity.
     */
    @Test
    public void testComputeVariableTypesInstanceImplicitHuman() {
        String stmt = "(exists (?D ?H)\n" +
                "           (and\n" +
                "               (instance ?D Driving)\n" +
                "               (agent ?D ?H)))";
        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor formulaPre = new FormulaPreprocessor();
        Map<String, Set<String>> actual = formulaPre.computeVariableTypes(f, kb);

        Map<String, Set<String>> expected = Maps.newHashMap();
        Set<String> set1 = Sets.newHashSet("AutonomousAgent");
        expected.put("?H", set1);
        Set<String> set2 = Sets.newHashSet("Process");
        expected.put("?D", set2);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComputeVariableTypesInstanceAgentInstrument() {
        String stmt = """
                (exists (?D ?H ?Car)
                           (and
                               (instance ?D Driving)
                               (instance ?H Human)
                               (names "John" ?H)
                               (instance ?Car Automobile)
                               (agent ?D ?H)
                               (patient ?D ?Car)))""";
        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor formulaPre = new FormulaPreprocessor();
        Map<String, Set<String>> actual = formulaPre.computeVariableTypes(f, kb);

        Map<String, Set<String>> expected = Maps.newHashMap();
        expected.put("?H", Sets.newHashSet("AutonomousAgent"));
        expected.put("?D", Sets.newHashSet("Process"));
        expected.put("?Car", Sets.newHashSet("Entity"));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComputeVariableTypesElementSet() {
        String stmt = "(=> " +
                "           (forall (?ELEMENT) " +
                "               (<=> " +
                "                   (element ?ELEMENT ?SET1) " +
                "                   (element ?ELEMENT ?SET2))) " +
                "           (equal ?SET1 ?SET2))";
        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor formulaPre = new FormulaPreprocessor();
        Map<String, Set<String>> actual = formulaPre.computeVariableTypes(f, kb);

        Map<String, Set<String>> expected = Maps.newHashMap();
        Set<String> set1 = Sets.newHashSet("Set");
        expected.put("?SET1", set1);
        Set<String> set2 = Sets.newHashSet("Set");
        expected.put("?SET2", set2);
        Set<String> setElement = Sets.newHashSet("Entity");
        expected.put("?ELEMENT", setElement);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComputeVariableTypesSubclass() {
        String stmt = "(subclass ?Cougar Feline)";
        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor formulaPre = new FormulaPreprocessor();
        Map<String, Set<String>> actual = formulaPre.computeVariableTypes(f, kb);

        Map<String, Set<String>> expected = Maps.newHashMap();
        Set<String> set1 = Sets.newHashSet("Class");
        expected.put("?Cougar", set1);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComputeVariableTypesMonthFn() {
        String stmt = "(exists (?M) " +
                "           (time JohnsBirth (MonthFn ?M (YearFn 2000))))";

        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor formulaPre = new FormulaPreprocessor();
        Map<String, Set<String>> actual = formulaPre.computeVariableTypes(f, kb);

        Map<String, Set<String>> expected = Maps.newHashMap();
        Set<String> set1 = Sets.newHashSet("Month+");
        expected.put("?M", set1);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComputeVariableTypesGovFn() {
        String stmt = "(=> " +
                "           (instance (GovernmentFn ?Place) StateGovernment) " +
                "           (instance ?Place StateOrProvince))) ";

        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor formulaPre = new FormulaPreprocessor();
        Map<String, Set<String>> actual = formulaPre.computeVariableTypes(f, kb);

        Map<String, Set<String>> expected = Maps.newHashMap();
        Set<String> set1 = Sets.newHashSet("GeopoliticalArea");
        expected.put("?Place", set1);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComputeVariableTypesSubclassIf() {
        String stmt = "(=> " +
                "           (subclass ?Cougar Feline) " +
                "           (subclass ?Cougar Carnivore))";
        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor formulaPre = new FormulaPreprocessor();
        Map<String, Set<String>> actual = formulaPre.computeVariableTypes(f, kb);

        Map<String, Set<String>> expected = Maps.newHashMap();
        Set<String> set1 = Sets.newHashSet("Class");
        expected.put("?Cougar", set1);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComputeVariableTypesLowTerrain() {
        Map<String, Set<String>> expected = ImmutableMap.of("?ZONE", Sets.newHashSet("Object"),
                "?SLOPE", Sets.newHashSet("RealNumber"), "?AREA", Sets.newHashSet("Object"));

        String stmt = "(=> (and (attribute ?AREA LowTerrain) (part ?ZONE ?AREA)" +
                " (slopeGradient ?ZONE ?SLOPE)) (greaterThan 0.03 ?SLOPE))";
        Formula f = new Formula();
        f.read(stmt);
        FormulaPreprocessor fp = new FormulaPreprocessor();
        Map<String, Set<String>> actualMap = fp.computeVariableTypes(f, kb);

        assertThat(actualMap).isEqualTo(expected);

    }

    @Test
    public void testComputeVariableTypesIfAndOnlyIfTransitiveRelation() {
        Map<String, Set<String>> expected = Maps.newHashMap();
        expected.put("?REL", Sets.newHashSet("Entity"));

        String stmt = "(<=> (instance ?REL TransitiveRelation) " +
                "(forall (?INST1 ?INST2 ?INST3) " +
                "(=> (and (?REL ?INST1 ?INST2) " +
                "(?REL ?INST2 ?INST3)) (?REL ?INST1 ?INST3))))";
        Formula f = new Formula();
        f.read(stmt);
        FormulaPreprocessor fp = new FormulaPreprocessor();
        System.out.println("Var types: " + fp.computeVariableTypes(f, kb));

        Map<String, Set<String>> actualMap = fp.computeVariableTypes(f, kb);

        assertThat(actualMap).isEqualTo(expected);
    }

    @Test
    public void testComputeVariableTypesForAllElementSet() {
        Map<String, Set<String>> expected = Maps.newHashMap();
        expected.put("?SET2", Sets.newHashSet("Set"));
        expected.put("?SET1", Sets.newHashSet("Set"));
        expected.put("?ELEMENT", Sets.newHashSet("Entity"));

        String stmt = "(=> (forall (?ELEMENT) (<=> (element ?ELEMENT ?SET1) " +
                "(element ?ELEMENT ?SET2))) (equal ?SET1 ?SET2))";
        Formula f = new Formula();
        f.read(stmt);
        FormulaPreprocessor fp = new FormulaPreprocessor();
        System.out.println("Formula: " + f);
        System.out.println("Var types: " + fp.computeVariableTypes(f, kb));

        Map<String, Set<String>> actualMap = fp.computeVariableTypes(f, kb);

        assertThat(actualMap).isEqualTo(expected);
    }

    @Test
    public void testComputeVariableTypesAwake() {
        Map<String, Set<String>> expected = Maps.newHashMap();
        expected.put("?HUMAN", Sets.newHashSet("AutonomousAgent"));
        expected.put("?PROC", Sets.newHashSet("Process"));

        String stmt = """
                (=>
                           (and
                               (instance ?PROC IntentionalProcess)
                               (agent ?PROC ?HUMAN)
                               (instance ?HUMAN Animal))
                           (holdsDuring
                               (WhenFn ?PROC)
                               (attribute ?HUMAN Awake)))""";
        Formula f = new Formula(stmt);

        FormulaPreprocessor fp = new FormulaPreprocessor();
        System.out.println("Formula: " + f);
        System.out.println("Var types: " + fp.computeVariableTypes(f, kb));

        Map<String, Set<String>> actualMap = fp.computeVariableTypes(f, kb);

        assertThat(actualMap).isEqualTo(expected);
    }

}