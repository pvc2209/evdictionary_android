package com.example.evdictionary;

import java.io.Serializable;

public class History {
    private int position;
    private int id;
    private String word;
    private String table;

    public History(int position, int id, String word, String table) {
        this.position = position;
        this.id = id;
        this.word = word;
        this.table = table;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
