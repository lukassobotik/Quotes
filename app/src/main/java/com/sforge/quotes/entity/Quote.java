package com.sforge.quotes.entity;

public class Quote {

    public Quote(){}
    public Quote(String quote, String author, String user) {
        Quote = quote;
        Author = author;
        this.user = user;
    }

    public String getQuote() {
        return Quote;
    }

    public void setQuote(String quote) {
        Quote = quote;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    private String Quote;
    private String Author;
    public String user;
}
