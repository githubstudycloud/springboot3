@echo off
REM 修复 XML 标签问题的批处理脚本

echo 开始修复 XML 标签问题...

REM 修复主 pom.xml
powershell -Command "(Get-Content pom.xml) -replace '<n>', '<name>' -replace '</n>', '</name>' | Set-Content pom.xml"

REM 修复 platform-dependencies pom.xml
powershell -Command "(Get-Content platform-dependencies/pom.xml) -replace '<n>', '<name>' -replace '</n>', '</name>' | Set-Content platform-dependencies/pom.xml"

REM 修复 platform-common pom.xml
powershell -Command "(Get-Content platform-common/pom.xml) -replace '<n>', '<name>' -replace '</n>', '</name>' | Set-Content platform-common/pom.xml"

REM 修复 platform-framework pom.xml
powershell -Command "(Get-Content platform-framework/pom.xml) -replace '<n>', '<name>' -replace '</n>', '</name>' | Set-Content platform-framework/pom.xml"

echo XML 标签修复完成！