package com.articulate.sigma.wordnet;

import com.articulate.sigma.KB;
import com.articulate.sigma.KBmanager;
import com.articulate.sigma.KBmanagerTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: Test the WordNet class more thoroughly. Start with the test methods called in main( ).

@SpringBootTest
@Tag("com.articulate.sigma.TopOnly")
@ActiveProfiles("TopOnly")
@Import(KBmanagerTestConfiguration.class)
public class WordNetITCase {

    private KB kb;

    @Autowired
    private KBmanager kbManager;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(kbManager.getPref("sumokbname"));
    }

    @Test
    public void testVerbRootFormGoing() {

        String actual = WordNet.wn.verbRootForm("going", "going");
        String expected = "go";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testVerbRootFormDriving() {

        String actual = WordNet.wn.verbRootForm("driving", "driving");
        String expected = "drive";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGetSingularFormGo() {

        String actual = WordNetUtilities.verbPlural("go");
        String expected = "goes";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGetSingularFormDrive() {

        String actual = WordNetUtilities.verbPlural("drive");
        String expected = "drives";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testIsValidKey() {

        assertThat(WordNetUtilities.isValidKey("stick_together_VB_1")).isTrue();
    }

    @Test
    public void checkWordsToSenses() {

        List<String> runs = WordNet.wn.wordsToSenseKeys.get("run");
        System.out.println("run " + runs);
        assertThat(runs.contains("run_NN_7")).isTrue();
        System.out.println("TV " + WordNet.wn.wordsToSenseKeys.get("TV"));
        System.out.println("tv " + WordNet.wn.wordsToSenseKeys.get("tv"));
        System.out.println("106277280 " + WordNet.wn.synsetsToWords.get("106277280"));
        System.out.println("106277280 " + WordNet.wn.reverseSenseIndex.get("106277280"));
        System.out.println("court " + WordNet.wn.wordsToSenseKeys.get("court"));
        System.out.println("state " + WordNet.wn.wordsToSenseKeys.get("state"));
        System.out.println("labor " + WordNet.wn.wordsToSenseKeys.get("labor"));
        System.out.println("phase " + WordNet.wn.wordsToSenseKeys.get("phase"));
        System.out.println("craft " + WordNet.wn.wordsToSenseKeys.get("craft"));
    }
}