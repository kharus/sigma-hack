/**
 * This software is released under the GNU Public License
 * <http://www.gnu.org/copyleft/gpl.html>.
 * Copyright 2019 Infosys
 * adam.pease@infosys.com
 */
package com.articulate.sigma;

import com.articulate.sigma.trans.SUMOKBtoTFAKB;
import com.articulate.sigma.trans.SUMOKBtoTPTPKB;
import com.articulate.sigma.trans.SUMOformulaToTPTPformula;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FormulaPreprocessor tests not focused on findExplicitTypes( ) or computeVariableTypes( ).
 */
@SpringBootTest
@Tag("com.articulate.sigma.TopOnly")
@ActiveProfiles("TopOnly")
@Import(KBmanagerTestConfiguration.class)
public class FormulaPreprocessorITCase {
    @Value("${sumokbname}")
    private String sumokbname;

    private KB kb;

    @Autowired
    private KBmanager kbManager;

    @Autowired
    FormulaDeepEqualsService deepEqualsService;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(sumokbname);
    }

    // TODO: Technically, this should to in the FormulaITCase class, but the gatherRelationsWithArgTypes( ) method requires a KB
    // and none of the other tests in that class do. Maybe move the method to FormulaPreprocessor--it's the only Formula method
    // requiring a KB.
    @Test
    public void testGatherRelationships() {

        System.out.println("\n============= testGatherRelationships ==================");
        String stmt = "(agent Leaving Human)";
        Formula f = new Formula();
        f.read(stmt);

        Map<String, List<String>> actualMap = f.gatherRelationsWithArgTypes(kb);

        List<String> expectedList = Lists.newArrayList(null, "Process", "AutonomousAgent", null, null, null, null, null);
        Map<String, List<String>> expectedMap = Maps.newHashMap();
        expectedMap.put("agent", expectedList);

        System.out.println("testGatherRelationships(): actual: " + actualMap);
        System.out.println("testGatherRelationships(): expected: " + expectedMap);
        if (expectedMap.equals(actualMap))
            System.out.println("testGatherRelationships(): pass");
        else
            System.out.println("testGatherRelationships(): fail");
        assertThat(actualMap).isEqualTo(expectedMap);
    }

    // FIXME: test is waiting completion of Formula.logicallyEquals()
    @Disabled
    @Test
    public void testAddTypes1() {

        System.out.println("\n============= testAddTypes1 ==================");
        String stmt = "(=> (forall (?ELEMENT) (<=> (element ?ELEMENT ?SET1) " +
                "(element ?ELEMENT ?SET2))) (equal ?SET1 ?SET2))";
        Formula f = new Formula();
        f.read(stmt);
        FormulaPreprocessor fp = new FormulaPreprocessor();

        Formula expected = new Formula();
        String expectedString = "(=> (and (instance ?SET2 Set) (instance ?SET1 Set)) " +
                "(=> (forall (?ELEMENT) (<=> (element ?ELEMENT ?SET1) (element ?ELEMENT ?SET2))) " +
                "(equal ?SET1 ?SET2)))";
        expected.read(expectedString);

        Formula actual = fp.addTypeRestrictions(f, kb);
        System.out.println("testAddTypes1(): actual: " + actual);
        System.out.println("testAddTypes1(): expected: " + expected);
        if (expected.equals(actual))
            System.out.println("testAddTypes1(): pass");
        else
            System.out.println("testAddTypes1(): fail");
        assertThat(actual).isEqualTo(expected);

        assertThat(deepEqualsService.logicallyEquals(expected, actual)).isTrue();
    }

    // FIXME: test is waiting completion of Formula.logicallyEquals()
    @Disabled
    @Test
    public void testAddTypes2() {

        System.out.println("\n============= testAddTypes2 ==================");
        String stmt = "(=> (and (attribute ?AREA LowTerrain) (part ?ZONE ?AREA)" +
                " (slopeGradient ?ZONE ?SLOPE)) (greaterThan 0.03 ?SLOPE))";
        Formula f = new Formula();
        f.read(stmt);
        FormulaPreprocessor fp = new FormulaPreprocessor();

        Formula expected = new Formula();
        String expectedString = "(=> (and (instance ?ZONE Object) (instance ?SLOPE Quantity) (instance ?AREA Object)) " +
                "(=> (and (attribute ?AREA LowTerrain) (part ?ZONE ?AREA) (slopeGradient ?ZONE ?SLOPE)) (greaterThan 0.03 ?SLOPE)))";
        expected.read(expectedString);

        Formula actual = fp.addTypeRestrictions(f, kb);
        System.out.println("testAddTypes2(): actual: " + actual);
        System.out.println("testAddTypes2(): expected: " + expected);

        assertThat(deepEqualsService.logicallyEquals(expected, actual)).isTrue();
    }

    @Test
    public void testMergeToMap1() {

        System.out.println("\n============= testMergeToMap1 ==================");

        Map<String, Set<String>> map1 = Maps.newHashMap();
        map1.put("?Obj", Sets.newHashSet("Object", "CorpuscularObject"));
        map1.put("?Hum", Sets.newHashSet("Man", "Woman"));
        map1.put("?Time", Sets.newHashSet("Month"));

        Map<String, Set<String>> map2 = Maps.newHashMap();
        map2.put("?Obj", Sets.newHashSet("Object"));
        map2.put("?Hum", Sets.newHashSet("Human"));

        Map<String, Set<String>> expectedMap = Maps.newHashMap();
        expectedMap.put("?Obj", Sets.newHashSet("CorpuscularObject"));
        expectedMap.put("?Hum", Sets.newHashSet("Man", "Woman"));
        expectedMap.put("?Time", Sets.newHashSet("Month"));

        Map<String, Set<String>> actualMap = KButilities.mergeToMap(map1, map2, kb);

        System.out.println("testMergeToMap1(): actual: " + actualMap);
        System.out.println("testMergeToMap1(): expected: " + expectedMap);
        if (expectedMap.equals(actualMap))
            System.out.println("testMergeToMap1(): pass");
        else
            System.out.println("testMergeToMap1(): fail");
        assertThat(actualMap).isEqualTo(expectedMap);
    }

    @Test
    public void test4() {

        System.out.println("\n============= test4 ==================");
        FormulaPreprocessor fp = new FormulaPreprocessor();
        //FormulaPreprocessor.debug = true;
        String strf = "(forall (?NUMBER ?ELEMENT ?CLASS)\n" +
                "        (=>\n" +
                "          (equal ?ELEMENT\n" +
                "            (ListOrderFn\n" +
                "              (ListFn_1Fn ?FOO) ?NUMBER))\n" +
                "          (instance ?ELEMENT ?CLASS)))";
        Formula f = new Formula();
        f.read(strf);
        String actual = fp.addTypeRestrictions(f, kb).toString();
        String expected = """
                (forall (?NUMBER ?ELEMENT ?CLASS)
                    (=>
                      (and
                        (instance ?NUMBER PositiveInteger)
                        (instance ?CLASS Class))
                      (=>
                        (equal ?ELEMENT
                          (ListOrderFn
                            (ListFn_1Fn ?FOO) ?NUMBER))
                        (instance ?ELEMENT ?CLASS))))""";
        System.out.println("test4(): actual: " + actual);
        System.out.println("test4(): expected: " + expected);
        Formula fActual = new Formula(actual);
        Formula fExpected = new Formula(expected);
        assertThat(deepEqualsService.deepEquals(fExpected, fActual)).isTrue();
    }

    @Test
    @Disabled
    public void test5() {

        System.out.println("\n============= test5 ==================");
        FormulaPreprocessor fp = new FormulaPreprocessor();
        String strf = """
                (<=>
                   (equal (RemainderFn ?NUMBER1 ?NUMBER2) ?NUMBER)
                   (equal (AdditionFn (MultiplicationFn (FloorFn (DivisionFn ?NUMBER1 ?NUMBER2)) ?NUMBER2) ?NUMBER) ?NUMBER1))""";
        Formula f = new Formula();
        f.read(strf);
        //FormulaPreprocessor.debug = true;
        String actual = fp.addTypeRestrictions(f, kb).toString();
        String expected = "(=>\n" +
                "    (and\n" +
                "      (instance ?NUMBER1 Integer)\n" +
                "      (instance ?NUMBER2 Integer)\n" +
                "      (instance ?NUMBER Integer) )\n" +
                "    (<=>\n" +
                "   (equal " +
                "     (RemainderFn ?NUMBER1 ?NUMBER2) ?NUMBER)\n" +
                "   (equal \n" +
                "     (AdditionFn \n" +
                "       (MultiplicationFn \n" +
                "         (FloorFn \n" +
                "           (DivisionFn ?NUMBER1 ?NUMBER2)) ?NUMBER2) ?NUMBER) ?NUMBER1)) )";
        System.out.println("test5(): actual: " + actual);
        System.out.println("test5(): expected: " + expected);
        Formula fActual = new Formula(actual);
        Formula fExpected = new Formula(expected);
        assertThat(deepEqualsService.deepEquals(fExpected, fActual)).isTrue();
    }

    @Test
    @Disabled
    public void test6() {

        FormulaPreprocessor fp = new FormulaPreprocessor();
        String strf = """
                (<=>
                  (temporalPart ?POS
                    (WhenFn ?THING))
                  (time ?THING ?POS))""";
        Formula f = new Formula();
        f.read(strf);
        //FormulaPreprocessor.debug = true;
        String actual = fp.addTypeRestrictions(f, kb).toString();
        String expected = """
                (=>
                  (and
                    (instance ?POS TimePosition)
                    (instance ?THING Physical))
                  (<=>
                    (temporalPart ?POS
                      (WhenFn ?THING))
                    (time ?THING ?POS)))""";

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Disabled
    public void test7() {

        FormulaPreprocessor fp = new FormulaPreprocessor();
        String strf = """
                (<=>
                  (temporalPart ?POS
                    (WhenFn ?THING))
                  (time ?THING ?POS))""";
        Formula f = new Formula();
        f.read(strf);

        Set<Formula> actual = fp.preProcess(f, false, kb);
        String expected = """
                (=>
                  (and
                    (instance ?POS TimePosition)
                    (instance ?THING Physical))
                  (<=>
                    (temporalPart ?POS
                      (WhenFn ?THING))
                    (time ?THING ?POS)))""";

        assertThat(actual.iterator().next().toString()).isEqualTo(expected);
    }

    @Test
    public void testAbsolute() {

        System.out.println("\n============= testAbsolute ==================");
        FormulaPreprocessor fp = new FormulaPreprocessor();
        String strf = "(equal\n" +
                "  (AbsoluteValueFn ?NUMBER1) ?NUMBER2)";
        Formula f = new Formula();
        f.read(strf);
        //FormulaPreprocessor.debug = true;
        Map<String, Set<String>> actual = fp.findAllTypeRestrictions(f, kb);
        String expected = "{?NUMBER1=[RealNumber], ?NUMBER2=[NonnegativeRealNumber]}";

        assertThat(actual.toString()).isEqualTo(expected);
    }

    @Test
    public void testInstantiatePredStmt4() {

        System.out.println("\n============= testInstantiatePredStmt4 ==================");
        String stmt = "(=> " +
                "(and " +
                "(minValue ?R ?ARG ?N) " +
                "(?R @ARGS) " +
                "(equal ?VAL (ListOrderFn (ListFn @ARGS) ?ARG))) " +
                "(greaterThan ?VAL ?N))";
        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor fp = new FormulaPreprocessor();
        //FormulaPreprocessor.debug = true;
        List<Formula> actual = fp.replacePredVarsAndRowVars(f, kb, false);
        int expectedSize = 100;
        assertThat(actual.size()).isGreaterThan(100);
    }

    @Test
    public void testMinValuePreprocess() {

        System.out.println("\n============= testMinValuePreprocess ==================");
        String stmt = "(=> " +
                "(and " +
                "(minValue ?R ?ARG ?N) " +
                "(?R @ARGS) " +
                "(equal ?VAL (ListOrderFn (ListFn @ARGS) ?ARG))) " +
                "(greaterThan ?VAL ?N))";
        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor fp = new FormulaPreprocessor();
        assertThat(kb.kbCache.valences.get("greaterThanOrEqualTo").intValue()).isEqualTo(2);
        Set<Formula> actual = fp.preProcess(f, false, kb);

        assertThat(actual.size()).isGreaterThan(100);
    }

    @Test
    public void testArgNumsPreprocess() {

        String stmt = """
                (=>
                    (and
                        (exactCardinality patient ?ARG 1)
                        (instance patient Predicate))
                    (exists (?X @ARGS)
                        (and
                            (patient @ARGS)
                            (equal ?X
                                (ListOrderFn
                                    (ListFn @ARGS) ?ARG))
                            (not
                                (exists (?Y)
                                    (and
                                        (equal ?Y
                                            (ListOrderFn
                                                (ListFn @ARGS) ?ARG))
                                        (not
                                            (equal ?X ?Y))))))))""";
        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor fp = new FormulaPreprocessor();
        //PredVarInst.debug = true;
        //FormulaPreprocessor.debug = true;
        // RowVars.DEBUG = true;
        System.out.println("testArgNumsPreprocess: patient valence: " +
                kb.kbCache.valences.get("patient"));

        List<Formula> forms = RowVars.expandRowVars(kb, f);
        System.out.println("testArgNumsPreprocess: forms: " + forms);
    }

    @Test
    public void testTVRPreprocess() {

        SUMOformulaToTPTPformula.lang = "tff";
        SUMOKBtoTPTPKB.lang = "tff";
        SUMOKBtoTFAKB skbtfakb = new SUMOKBtoTFAKB();
        skbtfakb.initOnce(kb);
        String kbName = sumokbname;
        String filename = KBmanager.getMgr().getPref("kbDir") + File.separator + kbName + ".tff";
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(filename));
            skbtfakb.writeSorts(pw);
            //skbtfakb.writeFile(filename, null, false, "", false, pw);
            pw.flush();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String stmt = """
                (<=>
                    (and
                        (instance ?REL TotalValuedRelation)
                        (instance ?REL Predicate))
                    (exists (?VALENCE)
                        (and
                            (instance ?REL Relation)
                            (valence ?REL ?VALENCE)
                            (=>
                                (forall (?NUMBER ?ELEMENT ?CLASS)
                                    (=>
                                        (and
                                            (lessThan ?NUMBER ?VALENCE)
                                            (domain ?REL ?NUMBER ?CLASS)
                                            (equal ?ELEMENT
                                                (ListOrderFn
                                                    (ListFn @ROW) ?NUMBER)))
                                        (instance ?ELEMENT ?CLASS)))
                                (exists (?ITEM)
                                    (?REL @ROW ?ITEM))))))""";
        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor fp = new FormulaPreprocessor();
        Set<Formula> actual = fp.preProcess(f, false, kb);
        assertThat(actual.size()).isGreaterThan(30);
    }

    @Test
    public void testFunctionVariable() {

        System.out.println("\n============= testFunctionVariable ==================");
        String stmt = "(and\n" +
                "  (instance ?F Function)\n" +
                "  (instance ?I (?F ?X)))";
        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor fp = new FormulaPreprocessor();
        Set<Formula> actual = fp.preProcess(f, false, kb);

        assertThat(actual.size()).isGreaterThan(1);
    }

    @Test
    public void testReplaceQuantifierVars() throws Exception {

        String stmt = """
                (exists (?X)
                        (and
                                (instance ?X Organism)
                        (part Bio18-1 ?X)))""";

        String expected = """
                (exists (Drosophila)
                        (and
                                (instance Drosophila Organism)
                        (part Bio18-1 Drosophila)))""";
        Formula f = new Formula(stmt);
        Formula exp = new Formula(expected);

        List<String> vars = new ArrayList<>();
        vars.add("Drosophila");
        Formula actual = f.replaceQuantifierVars(Formula.EQUANT, vars);
        assertThat(deepEqualsService.logicallyEquals(actual, exp)).isTrue();

        stmt = "(exists (?JOHN ?KICKS ?CART)\n" +
                "  (and\n" +
                "    (instance ?JOHN Human)\n" +
                "    (instance ?KICKS Kicking)\n" +
                "    (instance ?CART Wagon)\n" +
                "    (patient ?KICKS ?CART)\n" +
                "    (agent ?KICKS ?JOHN)))\n";

        expected = "(exists (Doyle Kick_2 Cart_1)\n" +
                "  (and\n" +
                "    (instance Doyle Human)\n" +
                "    (instance Kick_2 Kicking)\n" +
                "    (instance Cart_1 Wagon)\n" +
                "    (patient Kick_2 Cart_1)\n" +
                "    (agent Kick_2 Doyle)))\n";
        f = new Formula(stmt);
        exp = new Formula(expected);

        vars = new ArrayList<>();
        vars.add("Doyle");
        vars.add("Kick_2");
        vars.add("Cart_1");
        actual = f.replaceQuantifierVars(Formula.EQUANT, vars);
        assertThat(deepEqualsService.logicallyEquals(actual, exp)).isTrue();

        stmt = "(exists (?ENTITY)\n" +
                "         (and \n" +
                "           (subclass ?ENTITY Animal) \n" +
                "           (subclass ?ENTITY CognitiveAgent)\n" +
                "           (equal ?ENTITY Human)))";

        expected = "(exists (Ent_1)\n" +
                "         (and \n" +
                "           (subclass Ent_1 Animal) \n" +
                "           (subclass Ent_1 CognitiveAgent)\n" +
                "           (equal Ent_1 Human)))";
        f = new Formula(stmt);
        exp = new Formula(expected);

        vars = new ArrayList<>();
        vars.add("Ent_1");
        actual = f.replaceQuantifierVars(Formula.EQUANT, vars);
        assertThat(deepEqualsService.logicallyEquals(actual, exp)).isTrue();

        stmt = "(exists (?ENTITY)\n" +
                "         (and \n" +
                "           (subclass ?ENTITY ?TEST) \n" +
                "           (subclass ?ENTITY CognitiveAgent)\n" +
                "           (equal ?ENTITY Human)))";

        expected = "(exists (Ent_1)\n" +
                "         (and \n" +
                "           (subclass Ent_1 Ent_1) \n" +
                "           (subclass Ent_1 CognitiveAgent)\n" +
                "           (equal Ent_1 Human)))";
        f = new Formula(stmt);
        exp = new Formula(expected);

        vars = new ArrayList<>();
        vars.add("Ent_1");
        actual = f.replaceQuantifierVars(Formula.EQUANT, vars);
        assertThat(deepEqualsService.logicallyEquals(actual, exp))
                .as(actual + "\n should not be logically equal to \n" + expected)
                .isFalse();
    }
}