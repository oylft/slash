package com.wuhit.configure;

import java.util.List;

public record Ssh(String host, Integer port, String user, String password, MFA mfa, List<SshTask> tasks) {
}
