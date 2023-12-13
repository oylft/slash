package com.wuhit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class Logger {

  private String ruciaHome;

  private String fileName;

  private String separator;

  private FileWriter fileWriter;

  private void initFileWriter() {
    String parentDir =
        ruciaHome + separator + "logs" + separator + LocalDate.now().format(DTFUtil.YYYY_MM_DD);
    File parentDirFile = Paths.get(parentDir).toFile();
    if (parentDirFile.exists() == false) {
      parentDirFile.mkdirs();
    }

    String logPath = parentDir + separator + fileName + ".log";

    File logFile = Paths.get(logPath).toFile();

    if (logFile.exists() == false) {
      try {
        logFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    try {
      fileWriter = new FileWriter(logFile, true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void write(String log) {
    try {
      fileWriter.write(log + "\n");
      fileWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void info(String log) {
    log = LocalDateTime.now().format(DTFUtil.YYYY_MM_DD_HH_MM_SS_SSS) + ": " + log;
    System.out.println(log);
    this.write(log);
  }

  public void error(String log) {
    log = LocalDateTime.now().format(DTFUtil.YYYY_MM_DD_HH_MM_SS_SSS) + ": " + log;
    System.err.println(log);
    this.write(log);
  }

  public void destroy() {
    try {
      fileWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Logger(String ruciaHome, String fileName, String separator) {
    this.ruciaHome = ruciaHome;
    this.fileName = fileName;
    this.separator = separator;
    this.initFileWriter();
  }
}
