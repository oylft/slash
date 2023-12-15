# Slash
<a href="https://github.com/Rich-Harris/magic-string/blob/main/LICENSE.md">
  <img src="https://github.com/wuhit/slash/blob/main/license.svg" alt="license">
</a>

It's a small, fast utility for SSH and Sftp.

Deploying your project to the server uses `Slash`.

## Usage

### Command

Slash uses a `config-[alias].yml` file in the current directory.

```bash
  slash [alias]
```
#### eg:
```bash
  slash dev
```
Uses the `config-dev.yml` file in the current directory.


### For SSH configuration

```yaml
- host: wuhit.com
  port: 22
  user: dev
  password: slash
  tasks:
    - alias: web
      remoteDir: /root
      localPath: /Users/zhaoxin/Pictures/wuhit.jar
      beforeCommand: pwd
      afterCommand: ls
```

#### Width Google Authenticator

```yaml
- host: wuhit.com
  port: 22
  user: dev
  password: slash
  mfa:
    secret: D4DDFZ2TCUO26Ri2
  tasks:
    - alias: web
      remoteDir: /root
      localPath: /Users/zhaoxin/Pictures/wuhit.jar
      beforeCommand: pwd
      afterCommand: ls
```


### For Jump Server configuration

```yaml
- host: js.wuhit.com
  port: 2222
  user: jumpServerUser@root@192.169.0.1
  password: jumpServerPassword
  tasks:
    - alias: web
      remoteDir: test-192.169.0.1
      localPath: /Users/zhaoxin/Pictures/F6zHvUFWgAA-dj-.jpeg
      beforeCommand: pwd
      afterCommand: ls
```

#### Width Google Authenticator

```yaml
- host: js.wuhit.com
  port: 2222
  user: jumpServerUser@root@192.169.0.1
  password: jumpServerPassword
  mfa:
    secret: D4DDFZ2TCUO26Ri2
  tasks:
    - alias: web
      remoteDir: test-192.169.0.1
      localPath: /Users/zhaoxin/Pictures/F6zHvUFWgAA-dj-.jpeg
      beforeCommand: pwd
      afterCommand: ls
```