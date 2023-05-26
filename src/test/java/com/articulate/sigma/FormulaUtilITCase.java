package com.articulate.sigma;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FormulaUtilITCase {

    @Test
    public void testToProlog() {

        String stmt = "(birthplace ?animal ?LOC)";
        Formula f = new Formula(stmt);
        String result = FormulaUtil.toProlog(f);
        System.out.println("FormulaUtilITCase.testToProlog(): " + result);
        assertEquals("birthplace(?animal,?LOC)", result);
    }

}