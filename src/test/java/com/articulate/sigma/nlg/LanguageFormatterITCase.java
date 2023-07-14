package com.articulate.sigma.nlg;

import com.articulate.sigma.KB;
import com.articulate.sigma.KBmanager;
import com.articulate.sigma.KBmanagerTestConfiguration;
import com.articulate.sigma.utils.StringUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LanguageFormatter tests NOT targeted toward the htmlParaphrase( ) method.
 * See LanguageFormatterHtmlParaphraseITCase for tests that invoke this method.
 */
@SpringBootTest
@Tag("com.articulate.sigma.TopOnly")
@ActiveProfiles("TopOnly")
@Import(KBmanagerTestConfiguration.class)
public class LanguageFormatterITCase {
    @Value("${sumokbname}")
    private String sumokbname;
    private KB kb;

    @Autowired
    private KBmanager kbManager;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(sumokbname);
    }

    @Disabled
    @Test
    public void testStatementParse() {
        String input = "(exists (?D ?H) (and (instance ?D Driving) (instance ?H Human) (agent ?D ?H)))";
        LanguageFormatter lf = new LanguageFormatter(input, kb.getFormatMap("EnglishLanguage"), kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");
        String actual = lf.paraphraseStatement(input, false, 0);
        assertThat(actual).isEqualTo("");
    }

    @Test
    public void testVariableReplaceBasic() {
        String form = "there exist ?D and ?H such that ?D is an &%instance$\"instance\" of &%Driving$\"driving\" and ?H is an &%instance$\"instance\" of &%Human$\"human\" and ?H is an &%agent$\"agent\" of ?D";
        Map<String, Set<String>> instanceMap = Maps.newHashMap();
        instanceMap.put("?D", Sets.newHashSet("Process"));
        instanceMap.put("?H", Sets.newHashSet("AutonomousAgent"));
        Map<String, Set<String>> classMap = Maps.newHashMap();

        String expected = "there exist &%Process$\"a  process\" and &%AutonomousAgent$\"an agent\" such that &%Process$\"the process\" is an &%instance$\"instance\" of &%Driving$\"driving\" and &%AutonomousAgent$\"the agent\" is an &%instance$\"instance\" of &%Human$\"human\" and &%AutonomousAgent$\"the agent\" is an &%agent$\"agent\" of &%Process$\"the process\"";

        String actual = LanguageFormatter.variableReplace(form, instanceMap, classMap, kb, "EnglishLanguage");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGenerateFormalNaturalLanguageIf() {
        LanguageFormatter formatter = new LanguageFormatter("", kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");

        List<String> translations = Lists.newArrayList("Socrates is a man", "Socrates is mortal");
        String actual = formatter.generateFormalNaturalLanguage(translations, "=>", false);
        actual = StringUtil.filterHtml(actual);

        assertThat(actual).isEqualTo("if Socrates is a man, then Socrates is mortal");

        actual = formatter.generateFormalNaturalLanguage(translations, "=>", true);
        actual = StringUtil.filterHtml(actual);

        assertThat(actual).isEqualTo("Socrates is mortal and ~{Socrates is a man}");
    }

    @Test
    public void testGenerateFormalNaturalLanguageIfAndOnlyIf() {
        LanguageFormatter formatter = new LanguageFormatter("", kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");

        List<String> translations = Lists.newArrayList("Socrates is a man", "Socrates is mortal");
        String actual = formatter.generateFormalNaturalLanguage(translations, "<=>", false);
        actual = StringUtil.filterHtml(actual);

        assertThat(actual).isEqualTo("Socrates is a man if and only if Socrates is mortal");

        actual = formatter.generateFormalNaturalLanguage(translations, "<=>", true);
        actual = StringUtil.filterHtml(actual);

        assertThat(actual).isEqualTo("Socrates is mortal or ~{ Socrates is a man } or Socrates is a man or ~{ Socrates is mortal }");
    }

    @Test
    public void testGenerateFormalNaturalLanguageAnd() {
        LanguageFormatter formatter = new LanguageFormatter("", kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");

        List<String> translations = Lists.newArrayList("Socrates is a man", "Socrates is mortal");
        String actual = formatter.generateFormalNaturalLanguage(translations, "and", false);
        actual = StringUtil.filterHtml(actual);

        assertThat(actual).isEqualTo("Socrates is a man and Socrates is mortal");

        actual = formatter.generateFormalNaturalLanguage(translations, "and", true);
        actual = StringUtil.filterHtml(actual);

        assertThat(actual).isEqualTo("~{ Socrates is a man } or ~{ Socrates is mortal }");
    }

    @Test
    public void testGenerateFormalNaturalLanguageOr() {
        LanguageFormatter formatter = new LanguageFormatter("", kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");

        List<String> translations = Lists.newArrayList("Socrates is a man", "Socrates is mortal");
        String actual = formatter.generateFormalNaturalLanguage(translations, "or", false);
        actual = StringUtil.filterHtml(actual);

        assertThat(actual).isEqualTo("Socrates is a man or Socrates is mortal");

        actual = formatter.generateFormalNaturalLanguage(translations, "or", true);
        actual = StringUtil.filterHtml(actual);

        assertThat(actual).isEqualTo("Socrates is a man and Socrates is mortal");
    }

    /**
     * Test various LanguageFormatter methods that are employed in "informal" NLG.
     */
    @Test
    public void testInformalNLGWithHtml() {
        String form = "<ul><li>if ?H drives,</li><li>then ?H sees</li></ul>";

        // Verify variableReplace( ).
        Map<String, Set<String>> instanceMap = Maps.newHashMap(ImmutableMap.of("?S", Sets.newHashSet("Seeing"),
                "?H", Sets.newHashSet("Human"), "?D", Sets.newHashSet("Driving")));
        Map<String, Set<String>> classMap = Maps.newHashMap();

        String expected = "<ul><li>if &%Human$\"a  human\" drives,</li><li>then &%Human$\"the human\" sees</li></ul>";
        String variableReplaceOutput = LanguageFormatter.variableReplace(form, instanceMap, classMap, kb, "EnglishLanguage");
        assertThat(variableReplaceOutput).isEqualTo(expected);

        // Verify resolveFormatSpecifiers( ).
        expected = "<ul><li>if <a href=\"&term=Human\">a  human</a> drives,</li><li>then <a href=\"&term=Human\">the human</a> sees</li></ul>";
        String resolveFormatSpecifiersOutput = NLGUtils.resolveFormatSpecifiers(variableReplaceOutput, "");
        assertThat(resolveFormatSpecifiersOutput).isEqualTo(expected);
    }

    /**
     * Test various LanguageFormatter methods that are employed in "informal" NLG.
     */
    @Test
    public void testInformalNLGWithoutHtml() {
        String form = "if ?H drives, then ?H sees";

        // Verify variableReplace( ).
        Map<String, Set<String>> instanceMap = Maps.newHashMap(ImmutableMap.of("?S", Sets.newHashSet("Seeing"),
                "?H", Sets.newHashSet("Human"), "?D", Sets.newHashSet("Driving")));
        Map<String, Set<String>> classMap = Maps.newHashMap();

        String expected = "if &%Human$\"a  human\" drives, then &%Human$\"the human\" sees";
        String variableReplaceOutput = LanguageFormatter.variableReplace(form, instanceMap, classMap, kb, "EnglishLanguage");
        assertThat(variableReplaceOutput).isEqualTo(expected);

        // Verify resolveFormatSpecifiers( ).
        expected = "if a human drives, then the human sees";
        String resolveFormatSpecifiersOutput = NLGUtils.resolveFormatSpecifiers(variableReplaceOutput, "");
        assertThat(StringUtil.filterHtml(resolveFormatSpecifiersOutput)).isEqualTo(expected);
    }

}