package com.articulate.sigma.verbnet;

import com.articulate.sigma.IntegrationTestBase;
import com.articulate.sigma.KB;
import com.articulate.sigma.KBmanager;
import com.articulate.sigma.KBmanagerTestConfiguration;
import com.articulate.sigma.utils.StringUtil;
import com.articulate.sigma.wordnet.WordNet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("com.articulate.sigma.MidLevel")
@ActiveProfiles("MidLevel")
@Import(KBmanagerTestConfiguration.class)
public class VerbNetITCase {

    @Autowired
    private KBmanager kbManager;

    @Test
    @Disabled
    public void testTerm() {

        String term = "SocialInteraction";
        TreeMap<String, String> tm = WordNet.wn.getWordsFromTerm(term);
        System.out.println("testTerm(): words: " + tm);
        String verbs = VerbNet.formatVerbs(tm);
        System.out.println("testTerm(): verbs: " + verbs);
        assertThat(StringUtil.emptyString(verbs)).isFalse();
    }

    @Test
    @Disabled
    public void testWordList() {

        TreeMap<String, List<String>> tm = WordNet.wn.getSenseKeysFromWord("object");
        System.out.println("testWordList(): senses: " + tm);
        String verbs = VerbNet.formatVerbsList(tm);
        System.out.println("testWordList(): verbs: " + verbs);
        assertThat(StringUtil.emptyString(verbs)).isFalse();
    }
}