@ECHO OFF

set WAR_DIR=%~dp0

set MY_CLASSPATH=%WAR_DIR%;%WAR_DIR%\WEB-INF\lib\*

java -cp "%MY_CLASSPATH%" org.cmdbuild.webapp.cli.Main startedFromExplodedWar %WAR_DIR% %*
