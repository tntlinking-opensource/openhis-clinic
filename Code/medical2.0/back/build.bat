@echo off
set "JAVA_HOME=C:\Program Files (x86)\Java\jdk1.8.0_71"
set "PATH=C:\Program Files (x86)\Java\jdk1.8.0_71\bin;%PATH%"
cd /d D:\IdeaProject\oh-cloudclinic\Code\medical2.0\back
D:\Maven\apache-maven-3.8.6\bin\mvn.cmd spring-boot:run
