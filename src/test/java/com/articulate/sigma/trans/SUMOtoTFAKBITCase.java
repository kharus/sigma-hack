/*
 * This software is released under the GNU Public License
 * <http://www.gnu.org/copyleft/gpl.html>.
 * Copyright 2019 Infosys
 * adam.pease@infosys.com
 */
package com.articulate.sigma.trans;

import com.articulate.sigma.Formula;
import com.articulate.sigma.IntegrationTestBase;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class SUMOtoTFAKBITCase extends IntegrationTestBase {

    private static SUMOKBtoTFAKB skbtfakb = null;

    @BeforeClass
    public static void init() {

        SUMOtoTFAform.initOnce();
        SUMOtoTFAform.setNumericFunctionInfo();
        skbtfakb = new SUMOKBtoTFAKB();
        SUMOformulaToTPTPformula.lang = "tff";
        skbtfakb.initOnce();
    }

    @Test
    public void testPartition() {

        System.out.println();
        System.out.println("\n======================== SUMOtoTFAKBITCase.testPartition(): ");
        ArrayList<String> sig = SUMOtoTFAform.relationExtractNonNumericSig("partition__5");
        System.out.println(sig);
        String expectedRes = "[, Class, Class, Class, Class, Class]";
        String result = sig.toString();
        System.out.println("testDynamicSortDef(): result: " + result);
        System.out.println("testDynamicSortDef(): expect: " + expectedRes);
        if (expectedRes.equals(result))
            System.out.println("testPartition(): Success!");
        else
            System.out.println("testPartition(): fail");
        assertEquals(expectedRes, result);
    }

    @Test
    public void testDynamicSortDef() {

        System.out.println();
        System.out.println("\n======================== SUMOtoTFAKBITCase.testDynamicSortDef(): ");

        String result = SUMOtoTFAform.sortFromRelation("ListFn__2Fn__2ReFn");
        String expectedRes = "tff(listFn__2Fn__2ReFn_sig,type,s__ListFn__2Fn__2ReFn : (  $i * $real  ) > $i ).";
        System.out.println("testDynamicSortDef(): result: " + result);
        System.out.println("testDynamicSortDef(): expect: " + expectedRes);
        if (expectedRes.equals(result))
            System.out.println("testDynamicSortDef(): Success!");
        else
            System.out.println("testDynamicSortDef(): fail");
        assertEquals(expectedRes, result);
    }

    @Test
    public void testMissingSort() {

        SUMOtoTFAform.debug = true;
        SUMOKBtoTFAKB.debug = true;
        System.out.println();
        System.out.println("\n======================== SUMOtoTFAKBITCase.testMissingSort(): ");
        SUMOtoTFAform stfa = new SUMOtoTFAform();
        String f = "! [V__ROW1 : $i,V__ROW2 : $real,V__CLASS : $i,V__NUMBER : $int] : " +
                "((s__instance(V__ROW1, s__Human) & s__instance(V__CLASS, s__Class)) => " +
                "(s__domain__2In(s__intelligenceQuotient__m, V__NUMBER, V__CLASS) & " +
                "s__instance(s__intelligenceQuotient__m, s__Predicate) & " +
                "s__intelligenceQuotient__2Re(V__ROW1, V__ROW2)) => " +
                "s__instance(s__ListOrderFn__2InFn(s__ListFn__2ReFn(V__ROW1, V__ROW2), V__NUMBER), V__CLASS))";
        HashSet<String> result = stfa.missingSorts(new Formula(f));
        String expectedRes = "tff(listFn__2ReFn_sig,type,s__ListFn__2ReFn : (  $i * $real  ) > $i ).";
        String resultStr = "";
        if (result != null && result.size() > 0)
            resultStr = result.iterator().next();
        System.out.println("testMissingSort(): result: " + resultStr);
        System.out.println("testMissingSort(): expect: " + expectedRes);
        if (result != null && result.contains(expectedRes))
            System.out.println("testMissingSort(): Success!");
        else
            System.out.println("testMissingSort(): fail");
        assertTrue(result.contains(expectedRes));
    }
}
