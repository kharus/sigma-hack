/*
 * Copyright 2014-2015 IPsoft
 *
 * Author: Andrei Holub andrei.holub@ipsoft.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program ; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA  02111-1307 USA
 */
package com.articulate.sigma.wordnet;

import com.articulate.sigma.TopOnly;
import com.articulate.sigma.UnitTestBase;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Category(TopOnly.class)
public class MultiWordsITCase extends UnitTestBase {

    @Test
    public void testVerbMultiWordKickBucket1() {

        List<String> input = Lists.newArrayList("kick", "the", "bucket");
        List<String> synset = Lists.newArrayList();
        String w = WordNet.wn.getMultiWords().findMultiWord(input);
        String s = WordNetUtilities.wordsToSynsets(w).iterator().next();
        assertThat(s).isEqualTo("200358431");
    }

    @Test
    public void testVerbMultiWordKickBucket2() {

        List<String> input = Lists.newArrayList("kick", "the", "bucket", "down", "the", "road");
        List<String> synset = Lists.newArrayList();
        String w = WordNet.wn.getMultiWords().findMultiWord(input);
        String s = WordNetUtilities.wordsToSynsets(w).iterator().next();
        assertThat(s).isEqualTo("200358431");
    }

    @Test
    public void testVerbMultiWordCatsAndDogs1() {

        List<String> input = Lists.newArrayList("many", "raining", "cats", "and", "dogs", "and", "sheep");
        List<String> synset = Lists.newArrayList();
        int endIndex = WordNet.wn.getMultiWords().findMultiWord(input, 0, synset);

        assertThat(endIndex).isEqualTo(0);
        assertThat(synset.size()).isEqualTo(0);
    }

    @Test
    public void testVerbMultiWordCatsAndDogs2() {

        List<String> input = Lists.newArrayList("many", "raining", "cats", "and", "dogs", "and", "sheep");
        List<String> synset = Lists.newArrayList();
        int endIndex = WordNet.wn.getMultiWords().findMultiWord(input, 1, synset);

        assertThat(endIndex).isEqualTo(5);
        assertThat(synset.size()).isEqualTo(1);
    }

    @Test
    public void testVerbMultiWordCatsAndDogs3() {

        List<String> input = Lists.newArrayList("cats", "and", "dogs", "and", "sheep");
        List<String> synset = Lists.newArrayList();
        int endIndex = WordNet.wn.getMultiWords().findMultiWord("rain", "rain", input, synset);

        assertThat(endIndex).isEqualTo(4);
        assertThat(synset.size()).isEqualTo(1);
        assertThat(synset.get(0)).isEqualTo("202758033");
    }

    @Test
    public void testVerbMultiWordCatsAndDogs4() {

        List<String> input = Lists.newArrayList("cats", "and", "dogs", "and", "sheep");
        List<String> synset = Lists.newArrayList();
        // Incorrect root form
        int endIndex = WordNet.wn.getMultiWords().findMultiWord("raining", "raining", input, synset);

        assertThat(endIndex).isEqualTo(0);
        assertThat(synset.size()).isEqualTo(0);
    }

    @Test
    public void testVerbMultiWordCatsAndDogs5() {

        List<String> input = Lists.newArrayList("raining", "cats", "and", "dogs", "and", "sheep");
        List<String> synset = Lists.newArrayList();
        // Incorrect root form
        String result = WordNet.wn.getMultiWords().findMultiWord(input);

        assertThat(result).isEqualTo("raining_cats_and_dogs");
        //WordNet.wn.getSUMO
    }

    @Test
    public void testNounMultiWord1() {

        List<String> input = Lists.newArrayList("father");
        List<String> synset = Lists.newArrayList();
        // Incorrect root form
        int endIndex = WordNet.wn.getMultiWords().findMultiWord("found", "found", input, synset);

        assertThat(endIndex).isEqualTo(0);
        assertThat(synset.size()).isEqualTo(0);
    }

    @Test
    public void testNounMultiWord2() {

        List<String> input = Lists.newArrayList("father");
        List<String> synset = Lists.newArrayList();
        int endIndex = WordNet.wn.getMultiWords().findMultiWord("founding", "founding", input, synset);

        assertThat(endIndex).isEqualTo(2);
        assertThat(synset.size()).isEqualTo(1);
        assertThat(synset.get(0)).isEqualTo("110107303");
    }
}