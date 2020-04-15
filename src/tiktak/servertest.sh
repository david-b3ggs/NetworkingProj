#!/usr/bin/bash
# David Beggs
# Program 2
# Test script for tiktak Client

javac tiktak/*/*.java tiktak/app/*/*.java

jar cfe Client.jar tiktak.app.client.Client tiktak/*/*.class tiktak/app/*/*.class

jar cfe Server.jar tiktak.app.server.Server tiktak/*/*.class tiktak/app/*/*.class

echo testing server startup problems
echo testing incorrect portNumber
java -cp Server.jar tiktak.app.server.Server 1 5 passfile.txt

echo testing invalid thread number
java -cp Server.jar tiktak.app.server.Server 12389 0 passfile.txt

echo testing invalid password fileName
java -cp Server.jar tiktak.app.server.Server 12389 5 wrongFile

echo testing invalid argument order
java -cp Server.jar tiktak.app.server.Server passfile.txt 2 12389

echo testing invalid argument count
java -cp Server.jar tiktak.app.server.Server 12389 2 passfile.txt anotherfile.txt

echo testing password file format
java -cp Server.jar tiktak.app.server.Server 12389 2 badfile.txt

echo running server
(java -cp Server.jar tiktak.app.server.Server 12389 5 passfile.txt) &

sleep 1

echo running client tost
java -cp Client.jar tiktak.app.client.Client wind.ecs.baylor.edu 12389 beggs xvkby TOST

java -cp Client.jar tiktak.app.client.Client wind.ecs.baylor.edu 12389 beggs xvkby TOST

echo running client ltsrl
java -cp Client.jar tiktak.app.client.Client wind.ecs.baylor.edu 12389 beggs xvkby LTSRL coolpics windows.jpg

echo Testing invalid fields
( (echo 'ID begg^s') | nc -l -c 12389)

echo Testing unexpected type
( (echo 'ACK') | nc -l -c 12389)

echo Testing ERROR Message
( (echo 'ERROR 123 BAD') | nc -l -c 12389)

echo Testing unkownID
( (echo 'ID blahblah') | nc -l -c 12389)

echo Testing bad credentials
( (echo 'ID beggs'; echo 'baddPass') |nc -l -c 12389)

echo Killing Server
fuser -k 12389/tcp 

echo Cleaning up
rm Server.jar
rm Client.jar
