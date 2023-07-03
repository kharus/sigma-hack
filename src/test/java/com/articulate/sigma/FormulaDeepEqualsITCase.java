package com.articulate.sigma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("com.articulate.sigma.TopOnly")
@ActiveProfiles("TopOnly")
@Import(KBmanagerTestConfiguration.class)
public class FormulaDeepEqualsITCase {

    @Autowired
    FormulaDeepEqualsService deepEqualsService;
    private KB kb;
    @Autowired
    private KBmanager kbManager;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(kbManager.getPref("sumokbname"));
    }

    @Test
    public void testDeepEquals() {

        Formula f1 = new Formula();
        f1.read("(=>" +
                "    (instance ?C WalkingCane)" +
                "    (hasPurpose ?C" +
                "        (exists (?W)" +
                "            (and" +
                "                (instance ?W Walking)" +
                "                (instrument ?W ?C)))))");

        Formula f2 = new Formula();
        f2.read("(=>" +
                "    (instance ?C WalkingCane)" +
                "    (hasPurpose ?C" +
                "        (exists (?W)" +
                "            (and" +
                "                (instance ?W Walking)" +
                "                (instrument ?W ?C)))))");

        //testing equal formulas
        assertThat(deepEqualsService.deepEquals(f1, f1)).isTrue();

        //testing formulas that differ in variable reference
        f2.read("(or (not (instance ?X6 WalkingCane)) (hasPurpose ?X4 (and (instance (SkFn2 ?X6) Walking) (instrument (SkFn2 ?X6) ?X6))))");
        assertThat(deepEqualsService.deepEquals(f1, f2)).isTrue();

        //testing unequal formulas
        f1 = new Formula();
        f1.read("(=>" +
                "    (instance ?C WalkingCane)" +
                "    (hasPurpose ?C" +
                "        (exists (?W)" +
                "            (and" +
                "                (instance ?W Walking)" +
                "                (instrument ?W ?C)))))");

        f2 = new Formula();
        f2.read("(=>" +
                "    (instance ?C WalkingCane)" +
                "    (hasPurpose ?C" +
                "        (exists (?W)" +
                "            (and" +
                "                (instance ?W Running)" +
                "                (instrument ?W ?C)))))");

        assertThat(deepEqualsService.deepEquals(f1, f2)).isFalse();

        //testing commutative terms
        f1 = new Formula();
        f1.read("(=>" +
                "    (instance ?C WalkingCane)" +
                "    (hasPurpose ?C" +
                "        (exists (?W)" +
                "            (and" +
                "                (instance ?W Walking)" +
                "                (instrument ?W ?C)))))");

        f2 = new Formula();
        f2.read("(=>" +
                "    (instance ?C WalkingCane)" +
                "    (hasPurpose ?C" +
                "        (exists (?W)" +
                "            (and" +
                "                (instrument ?W ?C)" +
                "                (instance ?W Walking)))))");

        assertThat(deepEqualsService.deepEquals(f1, f2)).isTrue();

    }

    @Test
    public void testDeepEquals2() {

        System.out.println("============= FormulaDeepEqualsITCase.testDeepEquals2 ==================");
        Formula f1 = new Formula();
        f1.read("(exists (?Leigh-1 ?baby-4 ?blankets-6 ?swaddled-2)\n" +
                "  (and\n" +
                "    (orientation ?swaddled-2 ?blankets-6 Inside)\n" +
                "    (destination ?swaddled-2 ?blankets-6)\n" +
                "    (names ?Leigh-1 \"Leigh\")\n" +
                "    (instance ?baby-4 HumanBaby)\n" +
                "    (agent ?swaddled-2 ?Leigh-1)\n" +
                "    (patient ?swaddled-2 ?baby-4)\n" +
                "    (earlier\n" +
                "      (WhenFn ?swaddled-2) Now)\n" +
                "    (instance ?blankets-6 Blanket)\n" +
                "    (instance ?Leigh-1 Human)\n" +
                "    (instance ?swaddled-2 Covering)))");

        Formula f2 = new Formula();
        f2.read("(exists (?Leigh-1 ?baby-4 ?blankets-6 ?swaddled-2)\n" +
                "  (and\n" +
                "    (orientation ?swaddled-2 ?blankets-6 Inside)\n" +
                "    (destination ?swaddled-2 ?blankets-6)\n" +
                "    (names ?Leigh-1 \"Leigh\")\n" +
                "    (instance ?swaddled-2 Covering)\n" +
                "    (agent ?swaddled-2 ?Leigh-1)\n" +
                "    (patient ?swaddled-2 ?baby-4)\n" +
                "    (earlier\n" +
                "      (WhenFn ?swaddled-2) Now)\n" +
                "    (instance ?blankets-6 Blanket)\n" +
                "    (instance ?Leigh-1 Human)\n" +
                "    (instance ?baby-4 HumanBaby)) )");
        //testing equal formulas
        assertThat(deepEqualsService.deepEquals(f1, f2)).isTrue();
    }

    @Test
    public void testDeepEqualsErrorCases() {

        Formula f = new Formula();
        f.read("(<=> (instance ?REL SymmetricRelation) (forall (?INST1 ?INST2) (=> (?REL ?INST1 ?INST2) (?REL ?INST2 ?INST1)))))");

        assertThat(deepEqualsService.deepEquals(f, null)).isFalse();

        Formula compared = new Formula();
        assertThat(deepEqualsService.deepEquals(f, compared)).isFalse();

        compared.read("");
        assertThat(deepEqualsService.deepEquals(f, compared)).isFalse();

        compared.read("()");
        assertThat(deepEqualsService.deepEquals(f, compared)).isFalse();

        assertThat(deepEqualsService.deepEquals(f, f)).isTrue();
    }

    @Test
    public void testLogicallyEqualsErrorCases() {

        Formula f = new Formula();
        f.read("(<=> (instance ?REL SymmetricRelation) (forall (?INST1 ?INST2) (=> (?REL ?INST1 ?INST2) (?REL ?INST2 ?INST1)))))");

        assertThat(deepEqualsService.logicallyEquals(f, null)).isFalse();

        Formula compared = new Formula();
        assertThat(deepEqualsService.logicallyEquals(f, compared)).isFalse();

        compared.read("");
        assertThat(deepEqualsService.logicallyEquals(f, compared)).isFalse();

        compared.read("()");
        assertThat(deepEqualsService.logicallyEquals(f, compared)).isFalse();

        assertThat(deepEqualsService.logicallyEquals(f, f)).isTrue();
    }

    @Test
    public void testUnifyWith() {

        Formula f1 = new Formula();
        f1.read("(=>" +
                "    (instance ?C WalkingCane)" +
                "    (hasPurpose ?C" +
                "        (exists (?W)" +
                "            (and" +
                "                (instance ?W Walking)" +
                "                (instrument ?W ?C)))))");

        Formula f2 = new Formula();
        f2.read("(=>" +
                "    (instance ?C WalkingCane)" +
                "    (hasPurpose ?C" +
                "        (exists (?W)" +
                "            (and" +
                "                (instance ?W Walking)" +
                "                (instrument ?W ?C)))))");

        //testing equal formulas
        assertThat(deepEqualsService.unifyWith(f1,f2)).isTrue();

        //testing formulas that differ in variable reference
        f2.read("(or (not (instance ?X6 WalkingCane)) (hasPurpose ?X4 (and (instance (SkFn2 ?X6) Walking) (instrument (SkFn2 ?X6) ?X6))))");
        assertThat(deepEqualsService.unifyWith(f1,f2)).isFalse();

        //testing unequal formulas
        f1 = new Formula();
        f1.read("(=>" +
                "    (instance ?C WalkingCane)" +
                "    (hasPurpose ?C" +
                "        (exists (?W)" +
                "            (and" +
                "                (instance ?W Walking)" +
                "                (instrument ?W ?C)))))");

        f2 = new Formula();
        f2.read("(=>" +
                "    (instance ?C WalkingCane)" +
                "    (hasPurpose ?C" +
                "        (exists (?W)" +
                "            (and" +
                "                (instance ?W Running)" +
                "                (instrument ?W ?C)))))");

        assertThat(deepEqualsService.unifyWith(f1,f2)).isFalse();

        //testing commutative terms
        f1 = new Formula();
        f1.read("(=>" +
                "    (instance ?C WalkingCane)" +
                "    (hasPurpose ?C" +
                "        (exists (?W)" +
                "            (and" +
                "                (instance ?W Walking)" +
                "                (instrument ?W ?C)))))");

        f2 = new Formula();
        f2.read("(=>" +
                "    (instance ?C WalkingCane)" +
                "    (hasPurpose ?C" +
                "        (exists (?W)" +
                "            (and" +
                "                (instrument ?W ?C)" +
                "                (instance ?W Walking)))))");

        assertThat(deepEqualsService.unifyWith(f1,f2)).isTrue();

    }

    /**
     * Formula.unifyWith is deprecated
     */
    @Test
    @Disabled
    public void testUnifyWithMiscPredicates() {

        String s1 = "(=> (and (instance ?X4 Dog) (instance ?X5 Cat)) (equal ?X4 ?X5))";
        Formula f1 = new Formula();
        f1.read(s1);

        String s2 = "(=> (and (instance ?X11 Dog) (instance ?X12 Cat)) (equal ?X12 ?X11))";
        Formula f2 = new Formula();
        f2.read(s2);

        assertThat(deepEqualsService.unifyWith(f1,f2)).isTrue();
    }

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

        Formula actual = fp.addTypeRestrictions(f, kb);

        assertThat(deepEqualsService.unifyWith(expected,actual)).isTrue();
    }

    @Test
    public void testUnification() {
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

        assertThat(deepEqualsService.unifyWith(f1,f2)).isTrue();
    }
}
