package com.wuhit.configure;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.wuhit.mfa.BaseMFA;

import java.util.Scanner;

public class UserInfo implements com.jcraft.jsch.UserInfo, UIKeyboardInteractive {

    private String password;
    private BaseMFA mfa;

    @Override
    public String[] promptKeyboardInteractive(
            String destination, String name, String instruction, String[] prompt, boolean[] echo) {

        String[] strings;

        if (prompt[0].contains("OTP Code") && mfa != null) {
            strings = new String[]{mfa.otpCode()};
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println(STR."\u001B[32m\{destination} \{prompt[0]}\u001B[0m");
            String input = scanner.nextLine().trim();

            strings = new String[]{input};
        }


        return strings;
    }

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean promptPassword(String message) {
        return true;
    }

    @Override
    public boolean promptPassphrase(String message) {
        return true;
    }

    @Override
    public boolean promptYesNo(String message) {
        return true;
    }

    @Override
    public void showMessage(String message) {
        System.err.println(message);
    }

    public UserInfo(String password, BaseMFA mfa) {
        this.password = password;
        this.mfa = mfa;
    }

    private UserInfo() {
    }
}
