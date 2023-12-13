package com.wuhit.configure;

public record SshTask(
    String alias, String remoteDir, String localPath, String beforeCommand, String afterCommand) {}
