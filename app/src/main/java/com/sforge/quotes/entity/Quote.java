package com.sforge.quotes.entity;

/**
 * Basic entity holding data for Quote.
 */
public class Quote {

    /**
     * Quote string to be displayed.
     */
    private String quote;

    /**
     * Author of the quote.
     */
    private String author;

    /**
     * User who added the quote.
     */
    private String user;

    /**
     * Database key that is used to store the quote.
     */
    private String key;

    public Quote() {
    }

    /**
     * Public constructor for creating of immutable instance of this entity with the additional info of the key that it is stored in the database with
     * @param quote Quote string to be displayed.
     * @param author Author of the quote.
     * @param user User who added the quote.
     * @param key Key that the quote is stored with.
     */
    public Quote(final String quote, final String author, final String user, final String key) {
        this.quote = quote;
        this.author = author;
        this.user = user;
        this.key = key;
    }

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

    public String getKey() {
        return key;
    }
}
