package com;

import lombok.Data;

@Data
public class EmotionalWords {
    private String sentimentClassification;
    private int emotionalIntensity;
    private int polarity;
    private String PartSpeech;

    public EmotionalWords(String sentimentClassification, int emotionalIntensity, int polarity, String PartSpeech){
        this.sentimentClassification=sentimentClassification;
        this.emotionalIntensity=emotionalIntensity;
        this.polarity=polarity;
        this.PartSpeech=PartSpeech;
    }
}
