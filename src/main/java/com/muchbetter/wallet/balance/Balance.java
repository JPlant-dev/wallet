package com.muchbetter.wallet.balance;


public class Balance {
    // using float to reduce memory consumption
    private float balance;
    private Currency currency;

    public Balance() {
    }

    public Balance(float balance, Currency currency) {
        this.balance = balance;
        this.currency = currency;
    }

    public float getBalance() {
        return balance;
    }

    public Currency getCurrency() {
        return currency;
    }
}
