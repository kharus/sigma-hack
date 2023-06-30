package com.articulate.sigma.nlg;

import com.google.common.collect.Lists;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NLGStringUtilsTest {

    @Test
    public void testConcatenateNoInput() {
        String expected = "";
        String actual = NLGStringUtils.concatenateWithCommas(Lists.newArrayList());
        assertThat(actual).isEqualTo(expected);

        expected = "";
        actual = NLGStringUtils.concatenateWithCommas(null);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testOneItem() {
        String expected = "one";
        String actual = NLGStringUtils.concatenateWithCommas(Lists.newArrayList("one"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testTwoItems() {
        String expected = "one and two";
        String actual = NLGStringUtils.concatenateWithCommas(Lists.newArrayList("one", "two"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testThreeItems() {
        String expected = "one, two and three";
        String actual = NLGStringUtils.concatenateWithCommas(Lists.newArrayList("one", "two", "three"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testSixItems() {
        String expected = "one, two, three, four, five and six";
        String actual = NLGStringUtils.concatenateWithCommas(Lists.newArrayList("one", "two", "three", "four", "five", "six"));
        assertThat(actual).isEqualTo(expected);
    }
}
