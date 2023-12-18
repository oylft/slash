package com.wuhit.configure;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.wuhit.StringUtils;
import com.wuhit.mfa.BaseMFA;

import java.util.Scanner;

public class UserInfo implements com.jcraft.jsch.UserInfo, UIKeyboardInteractive {

    private BaseMFA mfa;

    @Override
    public String[] promptKeyboardInteractive(
            String destination, String name, String instruction, String[] prompt, boolean[] echo) {


        if ((prompt[0].contains("OTP Code") || prompt[0].contains("Verification code")) && mfa != null) {
            System.out.println(STR."\u001B[32m\{destination} \{prompt[0]}\u001B[0m");
            String otpCode = mfa.otpCode();

            String prevOtpCode = OTPCodeStore.get();

            if (StringUtils.isBlank(prevOtpCode) || prevOtpCode.equals(otpCode) == false) {
                System.out.println(STR."\u001B[32m\{otpCode}\u001B[0m");
                OTPCodeStore.set(otpCode);
                return new String[]{otpCode};
            }
        }


        Scanner scanner = new Scanner(System.in);
        System.out.println(STR."\u001B[32m\{destination} \{prompt[0]}\u001B[0m");
        String input = scanner.nextLine().trim();

        return new String[]{input};

    }

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
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

    public UserInfo(BaseMFA mfa) {
        this.mfa = mfa;
    }

    private UserInfo() {
    }
}
