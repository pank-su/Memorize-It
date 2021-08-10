package com.example.memorize_it;

public class Note {
    String name;
    String annotation;
    String date;
    byte runned;
    int id;

    public Note(String name, String annotation, String date, int runned, int id){
        this.name = name;
        this.annotation = annotation;
        this.date = date;
        this.runned = (byte) runned;
        this.id = id;
    }
}
