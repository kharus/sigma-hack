package com.articulate.sigma.wordnet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum WordNetRegexPatterns {

    // 0: WordNet.processPointers()
    PROCESS_POINTERS_1(Pattern.compile("^\\s*\\d\\d\\s\\S\\s\\d\\S\\s")),

    // 1: WordNet.processPointers()
    PROCESS_POINTERS_2(Pattern.compile("^([a-zA-Z0-9'._\\-]\\S*)\\s([0-9a-f])\\s")),

    // 2: WordNet.processPointers()
    PROCESS_POINTERS_3(Pattern.compile("^...\\s")),

    // 3: WordNet.processPointers()
    PROCESS_POINTERS_4(Pattern.compile("^(\\S\\S?)\\s([0-9]{8})\\s(.)\\s([0-9a-f]{4})\\s?")),

    // 4: WordNet.processPointers()
    PROCESS_POINTERS_5(Pattern.compile("^..\\s")),

    // 5: WordNet.processPointers()
    PROCESS_POINTERS_6(Pattern.compile("^\\+\\s(\\d\\d)\\s(\\d\\d)\\s?")),

    // 6: WordNet.readNouns()
    READ_NOUNS_1(Pattern.compile("^([0-9]{8})([\\S\\s]+)\\|\\s([\\S\\s]+?)\\s(\\(?\\&\\%\\S+[\\S\\s]+)$")),

    // 7: WordNet.readNouns()
    READ_NOUNS_2(Pattern.compile("^([0-9]{8})([\\S\\s]+)\\|\\s([\\S\\s]+)$")),

    // 8: WordNet.readNouns()
    READ_NOUNS_3(Pattern.compile("(\\S+)\\s+(\\S+)")),

    // 9: WordNet.readNouns()
    READ_NOUNS_4(Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)")),

    // 10: WordNet.readVerbs()
    READ_VERBS_1(Pattern.compile("^([0-9]{8})([^\\|]+)\\|\\s([\\S\\s]+?)\\s(\\(?\\&\\%\\S+[\\S\\s]+)$")),

    // 11: WordNet.readVerbs()
    READ_VERBS_2(Pattern.compile("^([0-9]{8})([^\\|]+)\\|\\s([\\S\\s]+)$")),

    // 12: WordNet.readVerbs()
    READ_VERBS_3(Pattern.compile("(\\S+)\\s+(\\S+).*")),

    // 13: WordNet.readAdjectives()
    READ_ADJECTIVES_1(Pattern.compile("^([0-9]{8})([\\S\\s]+)\\|\\s([\\S\\s]+?)\\s(\\(?\\&\\%\\S+[\\S\\s]+)$")),

    // 14: WordNet.readAdjectives()
    READ_ADJECTIVES_2(Pattern.compile("^([0-9]{8})([\\S\\s]+)\\|\\s([\\S\\s]+)$")),

    // 15: WordNet.readAdverbs()
    READ_ADJECTIVES_3(Pattern.compile("^([0-9]{8})([\\S\\s]+)\\|\\s([\\S\\s]+)\\s(\\(?\\&\\%\\S+[\\S\\s]+)$")),

    // 16: WordNet.readAdverbs()
    READ_ADVERBS_1(Pattern.compile("^([0-9]{8})([\\S\\s]+)\\|\\s([\\S\\s]+)$")),

    // 17: WordNet.readWordFrequencies()
    READ_WORD_FREQUENCIES(Pattern.compile("^Word: ([^ ]+) Values: (.*)")),

    // 18: WordNet.readSenseIndex()
    READ_SENSE_INDEX(Pattern.compile("([^%]+)%([^:]*):([^:]*):([^:]*)?:([^:]*)?:([^ ]*)? ([^ ]+)? ([^ ]+).*")),

    // 19: WordNet.removePunctuation()
    REMOVE_PUNCTUATION_RE(Pattern.compile("(\\w)\\'re")),

    // 20: WordNet.removePunctuation()
    REMOVE_PUNCTUATION_M(Pattern.compile("(\\w)\\'m")),

    // 21: WordNet.removePunctuation()
    REMOVE_PUNCTUATION_NT(Pattern.compile("(\\w)n\\'t")),

    // 22: WordNet.removePunctuation()
    REMOVE_PUNCTUATION_LL(Pattern.compile("(\\w)\\'ll")),

    // 23: WordNet.removePunctuation()
    REMOVE_PUNCTUATION_S(Pattern.compile("(\\w)\\'s")),

    // 24: WordNet.removePunctuation()
    REMOVE_PUNCTUATION_D(Pattern.compile("(\\w)\\'d")),
    // 25: WordNet.removePunctuation()
    REMOVE_PUNCTUATION_VE(Pattern.compile("(\\w)\\'ve"));

    public final Pattern p;

    WordNetRegexPatterns(Pattern p) {
        this.p = p;
    }

    public Matcher matcher(CharSequence input) {
        return p.matcher(input);
    }
}
