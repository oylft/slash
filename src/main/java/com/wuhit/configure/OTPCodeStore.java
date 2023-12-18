package com.wuhit.configure;

public final class OTPCodeStore {

    private static final ThreadLocal<String> OTP_CODE_THREAD_LOCAL = new ThreadLocal<>();

    private OTPCodeStore() {
    }

    public static void set(String otpCode) {
        OTP_CODE_THREAD_LOCAL.set(otpCode);
    }

    public static String get() {
        return OTP_CODE_THREAD_LOCAL.get();
    }

    public static void remove() {
        OTP_CODE_THREAD_LOCAL.remove();
    }
}
