# How to setup release keys
Full guide here: [sbt-ci-release](https://github.com/olafurpg/sbt-ci-release)

### 1. Gen Key Pair
```
gpg --gen-key
```
**data**
- name: `$PRJ_NAME-release-bot`
- email: use your own email address
- For passphrase, generate a random password with a password manager

**Result**
```
pub   rsa2048 2018-06-10 [SC] [expires: 2020-06-09]
      $LONG_ID
uid                      $PRJ_NAME-release-bot bot <$EMAIL>
```

### 2. Set PRJ_NAME and LONG_ID

**example**
```
//On UNIX
PRJ_NAME=example-release-bot
LONG_ID=6E8ED79B03AD527F1B281169D28FC818985732D9

//On Windows
set PRJ_NAME=example-release-bot
set LONG_ID=6E8ED79B03AD527F1B281169D28FC818985732D9
```

### 3. Export public key
**clipboard**
```
# macOS
gpg --armor --export $LONG_ID | pbcopy
# linux
gpg --armor --export $LONG_ID | xclip
# Windows
gpg --armor --export %LONG_ID%
```

**file**
```
# macOS
gpg --armor --export $LONG_ID > $PRJ_NAME-release-bot-public.gpg
# linux
gpg --armor --export $LONG_ID > $PRJ_NAME-release-bot-public.gpg
# Windows
gpg --armor --export %LONG_ID% > %PRJ_NAME%-release-bot-public.gpg
```

### 4. Export private key in base64
**clipboard**
```
# macOS
gpg --armor --export-secret-keys $LONG_ID | base64 | pbcopy
# Ubuntu (assuming GNU base64)
gpg --armor --export-secret-keys $LONG_ID | base64 -w0 | xclip
# Windows
gpg --armor --export-secret-keys %LONG_ID% | openssl base64
```

**file**
```
# macOS
gpg --armor --export-secret-keys $LONG_ID | base64 > $PRJ_NAME-release-bot-private.gpg
# Ubuntu (assuming GNU base64)
gpg --armor --export-secret-keys $LONG_ID | base64 -w0 > $PRJ_NAME-release-bot-private.gpg
# Windows
gpg --armor --export-secret-keys %LONG_ID% | openssl base64 > %PRJ_NAME%-release-bot-private.gpg
```  

### 5. Public the public key to keyserver
Copy the **PUBLIC KEY** and publish it in a public keyserver

Like this one:
[https://keyserver.ubuntu.com/](https://keyserver.ubuntu.com/)

### 6. Put the private key in Github secrets
- Copy the **PRIVATE KEY** in _Base64_
- Create a secret in github named `PGP_SECRET` and store the base64 *PRIVATE KEY*
- Create a secret in github named `PGP_PASSPHRASE` and store the base64 *PRIVATE KEY* passphrase