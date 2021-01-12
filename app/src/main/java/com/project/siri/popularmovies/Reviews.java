package com.project.siri.popularmovies;

public class Reviews {

    String author;
    String content;
    String url;

    public Reviews(String author, String content, String url)
    {
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
