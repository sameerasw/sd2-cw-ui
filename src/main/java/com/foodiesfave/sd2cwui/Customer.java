package com.foodiesfave.sd2cwui;

public class Customer {
    private final String fName;
    private final String sName;
    private final int burgers;

    public Customer(String inputFName, String inputSName, int inputBurgers) {
        this.fName = inputFName;
        this.sName = inputSName;
        this.burgers = inputBurgers;
    }

    @Override
    public String toString() {
        return this.fName + "\n" + this.sName + "\n" + this.burgers;
    }

    public int getBurgers() {
        return this.burgers;
    }

    public String getFName() {
        return this.fName;
    }

    public String getSName() {
        return this.sName;
    }
}