package com.articulate.sigma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
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
public class RowVarITCase {
    private KB kb;

    @Autowired
    private KBmanager kbManager;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(kbManager.getPref("sumokbname"));
    }
    @Test
    public void testFindRowVars() {

        System.out.println("\n=========== testFindRowVars =================");
        String stmt1 = "(links @ARGS)";

        Formula f = new Formula();
        f.read(stmt1);

        //RowVars.DEBUG = true;
        Set<String> vars = RowVars.findRowVars(f);
        assertThat(vars != null && vars.size() > 0).isTrue();
        if (vars.contains("@ARGS") && vars.size() == 1)
            System.out.println("testFindRowVars(): success!");
        else
            System.out.println("testFindRowVars(): failure");
        assertThat(vars.contains("@ARGS") && vars.size() == 1).isTrue();
    }

    @Test
    public void testRowVarRels() {

        System.out.println("\n=========== testRowVarRels =================");
        String stmt1 = "(=>\n" +
                "  (and\n" +
                "    (minValue links ?ARG ?N)\n" +
                "    (links @ARGS)\n" +
                "    (equal ?VAL\n" +
                "      (ListOrderFn\n" +
                "        (ListFn @ARGS) ?ARG)))\n" +
                "  (greaterThan ?VAL ?N))";

        Formula f = new Formula();
        f.read(stmt1);

        //RowVars.DEBUG = true;
        Map<String, Set<String>> rels = RowVars.getRowVarRelations(f);
        assertThat(rels != null && rels.keySet().size() > 0).isTrue();
        System.out.println("testRowVarRels(): rels: " + rels);
        if (rels.get("@ARGS").contains("links"))
            System.out.println("testRowVarRels(): success!");
        else
            System.out.println("testRowVarRels(): failure");
        assertThat(rels.get("@ARGS").contains("links")).isTrue();
    }

    @Test
    public void testLinks() {

        System.out.println("\n=========== testLinks =================");
        String stmt1 = "(=>\n" +
                "  (and\n" +
                "    (minValue links ?ARG ?N)\n" +
                "    (links @ARGS)\n" +
                "    (equal ?VAL\n" +
                "      (ListOrderFn\n" +
                "        (ListFn @ARGS) ?ARG)))\n" +
                "  (greaterThan ?VAL ?N))";

        Formula f = new Formula();
        f.read(stmt1);

        //RowVars.DEBUG = true;
        Map<String, Set<String>> rels = RowVars.getRowVarRelations(f);
        Map<String, Integer> rowVarMaxArities = RowVars.getRowVarMaxAritiesWithOtherArgs(rels, kb, f);
        int arity = kb.kbCache.valences.get("links").intValue();
        System.out.println("testLinks(): arity of 'links': " + arity);
        System.out.println("testLinks(): rels: " + rels);
        System.out.println("testLinks(): rowVarMaxArities: " + rowVarMaxArities);
        System.out.println("testLinks(): result: " + rowVarMaxArities.get("@ARGS").intValue());
        System.out.println("testLinks(): expected: " + 3);
        if (3 == rowVarMaxArities.get("@ARGS").intValue())
            System.out.println("testLinks(): success!");
        else
            System.out.println("testLinks(): failure");
        assertThat(rowVarMaxArities.get("@ARGS").intValue()).isEqualTo(3);
    }

    @Test
    public void testLinks2() {

        System.out.println("\n=========== testLinks2 =================");
        String stmt1 = "(=>\n" +
                "  (and\n" +
                "    (minValue links ?ARG ?N)\n" +
                "    (links @ARGS)\n" +
                "    (equal ?VAL\n" +
                "      (ListOrderFn\n" +
                "        (ListFn @ARGS) ?ARG)))\n" +
                "  (greaterThan ?VAL ?N))";

        Formula f = new Formula();
        f.read(stmt1);

        //RowVars.DEBUG = true;
        List<Formula> results = RowVars.expandRowVars(kb, f);
        String result = results.get(0).getFormula();
        String expected = "(=>\n" +
                "  (and\n" +
                "    (minValue links ?ARG ?N)\n" +
                "    (links ?ARGS2 ?ARGS3 ?ARGS4)\n" +
                "    (equal ?VAL\n" +
                "      (ListOrderFn\n" +
                "        (ListFn ?ARGS2 ?ARGS3 ?ARGS4) ?ARG)))\n" +
                "  (greaterThan ?VAL ?N))";
        System.out.println("testLinks2(): result: " + result);
        System.out.println("testLinks2(): expected: " + expected);
        if (expected.equals(result))
            System.out.println("testLinks2(): success!");
        else
            System.out.println("testLinks2(): failure");
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testRowVarExp() {

        System.out.println("\n=========== testRowVarExp =================");
        String stmt = "(<=> (partition @ROW) (and (exhaustiveDecomposition @ROW) (disjointDecomposition @ROW)))";
        Formula f = new Formula();
        f.read(stmt);

        //RowVars.DEBUG = true;
        List<Formula> results = RowVars.expandRowVars(kb, f);
        System.out.println("testRowVarExp(: input: " + stmt);
        System.out.println("testRowVarExp(): results: " + results);
        System.out.println("testRowVarExp(): results size: " + results.size());
        System.out.println("testRowVarExp(): expected: " + 7);
        if (results.size() == RowVars.MAX_ARITY)
            System.out.println("testLinks(): success!");
        else
            System.out.println("testLinks(): failure");
        assertThat(results.size()).isEqualTo(RowVars.MAX_ARITY);
    }
}