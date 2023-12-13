package com.wuhit.configure;

import com.jcraft.jsch.UIKeyboardInteractive;

import java.util.Scanner;

public class UserInfo implements com.jcraft.jsch.UserInfo, UIKeyboardInteractive {

  private String password;

  @Override
  public String[] promptKeyboardInteractive(
      String destination, String name, String instruction, String[] prompt, boolean[] echo) {

    Scanner scanner = new Scanner(System.in);
    System.out.println(STR."\u001B[32m\{destination} \{prompt[0]}\u001B[0m");
    String input = scanner.nextLine().trim();

    String[] strings = {input};

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

  public UserInfo(String password) {
    this.password = password;
  }

  private UserInfo() {
  }
}
