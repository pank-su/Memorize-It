package com.example.memorize_it;

public class Note {
    String name;
    String annotation;
    String date;
    byte runned;

    Note(String name, String annotation, String date, int runned){
        this.name = name;
        this.annotation = annotation;
        this.date = date;
        this.runned = (byte) runned;
    }
}
