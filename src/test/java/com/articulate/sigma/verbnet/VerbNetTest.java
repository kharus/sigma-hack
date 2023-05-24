package com.articulate.sigma.verbnet;

import com.articulate.sigma.IntegrationTestBase;
import com.articulate.sigma.utils.StringUtil;
import com.articulate.sigma.wordnet.WordNet;
import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VerbNetTest extends IntegrationTestBase {

    @Test
    public void testTerm() {

        String term = "SocialInteraction";
        TreeMap<String, String> tm = WordNet.wn.getWordsFromTerm(term);
        System.out.println("testTerm(): words: " + tm);
        String verbs = VerbNet.formatVerbs(tm);
        System.out.println("testTerm(): verbs: " + verbs);
        assertFalse(StringUtil.emptyString(verbs));
    }

    @Test
    public void testWordList() {

        TreeMap<String, ArrayList<String>> tm = WordNet.wn.getSenseKeysFromWord("object");
        System.out.println("testWordList(): senses: " + tm);
        String verbs = VerbNet.formatVerbsList(tm);
        System.out.println("testWordList(): verbs: " + verbs);
        assertFalse(StringUtil.emptyString(verbs));
    }
}