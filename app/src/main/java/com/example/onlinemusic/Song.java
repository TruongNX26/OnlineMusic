package com.example.onlinemusic;

import java.io.Serializable;

public class Song implements Serializable {
    private Long id;
    private String name;
    private String singer;
    private String dataLink;

    public Song() {
    }

    public Song(Long id, String name, String singer, String dataLink) {
        this.id = id;
        this.name = name;
        this.singer = singer;
        this.dataLink = dataLink;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getDataLink() {
        return dataLink;
    }

    public void setDataLink(String dataLink) {
        this.dataLink = dataLink;
    }
}
