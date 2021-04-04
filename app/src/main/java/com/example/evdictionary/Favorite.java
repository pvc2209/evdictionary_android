package com.example.evdictionary;

public class Favorite {
    private int id;
    private String word;
    private String table;

    public Favorite(int id, String word, String table) {
        this.id = id;
        this.word = word;
        this.table = table;
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
