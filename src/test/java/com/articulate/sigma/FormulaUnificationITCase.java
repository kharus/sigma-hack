package com.articulate.sigma;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("com.articulate.sigma.TopOnly")
public class FormulaUnificationITCase extends UnitTestBase {

    @Test
    public void test() {
        String f1Text = """
                (=>
                    (instance ?C WalkingCane)
                    (hasPurpose ?C
                       (exists (?W)
                          (and
                                (instance ?W Walking)
                                (instrument ?W ?C)))))""";

        String f2Text = """
                (=>
                    (instance ?C WalkingCane)
                      (hasPurpose ?C
                      (exists (?W)
                        (and
                        (instance ?W Walking)
                        (instrument ?W ?C)))))""";

        Formula f1 = new Formula();
        f1.read(f1Text);
        Formula f2 = new Formula();
        f2.read(f2Text);

        assertThat(f1.unifyWith(f2)).isTrue();
    }
}
