package com.articulate.sigma.wordnet;

import com.articulate.sigma.TopOnly;
import com.articulate.sigma.UnitTestBase;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: Test the WordNet class more thoroughly. Start with the test methods called in main( ).

@Category(TopOnly.class)
public class WordNetITCase extends UnitTestBase {

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