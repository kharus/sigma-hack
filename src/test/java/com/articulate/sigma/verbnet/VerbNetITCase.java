package com.articulate.sigma.verbnet;

import com.articulate.sigma.IntegrationTestBase;
import com.articulate.sigma.utils.StringUtil;
import com.articulate.sigma.wordnet.WordNet;
import org.junit.Test;

import java.util.List;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

public class VerbNetITCase extends IntegrationTestBase {

    @Test
    public void testTerm() {

        String term = "SocialInteraction";
        TreeMap<String, String> tm = WordNet.wn.getWordsFromTerm(term);
        System.out.println("testTerm(): words: " + tm);
        String verbs = VerbNet.formatVerbs(tm);
        System.out.println("testTerm(): verbs: " + verbs);
        assertThat(StringUtil.emptyString(verbs)).isFalse();
    }

    @Test
    public void testWordList() {

        TreeMap<String, List<String>> tm = WordNet.wn.getSenseKeysFromWord("object");
        System.out.println("testWordList(): senses: " + tm);
        String verbs = VerbNet.formatVerbsList(tm);
        System.out.println("testWordList(): verbs: " + verbs);
        assertThat(StringUtil.emptyString(verbs)).isFalse();
    }
}