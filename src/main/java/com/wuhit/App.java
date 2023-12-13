package com.wuhit;

import com.wuhit.core.Slash;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/** Hello world! */
public class App {
  public static void main(String[] args) {
    String profile = "default";

    if (args.length > 0) {
      profile = args[0];
    }

    LocalDateTime startTime = LocalDateTime.now();

    Slash slash = new Slash(profile);
    slash.start();

    LocalDateTime endTime = LocalDateTime.now();

    long l = Duration.between(startTime, endTime).get(ChronoUnit.SECONDS);
    Typewriter.print("Slash is done!");
    Typewriter.print(STR."Took: \{l}s");
  }
}
