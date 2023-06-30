package com.articulate.sigma;

import org.junit.jupiter.api.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThat;

@Category(TopOnly.class)
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