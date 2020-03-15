package com.kennycason.kumo.nlp.tokenizer;

import java.util.Collections;
import java.util.List;

import com.kennycason.kumo.nlp.tokenizer_s.WordTokenizer;


public class NoTokenizer implements WordTokenizer {

    public List<String> tokenize(final String sentence) {
        return Collections.singletonList(sentence);
    }

}
