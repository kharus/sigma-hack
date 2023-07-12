package com.articulate.sigma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("com.articulate.sigma.TopOnly")
public class FormatITCase extends UnitTestBase {

    @Test
    public void testNegativePositiveFormat() {

        Map<String, String> phraseMap = SigmaTestBase.kb.getFormatMap("EnglishLanguage");

        StringBuilder problems = new StringBuilder("\n");
        for (Map.Entry<String, String> entry : phraseMap.entrySet()) {
            String value = entry.getValue();
            if (value.matches(".*%(p|n)\\(.*")) {
                problems.append("Syntax error. '").append(entry.getKey()).append("' has a p (pos) or n (neg) followed by a left paren instead of a left curly brace: '").append(value).append("'\n");
            }
            // Note use of ? for non-greedy matching.
            if (value.matches(".*%(p|n)\\{[^\\}]+?\\).*")) {
                problems.append("Syntax error. '").append(entry.getKey()).append("' has a p (pos) or n (neg) followed by a right paren instead of a right curly brace: '").append(value).append("'\n");
            }
        }

        // If a trimmed problems list is not empty, then all the problems will be printed out.
        assertThat(problems.toString().trim().isEmpty())
                .as(problems.toString())
                .isTrue();
    }

    @Test
    public void testFormatMatchingCharacters() {

        Map<String, String> phraseMap = SigmaTestBase.kb.getFormatMap("EnglishLanguage");

        StringBuilder problems = new StringBuilder("\n");
        for (Map.Entry<String, String> entry : phraseMap.entrySet()) {
            String value = entry.getValue();
            int leftParensCt = value.length() - value.replace("(", "").length();
            int rightParensCt = value.length() - value.replace(")", "").length();
            if (leftParensCt != rightParensCt) {
                problems.append("Syntax error. '").append(entry.getKey()).append("' contains unmatching parentheses: '").append(value).append("'\n");
            }
            int leftCurlyCt = value.length() - value.replace("{", "").length();
            int rightCurlyCt = value.length() - value.replace("}", "").length();
            if (leftCurlyCt != rightCurlyCt) {
                problems.append("Syntax error. '").append(entry.getKey()).append("' contains unmatching curly braces: '").append(value).append("'\n");
            }
            int leftSquareBracket = value.length() - value.replace("[", "").length();
            int rightSquareBracket = value.length() - value.replace("]", "").length();
            if (leftSquareBracket != rightSquareBracket) {
                problems.append("Syntax error. '").append(entry.getKey()).append("' contains unmatching square brackets: '").append(value).append("'\n");
            }
        }

        // If a trimmed problems list is not empty, then all the problems will be printed out.
        assertThat(problems.toString().trim().isEmpty())
                .as(problems.toString())
                .isTrue();
    }
}
