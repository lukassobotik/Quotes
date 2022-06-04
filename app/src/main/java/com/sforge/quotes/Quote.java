package com.sforge.quotes;

public class Quote {

    public Quote(){}
    public Quote(String quote, String author) {
        Quote = quote;
        Author = author;
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

    private String Quote;
    private String Author;
}
