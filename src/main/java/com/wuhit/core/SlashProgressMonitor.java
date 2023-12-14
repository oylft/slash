package com.wuhit.core;

import com.jcraft.jsch.SftpProgressMonitor;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SlashProgressMonitor implements SftpProgressMonitor {

  private String localPath;

  public SlashProgressMonitor(String localPath) {
    this.localPath = localPath;
  }

  private Long totalBytesTransferred;

  private Long totalBytes;

  @Override
  public void init(int i, String s, String s1, long l) {
    totalBytesTransferred = Long.valueOf(i);
    totalBytes = Long.valueOf(l);
  }

  @Override
  public boolean count(long l) {
    totalBytesTransferred += l;
    BigDecimal progress =
        new BigDecimal(totalBytesTransferred)
            .divide(new BigDecimal(totalBytes), 4, BigDecimal.ROUND_HALF_UP);

    int uploadedSize =
        progress.multiply(new BigDecimal(10)).setScale(0, RoundingMode.DOWN).intValue();

    StringBuilder progressBar = new StringBuilder();
    progressBar.append("[ ");
    for (int i = 0; i < uploadedSize; i++) {
      progressBar.append("#");
    }

    int rest = 10 - uploadedSize;
    for (int i = 0; i < rest; i++) {
        progressBar.append("=");
    }

    progressBar.append(" ]");

    String percent =
            STR."\{progress.multiply(new BigDecimal(100)).setScale(2, RoundingMode.DOWN)}%";

    progressBar.append(STR." \{percent}");

    System.out.print(STR."\r\{progressBar} \{localPath}");
    return true;
  }

  @Override
  public void end() {
    System.out.print("\n");
    totalBytesTransferred = null;
    totalBytes = null;
  }
}
