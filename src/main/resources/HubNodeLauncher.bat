@echo off
setlocal ENABLEDELAYEDEXPANSION

echo Host name: '%COMPUTERNAME%'

SET LAUNCHHUB=java -jar "<path>\selenium-server-standalone-3.141.59.jar" -role hub -port 4444 -timeout 1200 -browserTimeout 1200
SET LAUNCHNODE=java -Dwebdriver.chrome.driver="<path>\chromedriver.exe" -Dwebdriver.firefox.driver="<path>\geckodriver.exe" -jar "<path>\selenium-server-standalone-3.141.59.jar" -role node -nodeConfig "<path>\Selenium_Node.json"


	start cmd /k CALL %LAUNCHHUB%
	TIMEOUT 5
	start cmd /k CALL %LAUNCHNODE%

GOTO :eof

:BatUsage
echo Run to launch Hub-node connection
echo Jar file Location:<path>\selenium-server-standalone-3.141.59.jar
echo Chrome Driver Location:<path>\chromedriver.exe
echo Firefox Driver Location:<path>\geckodriver.exe
echo json file Location:<path>\Selenium_Node.json


