echo off
cls
echo MercuryTrade build starting
echo mvn clean package
call mvn clean package
echo Copying MercuryTrade.jar from app/target to release_files
cd app/target
copy MercuryTrade.jar "../../release_files"
cd ../..
echo Launching launch4j.exe to generate MercuryTrade.exe from .jar file
cd launch4j
launch4jc.exe ../release_files/release_config.xml
cd ..
echo build_mercury_bat completed

echo preparing zip files for release starting...
echo zipping with jar file start
cd release_files
echo removing old zip files
del MercuryTrade.jar.zip
del MercuryTrade.exe.zip
copy MercuryTrade.jar MercuryTrade
copy HOW_TO_RUN_JAR.txt MercuryTrade
call powershell Compress-Archive MercuryTrade MercuryTrade.jar.zip
cd MercuryTrade
del MercuryTrade.jar
del HOW_TO_RUN_JAR.txt
cd ..
echo Zipping with jar completed
echo Zipping with exe file start
copy MercuryTrade.exe MercuryTrade
call powershell Compress-Archive MercuryTrade MercuryTrade.exe.zip
cd MercuryTrade
del MercuryTrade.exe
cd ..
echo zipping with exe completed
echo zipping with lang started
echo removing old lang files
del lang.zip
call powershell Compress-Archive ../app-shared/src/main/resources/lang/* lang.zip
echo zipping with lang ended

