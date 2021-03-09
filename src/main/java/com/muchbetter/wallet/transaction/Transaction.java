package com.muchbetter.wallet.transaction;


import com.muchbetter.wallet.balance.Currency;

public class Transaction {
    //todo parse date in to Date object - will require DateFormat and a json handler
    public String date;
    public String description;
    public float amount;
    public Currency currency;

    public Transaction() {
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public float getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }
}
