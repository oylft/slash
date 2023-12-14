package com.wuhit.core;

import com.jcraft.jsch.*;
import com.wuhit.Logger;
import com.wuhit.SlashException;
import com.wuhit.StringUtils;
import com.wuhit.configure.LocalFile;
import com.wuhit.configure.Ssh;
import com.wuhit.configure.SshTask;
import com.wuhit.configure.UserInfo;
import com.wuhit.mfa.BaseMFA;
import com.wuhit.mfa.GoogleMFA;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SshClient {

    private Ssh ssh;

    private Logger logger;

    private JSch jSch;

    private Session session;

    private ChannelSftp channelSftp = null;

    private List<String> ignoreFiles = Arrays.asList(".DS_Store", "__MACOSX");

    private BaseMFA mfa;


    private void initMFAAuth() {

        if (ssh.mfa() != null && StringUtils.isNotBlank(ssh.mfa().key())) {
            mfa = new GoogleMFA(ssh.mfa().key());
        }

    }

    public void connect() {

        jSch = new JSch();

        String user = ssh.user();
        String host = ssh.host();

        try {
            session = jSch.getSession(user, host, ssh.port());

            session.setConfig("StrictHostKeyChecking", "no");

            session.setUserInfo(new UserInfo(ssh.password(), mfa));

            session.connect(10 * 1000);

        } catch (JSchException e) {
            throw new SlashException(e.getMessage());
        }

        logger.info(STR."Connection to host \{user}@\{host} successful.");

    }

    private String runCommand(String command) {

        ChannelExec channelExec = null;
        try {

            channelExec = (ChannelExec) session.openChannel("exec");

            channelExec.setCommand(command);

            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channelExec.setOutputStream(responseStream);

            channelExec.connect();

            while (channelExec.isConnected()) {
                Thread.sleep(100);
            }

            return new String(responseStream.toByteArray());


        } catch (JSchException e) {
            throw new SlashException(e.getMessage());
        } catch (InterruptedException e) {
            throw new SlashException(e.getMessage());
        } finally {
            if (channelExec != null) {
                channelExec.disconnect();
            }
        }

    }

    private void openSftp() {

        try {
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect(10 * 1000);

        } catch (JSchException e) {
            throw new SlashException(e.getMessage());
        }

    }

    private List<LocalFile> traverseDirectory(String rootPath, File file) {

        List<LocalFile> localFiles = new ArrayList<>();

        if (file.isDirectory()) {

            File[] files = file.listFiles();

            for (File f : files) {
                localFiles.addAll(traverseDirectory(rootPath, f));
            }

        } else {

            String fileName = file.getName();

            if (ignoreFiles.contains(fileName)) {
                return localFiles;
            }

            String absolutePath = file.getAbsolutePath();

            String parentDir = absolutePath.substring(0, absolutePath.length() - fileName.length() - 1);

            String relativePath = parentDir.substring(rootPath.length()).replaceAll(File.separator, "/");

            localFiles.add(new LocalFile(absolutePath, relativePath, fileName));
        }

        return localFiles;

    }

    private void traverseMkdirDirectory(String remoteDir) {

        try {
            channelSftp.ls(remoteDir);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                String[] dirs = remoteDir.split("/");
                StringBuilder currentDir = new StringBuilder("/");
                for (String dir : dirs) {
                    if (!dir.isEmpty()) {
                        currentDir.append(dir).append("/");
                        try {
                            channelSftp.ls(currentDir.toString());
                        } catch (SftpException ex) {
                            if (ex.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                                try {
                                    channelSftp.mkdir(currentDir.toString());
                                } catch (SftpException exc) {
                                    throw new SlashException(exc.getMessage());
                                }
                            } else {
                                throw new SlashException(ex.getMessage());
                            }
                        }
                    }
                }
            } else {
                throw new SlashException(e.getMessage());
            }
        }
    }

    public void upload(LocalFile file, String remoteDir) throws SftpException {
        channelSftp.put(file.localPath(), STR."\{remoteDir}\{file.relativePath()}/\{file.fileName()}", new SlashProgressMonitor(file.localPath()));
    }


    private String getParentPath(File localFile) {
        if (localFile.isDirectory()) {
            return localFile.getAbsolutePath();
        } else {
            return localFile.getParent();
        }
    }

    private void runTask(SshTask task) {
        String alias = task.alias();
        logger.info(STR."Execute Task [\{alias}]");

        String localPath = task.localPath();

        File localFile = Paths.get(localPath).toFile();

        if (localFile.exists() == false) {
            throw new SlashException(STR."The path '\{localFile.getAbsolutePath()}' does not exist.");
        }

        String remoteDir = task.remoteDir();

        if (StringUtils.isBlank(remoteDir)) {
            throw new SlashException(STR."The remote path is empty.");
        }

        try {
            channelSftp.ls(remoteDir);
        } catch (SftpException e) {
            throw new SlashException(STR."The remote path \{remoteDir} does not exist.\{e.getMessage()}");
        }

        if (StringUtils.isNotBlank(task.beforeCommand())) {
            logger.info(STR."Execute before command [\{task.beforeCommand()}]");
            String responseString = runCommand(task.beforeCommand());
            logger.info(STR."Response [\n\{responseString}\n]");
        }

        try {
            List<LocalFile> localFiles = traverseDirectory(getParentPath(localFile), localFile);
            for (LocalFile file : localFiles) {
                traverseMkdirDirectory(STR."\{remoteDir}\{file.relativePath()}");
                upload(file, remoteDir);
            }
        } catch (SftpException e) {
            throw new SlashException(e.getMessage());
        }

        if (StringUtils.isNotBlank(task.afterCommand())) {
            logger.info(STR."Execute after command [\{task.afterCommand()}]");
            String responseString = runCommand(task.afterCommand());
            logger.info(STR."Response [\n\{responseString}\n]");
        }

    }


    public void run() {
        ssh.tasks().forEach(this::runTask);
    }


    public void disconnect() {

        if (channelSftp != null) {
            channelSftp.exit();
            channelSftp.disconnect();
        }

        if (session != null) {
            session.disconnect();
        }
        jSch = null;
        logger.destroy();
    }

    public SshClient(String slashHome, Ssh ssh) {
        this.ssh = ssh;
        logger = new Logger(slashHome, ssh.host(), File.separator);
        initMFAAuth();
        connect();
        openSftp();
    }

    private SshClient() {
    }
}
