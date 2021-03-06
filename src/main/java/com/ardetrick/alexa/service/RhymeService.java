package com.ardetrick.alexa.service;

import com.ardetrick.alexa.model.RhymeWord;
import com.ardetrick.alexa.model.RhymeWordLite;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Drew Tenenbaum on 11/21/2016.
 */
public class RhymeService {

    public String RHYME_URL_BASE = "http://rhymebrain.com/talk";

    public List<RhymeWord> getWordsThatRhymeWith(String baseWord){
        URI targetUrl = getWebServiceURI(baseWord);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<RhymeWord[]> entity = restTemplate.getForEntity(targetUrl, RhymeWord[].class);
            HttpStatus serviceStatus = entity.getStatusCode();
            if (serviceStatus == HttpStatus.OK){
                RhymeWord[] snackArray = entity.getBody();
                return Arrays.asList(snackArray);
            } else {
                RhymeWord dummyWord = new RhymeWord();
                dummyWord.setDummy(true);
                List<RhymeWord> dummyList = new ArrayList<>();
                return dummyList;
            }
        } catch (final HttpClientErrorException e){
            throw e;
        }
    }

    public List<RhymeWord> getWordsThatPerfectRhymeWith(String baseWord){
        return getWordsThatRhymeWith(baseWord).stream().sorted(new Comparator<RhymeWord>() {
            @Override
            public int compare(RhymeWord o1, RhymeWord o2) {
                return o2.getScore() - o1.getScore();
            }
        }).collect(Collectors.toList());
    }

    private URI getWebServiceURI(String word) {
        return UriComponentsBuilder.fromUriString(RHYME_URL_BASE).queryParam("function", "getRhymes").queryParam("word", word).build().toUri();
    }

    public static List<RhymeWordLite> filterByNotYetGuessed(List<RhymeWordLite> rhymesArray) {
        return rhymesArray.stream().
                filter(rhymeWord -> !rhymeWord.isHasBeenGuessed()).
                collect(Collectors.toList());
    }
}
