#!/bin/bash
docker run -tid --rm --name=ssa \
        -v /mnt/f/java/screen_shot_analyzer/out:/usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
        -v /mnt/f/java/screen_shot_analyzer/src/web.xml:/usr/local/tomcat/webapps/ROOT/WEB-INF/web.xml \
        -v /mnt/i/adb:/data -p 8080:8080 tomcat:9.0.39-jdk11-openjdk-buster
