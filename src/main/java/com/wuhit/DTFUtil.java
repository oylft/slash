package com.wuhit;

import java.time.format.DateTimeFormatter;

public final class DTFUtil {

  public static DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  public static DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SSS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");

  private DTFUtil() {}
}
