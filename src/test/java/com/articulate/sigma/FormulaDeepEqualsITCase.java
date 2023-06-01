package com.articulate.sigma;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by sserban on 2/11/15.
 */
@SpringBootTest
public class FormulaDeepEqualsITCase  {
    @Autowired
    private KBmanager kbManager;

    @Test
    public void testLogicallyEqualsPerformance() {

        String stmt = "(=> (forall (?ELEMENT) (<=> (element ?ELEMENT ?SET1) " +
                "(element ?ELEMENT ?SET2))) (equal ?SET1 ?SET2))";
        Formula f = new Formula();
        f.read(stmt);
        FormulaPreprocessor fp = new FormulaPreprocessor();

        Formula expected = new Formula();
        String expectedString = "(=> (and (instance ?SET1 Set) (instance ?SET2 Set)) " +
                "(=> (forall (?ELEMENT) (<=> (element ?ELEMENT ?SET1) (element ?ELEMENT ?SET2))) " +
                "(equal ?SET1 ?SET2)))";

        expected.read(expectedString);

        KB kb = kbManager.getKB(kbManager.getPref("sumokbname"));
        Formula actual = fp.addTypeRestrictions(f, kb);
        System.out.println("testLogicallyEqualsPerformance: expected: " + expected);
        System.out.println("testLogicallyEqualsPerformance: actual: " + actual);
        long start = System.nanoTime();
//        assertTrue(expected.logicallyEquals(actual));
        assertTrue(expected.unifyWith(actual));
        long stop = System.nanoTime();
        System.out.println("Execution time (in microseconds): " + ((stop - start) / 1000));
    }
}
