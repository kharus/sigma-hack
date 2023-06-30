package com.articulate.sigma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("com.articulate.sigma.TopOnly")
public class FormulaUtilITCase {

    @Test
    public void testToProlog() {

        String stmt = "(birthplace ?animal ?LOC)";
        Formula f = new Formula(stmt);
        String result = FormulaUtil.toProlog(f);
        System.out.println("FormulaUtilITCase.testToProlog(): " + result);
        assertThat(result).isEqualTo("birthplace(?animal,?LOC)");
    }

}