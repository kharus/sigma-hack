package com.articulate.sigma;

//This software is released under the GNU Public License
//<http://www.gnu.org/copyleft/gpl.html>.
// Copyright 2019 Infosys, 2020- Articulate Software
// apease@articulatesoftware.com

import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These tests follow PredVarInst.test( ), with the exception of that method's call to FormulaPreprocessor.
 * findExplicitTypesInAntecedent( ), which has been put into the FormulaPreprocessorITCase class.
 * TODO: See how relevant the line "if (kb.kbCache.transInstOf("exhaustiveAttribute","VariableArityRelation"))"
 * at the start of the original PredVarInst.test( ) method is. Should these tests somehow reflect that?
 */
@Category(TopOnly.class)
public class PredVarInstITCase extends UnitTestBase {

    private static final String stmt1 = "(<=> (instance ?REL TransitiveRelation) " +
            "(forall (?INST1 ?INST2 ?INST3) " +
            "(=> (and (?REL ?INST1 ?INST2) " +
            "(?REL ?INST2 ?INST3)) (?REL ?INST1 ?INST3))))";

    private static final String stmt2 = "(=> " +
            "(instance ?JURY Jury) " +
            "(holdsRight " +
            "(exists (?DECISION) " +
            "(and " +
            "(instance ?DECISION LegalDecision) " +
            "(agent ?DECISION ?JURY))) ?JURY))";

    private static final String stmt3 = "(=> (instance ?R TransitiveRelation) (=> (and (?R ?A ?B) (?R ?B ?C)) (?R ?A ?C)))";

