# Vanilla jsConnect Client Library for Java

[![Travis (.com)](https://img.shields.io/travis/com/vanilla/jsConnectJava)](https://travis-ci.com/vanilla/jsConnectJava)

This repository contains the files you need to use Vanilla's jsConnect with a java project.

The following files are included in this repo.

* `src/main/java/com/vanillaforums/vanilla/jsConnect.java`
  This is the main file you need. You don't need any other file in your project. You can just drop this file anywhere that you can access it on your site.
* `web/index.jsp`
  This file offers an example usage. You can customize this page or start from scratch.
  
## Change Log

### Version 2.1

- Added support for SHA1 and SHA256 hashing. We strongly recommend you use one of these hash methods.
- Removed dependencies on some external libraries.
- Added unit tests for most functionality.
- Moved test SSO string to `jsConnect.GetTestJsConnectString()`.
- Deprecated some of the methods from previous versions that use MD5. 

### Version 2

- Added more security information for the version 2 protocol of jsConnect.
- Fixed some issues with malformed callbacks.
- Added support for the jsConnect SSO string for embedded SSO.
  
## Requirements

This project requires Java 8 at a minimum. You can look at the [build](https://travis-ci.com/vanilla/jsConnectJava) to see what other versions are being built.
