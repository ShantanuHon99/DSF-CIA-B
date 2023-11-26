package com.example.englishdictnew1.controller;


import com.example.englishdictnew1.model.Dictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DictionaryController {

    private final Dictionary dictionary;

    @Autowired
    public DictionaryController(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    @GetMapping("/dictionary/meaning/{word}")
    public String getMeaning(@PathVariable String word) {
        return dictionary.getMeaning(word);
    }
}
