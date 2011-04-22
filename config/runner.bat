@echo off
REM ------------------------------------------------------------ 
REM script runner
REM ------------------------------------------------------------ 
%JAVA% -cp jar/twatter.jar org.twatter.main.Twatter
%JAVA% -cp jar/twatter.jar org.twatter.main.TwatterDatabase
