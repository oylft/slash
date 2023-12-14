package com.wuhit.mfa;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

public class GoogleMFA extends BaseMFA {

    private GoogleAuthenticatorKey key;
    private GoogleAuthenticator auth = new GoogleAuthenticator();

    public GoogleMFA(String secretKey) {
        super(secretKey);
        key = new GoogleAuthenticatorKey.Builder(secretKey).build();
    }

    public String otpCode() {
        return String.valueOf(auth.getTotpPassword(key.getKey()));
    }
}
