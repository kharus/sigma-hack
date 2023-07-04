package com.articulate.sigma.wordnet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum WordNetRegexPatterns {

    // 0: WordNet.processPointers()
    regexPatterns0(Pattern.compile("^\\s*\\d\\d\\s\\S\\s\\d\\S\\s")),

    // 1: WordNet.processPointers()
    regexPatterns1(Pattern.compile("^([a-zA-Z0-9'._\\-]\\S*)\\s([0-9a-f])\\s")),

    // 2: WordNet.processPointers()
    regexPatterns2(Pattern.compile("^...\\s")),

    // 3: WordNet.processPointers()
    regexPatterns3(Pattern.compile("^(\\S\\S?)\\s([0-9]{8})\\s(.)\\s([0-9a-f]{4})\\s?")),

    // 4: WordNet.processPointers()
    regexPatterns4(Pattern.compile("^..\\s")),

    // 5: WordNet.processPointers()
    regexPatterns5(Pattern.compile("^\\+\\s(\\d\\d)\\s(\\d\\d)\\s?")),

    // 6: WordNet.readNouns()
    regexPatterns6(Pattern.compile("^([0-9]{8})([\\S\\s]+)\\|\\s([\\S\\s]+?)\\s(\\(?\\&\\%\\S+[\\S\\s]+)$")),

    // 7: WordNet.readNouns()
    regexPatterns7(Pattern.compile("^([0-9]{8})([\\S\\s]+)\\|\\s([\\S\\s]+)$")),

    // 8: WordNet.readNouns()
    regexPatterns8(Pattern.compile("(\\S+)\\s+(\\S+)")),

    // 9: WordNet.readNouns()
    regexPatterns9(Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)")),

    // 10: WordNet.readVerbs()
    regexPatterns10(Pattern.compile("^([0-9]{8})([^\\|]+)\\|\\s([\\S\\s]+?)\\s(\\(?\\&\\%\\S+[\\S\\s]+)$")),

    // 11: WordNet.readVerbs()
    regexPatterns11(Pattern.compile("^([0-9]{8})([^\\|]+)\\|\\s([\\S\\s]+)$")),

    // 12: WordNet.readVerbs()
    regexPatterns12(Pattern.compile("(\\S+)\\s+(\\S+).*")),

    // 13: WordNet.readAdjectives()
    regexPatterns13(Pattern.compile("^([0-9]{8})([\\S\\s]+)\\|\\s([\\S\\s]+?)\\s(\\(?\\&\\%\\S+[\\S\\s]+)$")),

    // 14: WordNet.readAdjectives()
    regexPatterns14(Pattern.compile("^([0-9]{8})([\\S\\s]+)\\|\\s([\\S\\s]+)$")),

    // 15: WordNet.readAdverbs()
    regexPatterns15(Pattern.compile("^([0-9]{8})([\\S\\s]+)\\|\\s([\\S\\s]+)\\s(\\(?\\&\\%\\S+[\\S\\s]+)$")),

    // 16: WordNet.readAdverbs()
    regexPatterns16(Pattern.compile("^([0-9]{8})([\\S\\s]+)\\|\\s([\\S\\s]+)$")),

    // 17: WordNet.readWordFrequencies()
    regexPatterns17(Pattern.compile("^Word: ([^ ]+) Values: (.*)")),

    // 18: WordNet.readSenseIndex()
    regexPatterns18(Pattern.compile("([^%]+)%([^:]*):([^:]*):([^:]*)?:([^:]*)?:([^ ]*)? ([^ ]+)? ([^ ]+).*")),

    // 19: WordNet.removePunctuation()
    regexPatterns19(Pattern.compile("(\\w)\\'re")),

    // 20: WordNet.removePunctuation()
    regexPatterns20(Pattern.compile("(\\w)\\'m")),

    // 21: WordNet.removePunctuation()
    regexPatterns21(Pattern.compile("(\\w)n\\'t")),

    // 22: WordNet.removePunctuation()
    regexPatterns22(Pattern.compile("(\\w)\\'ll")),

    // 23: WordNet.removePunctuation()
    regexPatterns23(Pattern.compile("(\\w)\\'s")),

    // 24: WordNet.removePunctuation()
    regexPatterns24(Pattern.compile("(\\w)\\'d")),
// 25: WordNet.removePunctuation()
    regexPatterns25(Pattern.compile("(\\w)\\'ve"));

    public final Pattern p;
    WordNetRegexPatterns(Pattern p) {
        this.p = p;
    }

    public Matcher matcher(CharSequence input) {
        return p.matcher(input);
    }
}
