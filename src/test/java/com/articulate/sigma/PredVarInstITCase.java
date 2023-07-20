/*
 * This software is released under the GNU Public License
 * <http://www.gnu.org/copyleft/gpl.html>.
 * Copyright 2019 Infosys, 2020- Articulate Software
 * apease@articulatesoftware.com
 */
package com.articulate.sigma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These tests follow PredVarInst.test( ), with the exception of that method's call to FormulaPreprocessor.
 * findExplicitTypesInAntecedent( ), which has been put into the FormulaPreprocessorITCase class.
 * TODO: See how relevant the line "if (kb.kbCache.transInstOf("exhaustiveAttribute","VariableArityRelation"))"
 * at the start of the original PredVarInst.test( ) method is. Should these tests somehow reflect that?
 */
@SpringBootTest
@Tag("com.articulate.sigma.TopOnly")
@ActiveProfiles("TopOnly")
@Import(KBmanagerTestConfiguration.class)
public class PredVarInstITCase {
    private static final String stmt1 = """
            (<=>
             (instance ?REL TransitiveRelation)
             (forall
              (?INST1 ?INST2 ?INST3)
              (=>
               (and
                (?REL ?INST1 ?INST2)
                (?REL ?INST2 ?INST3))
               (?REL ?INST1 ?INST3))))""";
    private static final String stmt2 = """
            (=>
              (instance ?JURY Jury)
              (holdsRight
               (exists
                (?DECISION)
                (and
                 (instance ?DECISION LegalDecision)
                 (agent ?DECISION ?JURY)))
               ?JURY))""";
    private static final String stmt3 = """
            (=>
             (instance ?R TransitiveRelation)
             (=>
              (and
               (?R ?A ?B)
               (?R ?B ?C))
              (?R ?A ?C)))""";
    @Value("${sumokbname}")
    private String sumokbname;
    private KB kb;
    @Autowired
    private KBmanager kbManager;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(sumokbname);
    }

    @Test
    public void testGatherPredVarsStmt1() {

        Formula f = new Formula();
        f.read(PredVarInstITCase.stmt1);
        Set<String> actual = PredVarInst.gatherPredVars(kb, f);
        Set<String> expected = Set.of("?REL");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGatherPredVarsStmt2() {

        Formula f = new Formula();
        f.read(PredVarInstITCase.stmt2);
        Set<String> actual = PredVarInst.gatherPredVars(kb, f);
        assertThat(actual).isEmpty();
    }

    @Test
    public void testGatherPredVarsStmt3() {

        Formula f = new Formula();
        f.read(PredVarInstITCase.stmt3);
        Set<String> actual = PredVarInst.gatherPredVars(kb, f);
        Set<String> expected = Set.of("?R");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testInstantiatePredStmt2() {

        Formula f = new Formula();
        f.read(PredVarInstITCase.stmt2);
        Set<Formula> actual = PredVarInst.instantiatePredVars(f, kb);
        assertThat(actual).isEmpty();
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

        Set<Formula> actual = PredVarInst.instantiatePredVars(f, kb);
        assertThat(actual).hasSizeGreaterThanOrEqualTo(100);
    }

    @Test
    public void testPredVarArity() {

        String stmt = """
                (=>
                 (and
                  (instance ?REL CaseRole)
                  (instance ?OBJ Object)
                  (?REL ?PROCESS ?OBJ))
                 (exists
                  (?TIME)
                  (overlapsSpatially
                   (WhereFn ?PROCESS ?TIME)
                   ?OBJ)))""";
        Formula f = new Formula();
        f.read(stmt);
        Set<String> actual = PredVarInst.gatherPredVarRecurse(kb, f);

        Set<String> expected = Set.of("?REL");

        assertThat(actual).isEqualTo(expected);

        assertThat(PredVarInst.predVarArity.get("?REL")).isEqualTo(2);
    }

    @Test
    public void testPredVarArity2() {

        String stmt = """
                (=>
                  (and
                    (instance ?REL CaseRole)
                    (instance ?OBJ Object)
                    (?REL ?PROCESS ?OBJ))
                  (exists (?TIME)
                    (overlapsSpatially
                      (WhereFn ?PROCESS ?TIME) ?OBJ)))""";
        Formula f = new Formula();
        f.read(stmt);

        Set<String> actual = PredVarInst.gatherPredVarRecurse(kb, f);
        int arity = PredVarInst.predVarArity.get("?REL");

        assertThat(actual).isEqualTo(Set.of("?REL"));
        assertThat(arity).isEqualTo(2);
    }

    @Test
    public void testTVRPredVars() {

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
        Set<String> actual = PredVarInst.gatherPredVars(kb, f);
        assertThat(actual).isEqualTo(Set.of("?REL"));
    }

    @Test
    public void testTVRArity() {

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
        PredVarInst.gatherPredVars(kb, f);
        int arity = PredVarInst.predVarArity.get("?REL");
        assertThat(arity).isEqualTo(0);
    }

    @Test
    public void testTVRTypes() {

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
        Map<String, Set<String>> varTypes = PredVarInst.findPredVarTypes(f, kb);
        varTypes = PredVarInst.addExplicitTypes(kb, f, varTypes);
        Set<String> types = varTypes.get("?REL");
        assertThat(types).contains("TotalValuedRelation");
        assertThat(types).contains("Predicate");
    }

    @Test
    public void testPredVarCount() {

        String stmt = "(=> (and (instance ?REL1 Predicate) (instance ?REL2 Predicate) " +
                "(disjointRelation ?REL1 ?REL2) (not (equal ?REL1 ?REL2)) (?REL1 @ROW2)) (not (?REL2 @ROW2)))";
        Formula f = new Formula();
        f.read(stmt);
        Set<String> predVars = PredVarInst.gatherPredVars(kb, f);

        assertThat(predVars).contains("?REL1");
        assertThat(predVars).contains("?REL2");
        assertThat(predVars).hasSize(2);
    }

    @Test
    public void testArity() {

        String stmt = "(termFormat EnglishLanguage WestMakianLanguage \"west makian language\")";
        Formula f = new Formula();
        f.read(stmt);
        String hasCorrectArity = PredVarInst.hasCorrectArity(f, kb);
        assertThat(hasCorrectArity).isNull();
    }
}