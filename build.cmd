@echo off

call mvn clean compile
call mvn -Pnative -Dagent exec:exec@java-agent
call mvn -Pnative -Dagent package
call mvn -Pnative exec:exec@native