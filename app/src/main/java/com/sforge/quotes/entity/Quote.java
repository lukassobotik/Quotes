package com.sforge.quotes.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Quote implements Parcelable {
    private String quote;
    private String author;
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


    protected Quote(Parcel in) {
        quote = in.readString();
        author = in.readString();
        user = in.readString();
        key = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(quote);
        dest.writeString(author);
        dest.writeString(user);
        dest.writeString(key);
    }

    public static final Creator<Quote> CREATOR = new Creator<Quote>() {
        @Override
        public Quote createFromParcel(Parcel in) {
            return new Quote(in);
        }

        @Override
        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };
}
