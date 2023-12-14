package com.wuhit.mfa;

public abstract class BaseMFA {

    private String secretKey;


    public abstract String otpCode();


    public BaseMFA(String secretKey) {
        this.secretKey = secretKey;
    }

    private BaseMFA() {

    }
}
