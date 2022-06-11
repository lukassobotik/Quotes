package com.sforge.quotes.entity;

/**
 * Basic immutable entity holding data for Quote.
 */
public class Quote {

    /**
     * Quote string to be displayed.
     */
    private final String quote;

    /**
     * Author of the quote.
     */
    private final String author;

    /**
     * User who added the quote.
     */
    private final String user;

    /**
     * Public constructor for creating of immutable instance of this entity.
     * @param quote Quote string to be displayed.
     * @param author Author of the quote.
     * @param user User who added the quote.
     */
    public Quote(final String quote, final String author, final String user) {
        this.quote = quote;
        this.author = author;
        this.user = user;
    }

    public String getQuote() {
        return quote;
    }

    public String getAuthor() {
        return author;
    }

    public String getUser() {
        return user;
    }
}
