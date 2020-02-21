package com.kennycason.kumo.nlp.tokenizer_s;

import java.util.List;

public interface WordTokenizer {
    List<String> tokenize(String sentence);
}