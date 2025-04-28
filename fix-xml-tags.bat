@echo off
echo 正在修复XML标签错误...

set TEMP_FILE=pom.xml.temp
set POM_FILE=pom.xml

type nul > %TEMP_FILE%

for /f "tokens=*" %%a in (%POM_FILE%) do (
    set line=%%a
    set line=!line:<n>V6平台 - 父项</n>=<name>V6平台 - 父项</name>!
    set line=!line:<n>Developer Name</n>=<name>Developer Name</name>!
    echo !line! >> %TEMP_FILE%
)

move /y %TEMP_FILE% %POM_FILE%

echo XML标签修复完成。
