package com.articulate.sigma.wordnet;

public enum FileMaps {
    noun_mappings("WordNetMappings30-noun.txt"),
    verb_mappings("WordNetMappings30-verb.txt"),
    adj_mappings("WordNetMappings30-adj.txt"),
    adv_mappings("WordNetMappings30-adv.txt"),
    noun_exceptions("noun.exc"),
    verb_exceptions("verb.exc"),
    adj_exceptions("adj.exc"),
    adv_exceptions("adv.exc"),
    sense_indexes("index.sense"),
    word_frequencies("wordFrequencies.txt"),
    cntlist("cntlist"),
    stopwords("stopwords.txt"),

    messages("messages.txt");

    public final String fileName;

    FileMaps(String fileName) {
        this.fileName = fileName;
    }
}
