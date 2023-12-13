package com.wuhit.core;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.wuhit.Logger;
import com.wuhit.SlashException;
import com.wuhit.configure.Ssh;
import com.wuhit.configure.UserInfo;

import java.io.File;

public class SshClient {

  private String slashHome;

  private Ssh ssh;

  private Logger logger;

  private JSch jSch;

  private Session session;

  public void connect() {

    jSch = new JSch();

    String user = ssh.user();
    String host = ssh.host();

    try {
      session = jSch.getSession(user, host, ssh.port());

      session.setConfig("StrictHostKeyChecking", "no");

      session.setUserInfo(new UserInfo(ssh.password()));

      session.connect(10 * 1000);

    } catch (JSchException e) {
      throw new SlashException(e.getMessage());
    }

    logger.info(STR."Connection to host \{user}@\{host} successful.");

  }


  public void disconnect() {
    if (session != null) {
      session.disconnect();
    }
    jSch = null;
    logger.destroy();
  }

  public SshClient(String slashHome, Ssh ssh) {
    this.slashHome = slashHome;
    this.ssh = ssh;
    this.logger = new Logger(slashHome, ssh.host(), File.separator);
    this.connect();
  }

  private SshClient() {}
}
