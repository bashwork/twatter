@echo off
REM ------------------------------------------------------------ 
REM script config
REM ------------------------------------------------------------ 
set JAVA=%JAVA_HOME%
set START=%JAVA% -cp jar/twatter.jar org.twatter.main
set OPTIONS=%2 %3 %4 %5

REM ------------------------------------------------------------ 
REM script runner
REM ------------------------------------------------------------ 
if "%1" == "help" (
  :help
  echo "Usage: %0 {index|search|merge|twatter|database|summarize}"
  goto end
)

if "%1" == "index" (
  %START%.TwatterIndexer %OPTIONS%
  goto end
)
if "%1" == "search" (
  %START%.TwatterSearcher %OPTIONS%
  goto end
)
if "%1" == "merge" (
  %START%.TwatterMerger %OPTIONS%
  goto end
)
if "%1" == "twatter" (
  %START%.Twatter %OPTIONS%
  goto end
)
if "%1" == "database" (
  %START%.TwatterDatabase %OPTIONS%
  goto end
)
if "%1" == "summarize" (
  %START%.TwatterSummarizer %OPTIONS%
  goto end
)
if "%1" == "Parse" (
  %START%.TwatterParser %OPTIONS%
  goto end
)

goto help
:end
