#!/bin/sh

#/usr/bin/screen -t 'mqtt-java' -dm bash -c  'cd /home/denys/Projects/; java  -XX:+UseG1GC -Xmx2g -Xms32m -jar mqtt-message-store-1.0-SNAPSHOT-jar-with-dependencies.jar 10'
/usr/bin/screen -t 'mqtt-java' -dm bash -c  'cd /home/denys/Projects/; java  -XX:+UseG1GC -Xmx64m -Xms4m -jar mqtt-message-store-1.0-SNAPSHOT-jar-with-dependencies.jar 10'
#/usr/bin/screen -t 'mqtt-java' -dm bash -c  'cd /home/denys/Projects/; java  -XX:+UseSerialGC -Xmx64m -Xms4m -jar mqtt-message-store-1.0-SNAPSHOT-jar-with-dependencies.jar 10'
