package com.wuhit.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.wuhit.SlashException;
import com.wuhit.configure.Ssh;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Slash {

  private String configFileName;

  private String slashHome;

  private List<Ssh> sshes;

  private List<SshClient> sshClients = new ArrayList<>();

  public void loadDefinitions() {

    slashHome = System.getProperty("user.dir");

    System.out.println(STR."load the \{configFileName}.");

    File configFile = Paths.get(slashHome + File.separator + configFileName).toFile();

    if (configFile.exists() == false) {
      throw new SlashException(STR."The path '\{configFile.getAbsolutePath()}' does not exist.");
    }

    ObjectMapper omap = new ObjectMapper(new YAMLFactory());

      try {
        sshes = omap.readerForListOf(Ssh.class).readValue(configFile);
      } catch (IOException e) {
          throw new SlashException(e.getMessage());
      }

  }

  public void start() {

    if (sshes == null || sshes.isEmpty()) {
      throw new SlashException(STR."The '\{configFileName}' does not load.");
    }

    for (Ssh ssh : sshes) {
      SshClient sshClient = new SshClient(slashHome, ssh);
      sshClients.add(sshClient);
      sshClient.run();
    }

  }

  public void clear() {
    sshes = null;
    sshClients.forEach(SshClient::disconnect);
  }

  public Slash(String profile) {
    this.configFileName = STR."config-\{profile}.yml";
    this.loadDefinitions();
  }

  private Slash() {
  }
}