    @Test
    public void testGatherPredVarsStmt1() {

        Formula f = new Formula();
        f.read(PredVarInstITCase.stmt1);
        Set<String> actual = PredVarInst.gatherPredVars(SigmaTestBase.kb, f);
        Set<String> expected = Sets.newHashSet("?REL");
        System.out.println("\n--------------------");
        System.out.println("testGatherPredVarsStmt1() actual: " + actual);
        System.out.println("testGatherPredVarsStmt1() expected: " + expected);
        if (expected.equals(actual))
            System.out.println("testGatherPredVarsStmt1(): success!");
        else
            System.out.println("testGatherPredVarsStmt1(): failure");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGatherPredVarsStmt2() {

        Formula f = new Formula();
        f.read(PredVarInstITCase.stmt2);
        Set<String> actual = PredVarInst.gatherPredVars(SigmaTestBase.kb, f);
        Set<String> expected = Sets.newHashSet();
        System.out.println("\n--------------------");
        System.out.println("testGatherPredVarsStmt2() actual: " + actual);
        System.out.println("testGatherPredVarsStmt2() expected: " + expected);
        if (expected.equals(actual))
            System.out.println("testGatherPredVarsStmt2(): success!");
        else
            System.out.println("testGatherPredVarsStmt2(): failure");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGatherPredVarsStmt3() {

        Formula f = new Formula();
        f.read(PredVarInstITCase.stmt3);
        Set<String> actual = PredVarInst.gatherPredVars(SigmaTestBase.kb, f);
        Set<String> expected = Sets.newHashSet("?R");
        System.out.println("\n--------------------");
        System.out.println("testGatherPredVarsStmt3() actual: " + actual);
        System.out.println("testGatherPredVarsStmt3() expected: " + expected);
        if (expected.equals(actual))
            System.out.println("testGatherPredVarsStmt3(): success!");
        else
            System.out.println("testGatherPredVarsStmt3(): failure");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testInstantiatePredStmt2() {

        Formula f = new Formula();
        f.read(PredVarInstITCase.stmt2);
        Set<Formula> actual = PredVarInst.instantiatePredVars(f, SigmaTestBase.kb);
        Set<Formula> expected = Sets.newHashSet();
        System.out.println("\n--------------------");
        System.out.println("testInstantiatePredStmt2() actual: " + actual);
        System.out.println("testInstantiatePredStmt2() expected: " + expected);
        if (expected.equals(actual))
            System.out.println("testInstantiatePredStmt2(): success!");
        else
            System.out.println("testInstantiatePredStmt2(): failure");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testInstantiatePredStmt3() {

        String stmt = "(=> " +
                "(and " +
                "(minValue ?R ?ARG ?N) " +
                "(?R @ARGS) " +
                "(equal ?VAL (ListOrderFn (ListFn @ARGS) ?ARG))) " +
                "(greaterThan ?VAL ?N))";
        Formula f = new Formula();
        f.read(stmt);

        System.out.println("\n--------------------");
        Set<Formula> actual = PredVarInst.instantiatePredVars(f, SigmaTestBase.kb);
        if (actual.size() > 100)
            System.out.println("testInstantiatePredStmt3(): success!");
        else
            System.out.println("testInstantiatePredStmt3(): failure");
        assertThat(actual.size() > 100).isTrue();
    }

    @Test
    public void testPredVarArity() {

        String stmt = "(=> (and (instance ?REL CaseRole) (instance ?OBJ Object) " +
                "(?REL ?PROCESS ?OBJ)) (exists (?TIME) (overlapsSpatially (WhereFn ?PROCESS ?TIME) ?OBJ)))";
        Formula f = new Formula();
        f.read(stmt);
        System.out.println("\n--------------------");
        Set<String> actual = PredVarInst.gatherPredVarRecurse(SigmaTestBase.kb, f);

        Set<String> expected = new HashSet<>();
        expected.add("?REL");
        System.out.println("testPredVarArity() actual: " + actual);
        System.out.println("testPredVarArity() expected: " + expected);
        if (expected.equals(actual))
            System.out.println("testPredVarArity(): success!");
        else
            System.out.println("testPredVarArity(): failure");
        assertThat(actual).isEqualTo(expected);

        System.out.println("PredVarInstITCase.testPredVarArity(): actual arity: " + PredVarInst.predVarArity.get("?REL").intValue());
        System.out.println("PredVarInstITCase.testPredVarArity(): expected arity: " + 2);
        if (PredVarInst.predVarArity.get("?REL").intValue() == 2)
            System.out.println("testPredVarArity(): success!");
        else
            System.out.println("testPredVarArity(): failure");
        assertThat(PredVarInst.predVarArity.get("?REL").intValue()).isEqualTo(2);
    }

    @Test
    public void testPredVarArity2() {

        String stmt = "(=>\n" +
                "  (and\n" +
                "    (instance ?REL CaseRole)\n" +
                "    (instance ?OBJ Object)\n" +
                "    (?REL ?PROCESS ?OBJ))\n" +
                "  (exists (?TIME)\n" +
                "    (overlapsSpatially\n" +
                "      (WhereFn ?PROCESS ?TIME) ?OBJ)))";
        Formula f = new Formula();
        f.read(stmt);
        System.out.println("\n--------------------");
        String var = "?REL";
        System.out.println("PredVarInstITCase.testPredVarArity2(): formula: " + f);
        Set<String> actual = PredVarInst.gatherPredVarRecurse(SigmaTestBase.kb, f);
        System.out.println("PredVarInstITCase.testPredVarArity2(): actual pred vars: " + actual);
        int arity = PredVarInst.predVarArity.get(var).intValue();
        int expectedArity = 2;
        System.out.println("PredVarInstITCase.testPredVarArity2(): actual arity: " + arity);
        System.out.println("PredVarInstITCase.testPredVarArity2(): expectedArity: " + expectedArity);
        Set<String> expected = new HashSet<>();
        expected.add(var);
        System.out.println("PredVarInstITCase.testPredVarArity2(): expected pred vars: " + expected);
        assertThat(actual).isEqualTo(expected);
        assertThat(arity).isEqualTo(expectedArity);
    }

    @Test
    public void testTVRPredVars() {

        String stmt = "(<=>\n" +
                "    (and\n" +
                "        (instance ?REL TotalValuedRelation)\n" +
                "        (instance ?REL Predicate))\n" +
                "    (exists (?VALENCE)\n" +
                "        (and\n" +
                "            (instance ?REL Relation)\n" +
                "            (valence ?REL ?VALENCE)\n" +
                "            (=>\n" +
                "                (forall (?NUMBER ?ELEMENT ?CLASS)\n" +
                "                    (=>\n" +
                "                        (and\n" +
                "                            (lessThan ?NUMBER ?VALENCE)\n" +
                "                            (domain ?REL ?NUMBER ?CLASS)\n" +
                "                            (equal ?ELEMENT\n" +
                "                                (ListOrderFn\n" +
                "                                    (ListFn @ROW) ?NUMBER)))\n" +
                "                        (instance ?ELEMENT ?CLASS)))\n" +
                "                (exists (?ITEM)\n" +
                "                    (?REL @ROW ?ITEM))))))";
        Formula f = new Formula();
        f.read(stmt);
        System.out.println("\n--------------------");
        System.out.println("PredVarInstITCase.testTVRPredVars(): formula: " + f);
        Set<String> actual = PredVarInst.gatherPredVars(SigmaTestBase.kb, f);
        System.out.println("PredVarInstITCase.testTVRPredVars(): actual: " + actual);
        Set<String> expected = new HashSet<>();
        expected.add("?REL");
        System.out.println("PredVarInstITCase.testTVRPredVars(): expected: " + expected);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testTVRArity() {

        String stmt = "(<=>\n" +
                "    (and\n" +
                "        (instance ?REL TotalValuedRelation)\n" +
                "        (instance ?REL Predicate))\n" +
                "    (exists (?VALENCE)\n" +
                "        (and\n" +
                "            (instance ?REL Relation)\n" +
                "            (valence ?REL ?VALENCE)\n" +
                "            (=>\n" +
                "                (forall (?NUMBER ?ELEMENT ?CLASS)\n" +
                "                    (=>\n" +
                "                        (and\n" +
                "                            (lessThan ?NUMBER ?VALENCE)\n" +
                "                            (domain ?REL ?NUMBER ?CLASS)\n" +
                "                            (equal ?ELEMENT\n" +
                "                                (ListOrderFn\n" +
                "                                    (ListFn @ROW) ?NUMBER)))\n" +
                "                        (instance ?ELEMENT ?CLASS)))\n" +
                "                (exists (?ITEM)\n" +
                "                    (?REL @ROW ?ITEM))))))";
        Formula f = new Formula();
        f.read(stmt);
        System.out.println("\n--------------------");
        String var = "?REL";
        System.out.println("PredVarInstITCase.testTVRArity(): formula: " + f);
        System.out.println("PredVarInstITCase.testTVRArity(): variable: " + var);
        Set<String> actual = PredVarInst.gatherPredVars(SigmaTestBase.kb, f);
        int arity = PredVarInst.predVarArity.get(var).intValue();
        int expected = 0; // variable arity is given as "0"
        System.out.println("PredVarInstITCase.testTVRArity(): actual arity: " + arity);
        System.out.println("PredVarInstITCase.testTVRArity(): expected arity: " + expected);
        assertThat(arity).isEqualTo(expected);
    }

    @Test
    public void testTVRTypes() {

        String stmt = "(<=>\n" +
                "    (and\n" +
                "        (instance ?REL TotalValuedRelation)\n" +
                "        (instance ?REL Predicate))\n" +
                "    (exists (?VALENCE)\n" +
                "        (and\n" +
                "            (instance ?REL Relation)\n" +
                "            (valence ?REL ?VALENCE)\n" +
                "            (=>\n" +
                "                (forall (?NUMBER ?ELEMENT ?CLASS)\n" +
                "                    (=>\n" +
                "                        (and\n" +
                "                            (lessThan ?NUMBER ?VALENCE)\n" +
                "                            (domain ?REL ?NUMBER ?CLASS)\n" +
                "                            (equal ?ELEMENT\n" +
                "                                (ListOrderFn\n" +
                "                                    (ListFn @ROW) ?NUMBER)))\n" +
                "                        (instance ?ELEMENT ?CLASS)))\n" +
                "                (exists (?ITEM)\n" +
                "                    (?REL @ROW ?ITEM))))))";
        Formula f = new Formula();
        f.read(stmt);
        System.out.println("\n--------------------");
        System.out.println("PredVarInstITCase.testTVRTypes(): formula: " + f);
        Map<String, Set<String>> varTypes = PredVarInst.findPredVarTypes(f, kb);
        System.out.println("PredVarInstITCase.testTVRTypes(): types from domains: " + varTypes);
        varTypes = PredVarInst.addExplicitTypes(kb, f, varTypes);
        System.out.println("PredVarInstITCase.testTVRTypes(): with explicit types: " + varTypes);
        Set<String> types = varTypes.get("?REL");
        System.out.println("PredVarInstITCase.testTVRTypes(): types: " + types);
        System.out.println("PredVarInstITCase.testTVRTypes(): expected: TotalValuedRelation and Predicate");
        if (types.contains("TotalValuedRelation") && types.contains("Predicate"))
            System.out.println("PredVarInstITCase.testTVRTypes(): pass");
        else
            System.out.println("PredVarInstITCase.testTVRTypes(): fail");
        assertThat(types.contains("TotalValuedRelation")).isTrue();
        assertThat(types.contains("Predicate")).isTrue();
    }

    @Test
    public void testPredVarCount() {

        String stmt = "(=> (and (instance ?REL1 Predicate) (instance ?REL2 Predicate) " +
                "(disjointRelation ?REL1 ?REL2) (not (equal ?REL1 ?REL2)) (?REL1 @ROW2)) (not (?REL2 @ROW2)))";
        Formula f = new Formula();
        f.read(stmt);
        System.out.println("\n--------------------");
        System.out.println("PredVarInstITCase.testPredVarCount(): formula: " + f);
        Set<String> predVars = PredVarInst.gatherPredVars(kb, f);
        System.out.println("PredVarInstITCase.testPredVarCount(): predVars: " + predVars);
        System.out.println("PredVarInstITCase.testPredVarCount(): expected: ?REL1 ?REL2");
        if (predVars.contains("?REL1") && predVars.contains("?REL2") && predVars.size() == 2)
            System.out.println("PredVarInstITCase.testPredVarCount(): pass");
        else
            System.out.println("PredVarInstITCase.testPredVarCount(): fail");
        assertThat(predVars.contains("?REL1") && predVars.contains("?REL2") && predVars.size() == 2).isTrue();
    }

    @Test
    public void testArity() {

        String stmt = "(termFormat EnglishLanguage WestMakianLanguage \"west makian language\")";
        Formula f = new Formula();
        f.read(stmt);
        System.out.println("\n--------------------");
        System.out.println("PredVarInstITCase.testArity(): formula: " + f);
        String hasCorrectArity = PredVarInst.hasCorrectArity(f, kb);
        if (hasCorrectArity == null)
            System.out.println("PredVarInstITCase.testPredVarCount(): pass");
        else
            System.out.println("PredVarInstITCase.testPredVarCount(): fail");
        assertThat(hasCorrectArity).isNull();
    }
}