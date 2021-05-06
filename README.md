# Proxy Server

The goal of this project was to make a simple HTTPS proxy server to proxy giphy.com traffic through.
See https://signal.org/blog/giphy-experiment/

## Description

This Project sets up an HTTP proxy listenning on a configured port number limited to specific remote servers. The proxy will accept requests as they come in, parse the HTTP request, and if valid will open up a proxy between the connecting client and the remote server specificed in the request. The proxy has a list of predefiend hostnames that it will allow clients to connect to. The proxy will stay open until either the client or the sever terminates the connection. The proxy uses multi-threading to allow it to handle many concurrent proxy requests and concurrent traffic going between multiple servers and clients. 

If given more time I would like to handle the following
* Add plaintext proxying by handling the GET verb to the ProxyProtocol class
* Add timeouts to the sockets so that they would time out after a specified window.
* Add wildcard handling to the urls list or a blacklist of urls to add some more customization to the proxy server. 
* Look into things mentioned in the Signal blog such as disabling TLS session resume. 
* Perform more robust testing but given the quick turnaround I performed simple positive end to end testing. 

The testing consisted of starting up the server then running curl giving the proxy address and some valid and invalid urls.
```
curl -x "http://localhost:8080" "http://giphy.com" --verbose
```

## Getting Started

### Dependencies

The following dependencies need to be installed using the following links.

* java jdk - https://java.com/en/download/help/download_options.html
* sdk - https://sdkman.io (to install gradle)
* gradle -  https://docs.gradle.org/current/userguide/installation.html


Known Supported Versions
* Gradle: 		7.0
* Kotlin:       1.4.31
* Groovy:       3.0.7
* Ant:          Apache Ant(TM) version 1.10.9 compiled on September 27 2020
* JVM:          11.0.11 (AdoptOpenJDK 11.0.11+9)
* OS:           Mac OS X 11.2 x86_64


### Installing

* Run the following to build and bring up the server from the root of the project.
```
./gradlew run
```
* Modify app/src/main/resources/config.json to change the port the proxy listens on or the urls to filter on.


## Authors

Christopher Graves

## Acknowledgments

Inspiration, code snippets, etc.
* [Multithreaded Java Server]https://docs.oracle.com/javase/tutorial/networking/sockets/examples/KKMultiServerThread.java