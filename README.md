# Socket.IO coding assignment

This is a client interacting with a Socket.IO service providing search capability for Star Wars characters.

## Requirements
Java 18 and Maven are installed
JAVA_HOME environment variable is set (for Maven)

## To run
Run 

`mvn exec:java`

and this will start the application which will try to connect to the server on localhost:3000

## Libraries
Only used the Java Socket.IO client library

## Assumption
- Could convert all System.out/err.println to Log4J or similar for centralised logging/monitoring
- Didn't need to be able to configure the ability to choose the server URL 
