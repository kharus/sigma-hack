package com.articulate.sigma.nlg;

import com.articulate.sigma.*;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Tag("com.articulate.sigma.TopOnly")
@ActiveProfiles("TopOnly")
@Import(KBmanagerTestConfiguration.class)
public class NLGUtilsITCase {
    static final String SIGMA_HOME = System.getenv("SIGMA_HOME");
    public static final String KB_PATH = (new File(SIGMA_HOME, "KBs")).getAbsolutePath();

    @Autowired
    private KBmanager kbManager;

    @Test
    public void testReadKeywordMapNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            NLGUtils.readKeywordMap(null);
        });
    }

    @Test
    public void testReadKeywordMapNoExist() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            NLGUtils.readKeywordMap("/somePathThatDoesntExist/SomeFileThatDoesntExist.txt");
        });
    }

    /**
     * Verify no exception is thrown when the path is valid.
     */
    @Test
    public void testReadKeywordMapCorrectParameter() {
        NLGUtils.readKeywordMap(KB_PATH);
    }

    @Test
    public void testFormatListNoList() {
        String input = "";
        String actual = NLGUtils.formatList(input, "EnglishLanguage");
        String expected = "";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testFormatListNoLanguage() {
        String input = "?A ?B ?C";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            NLGUtils.formatList(input, "");
        });

    }

    @Test
    public void testFormat1List() {
        String input = "?A";
        String actual = NLGUtils.formatList(input, "EnglishLanguage");
        String expected = "?A";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testFormat2ListWithNoAnd() {
        String input = "?A ?B";
        String actual = NLGUtils.formatList(input, "EnglishLanguage");
        String expected = "?A and ?B";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testFormat2ListWithAnd() {
        String input = "?A and ?B";
        String actual = NLGUtils.formatList(input, "EnglishLanguage");
        String expected = "?A and ?B";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testFormat3ListWithNoAnd() {
        String input = "?A ?B ?C";
        String actual = NLGUtils.formatList(input, "EnglishLanguage");
        String expected = "?A, ?B and ?C";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testFormat3ListWithAnd() {
        String input = "?A ?B and ?C";
        String actual = NLGUtils.formatList(input, "EnglishLanguage");
        String expected = "?A, ?B and ?C";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testFormatListWithNoAndFrench() {
        String input = "?A ?B ?C";
        String actual = NLGUtils.formatList(input, "fr");
        String expected = "?A, ?B et ?C";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testFormatListWithAndFrench() {
        String input = "?A ?B et ?C";
        String actual = NLGUtils.formatList(input, "fr");
        String expected = "?A, ?B et ?C";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testCollectOrderedVariablesWithFormula1() {
        String stmt = "(exists (?he ?event)\n" +
                "                  (and\n" +
                "                    (instance ?event Transportation)\n" +
                "                    (instance ?he Human)\n" +
                "                    (agent ?event ?he)))";
        Formula formula = new Formula(stmt);

        List<String> actual = NLGUtils.collectOrderedVariables(formula.getFormula());

        List<String> expected = Lists.newArrayList("?he", "?event");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testCollectOrderedVariablesWithFormula2() {
        String stmt = "(agent ?event ?he)";
        Formula formula = new Formula(stmt);

        List<String> actual = NLGUtils.collectOrderedVariables(formula.getFormula());

        List<String> expected = Lists.newArrayList("?event", "?he");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testCollectOrderedVariablesWithFormula3() {
        String stmt = "(names \"John\" ?H)";
        Formula formula = new Formula(stmt);

        List<String> actual = NLGUtils.collectOrderedVariables(formula.getFormula());

        List<String> expected = Lists.newArrayList("?H");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testCollectOrderedVariablesWithString1() {
        String stmt = "(exists (?he ?event)\n" +
                "                  (and\n" +
                "                    (instance ?event Transportation)\n" +
                "                    (instance ?he Human)\n" +
                "                    (agent ?event ?he)))";
        List<String> actual = NLGUtils.collectOrderedVariables(stmt);

        List<String> expected = Lists.newArrayList("?he", "?event");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testCollectOrderedVariablesWithString2() {
        String stmt = "agent ?event ?he";
        List<String> actual = NLGUtils.collectOrderedVariables(stmt);

        List<String> expected = Lists.newArrayList("?event", "?he");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testCollectOrderedVariablesWithString3() {
        String stmt = "names \"John\" ?H";
        List<String> actual = NLGUtils.collectOrderedVariables(stmt);

        List<String> expected = Lists.newArrayList("?H");

        assertThat(actual).isEqualTo(expected);
    }

}
