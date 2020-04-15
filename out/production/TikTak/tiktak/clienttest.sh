#!/usr/bin/bash
# David Beggs
# Program 2
# Test script for tiktak Client

javac tiktak/*/*.java tiktak/app/*/*.java

jar cfe Client.jar tiktak.app.client.Client tiktak/*/*.class tiktak/app/*/*.class

java -cp Client.jar tiktak.app.client.Client wind.ecs.baylor.edu 12345 beggs TOST

java -cp Client.jar tiktak.app.client.Client wind 12345 beggs xvkby LTSRL coolpics imagedata otherdata

java -cp Client.jar tiktak.app.client.Client wind 12345 beggs TOS

java -cp Client.jar tiktak.app.client.Client wind.ecs.baylor.edu 12345 beggs xvkby LTSR coolpics imagedata

#Test IO/Exception
( (echo 'TIKTAK 1.0'; echo 'CLNG 1234')|nc -l -C 55555) &
java -cp Client.jar tiktak.app.client.Client wind.ecs.baylor.edu 55555 beggs xvkby TOST

#Test invalidFields
( (echo 'TIKTAK 1.0'; echo 'CLNG 12!!34'; echo 'ACK'; echo 'ACK')|nc -l -C 55556) &
java -cp Client.jar tiktak.app.client.Client wind.ecs.baylor.edu 55556 beggs xvkby TOST

#Test Errors
( (echo 'TIKTAK 1.0'; echo 'ERROR 123 BADLITTLEMESSAGE')|nc -l -C 55557) &
java -cp Client.jar tiktak.app.client.Client wind.ecs.baylor.edu 55557 beggs xvkby TOST

#Test Unexpected 
( (echo 'TIKTAK 1.0'; echo 'TIKTAK 1.0')|nc -l -C 55557) &
java -cp Client.jar tiktak.app.client.Client wind.ecs.baylor.edu 55557 beggs xvkby TOST


#Test Valid
( (echo 'TIKTAK 1.0'; echo 'TIKTAK 1.0')|nc -l -C 55557) &
java -cp Client.jar tiktak.app.client.Client wind.ecs.baylor.edu 12345 beggs xvkby TOST

