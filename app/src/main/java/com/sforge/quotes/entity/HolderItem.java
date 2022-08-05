package com.sforge.quotes.entity;

import com.sforge.quotes.view.QuoteVH;

public class HolderItem {
    QuoteVH vh;
    Quote model;

    public HolderItem(QuoteVH vh, Quote model) {
        this.vh = vh;
        this.model = model;
    }

    public QuoteVH getVh() {
        return vh;
    }

    public Quote getModel() {
        return model;
    }
}
