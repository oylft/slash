package com.wuhit.mfa;

public abstract class BaseMFA {

    private String secret;


    public abstract String otpCode();


    public BaseMFA(String secret) {
        this.secret = secret;
    }

    private BaseMFA() {

    }
}
