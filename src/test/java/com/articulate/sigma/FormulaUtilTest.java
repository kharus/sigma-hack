package com.articulate.sigma;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FormulaUtilTest {

    @Test
    public void testToProlog() {

        String stmt = "(birthplace ?animal ?LOC)";
        Formula f = new Formula(stmt);
        String result = FormulaUtil.toProlog(f);
        System.out.println("FormulaUtilITCase.testToProlog(): " + result);
        assertThat(result).isEqualTo("birthplace(?animal,?LOC)");
    }

}