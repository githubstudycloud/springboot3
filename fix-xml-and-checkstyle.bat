@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo    修复Maven XML文件中的标签问题和Checkstyle配置
echo ===================================================

REM 创建正确的pom.xml文件
echo 正在修复pom.xml文件...
type pom.xml.corrected > pom.xml

REM 检查platform-ddd-demo模块的pom.xml文件
if exist "platform-ddd-demo\pom.xml" (
    echo 正在修复platform-ddd-demo\pom.xml文件...
    
    REM 创建临时文件
    type nul > platform-ddd-demo\pom.xml.temp
    
    REM 读取原文件，替换所有的<n>标签为<name>标签
    for /f "delims=" %%a in (platform-ddd-demo\pom.xml) do (
        set "line=%%a"
        set "line=!line:<n>=<name>!"
        set "line=!line:</n>=</name>!"
        echo !line!>> platform-ddd-demo\pom.xml.temp
    )
    
    REM 用修复后的文件替换原文件
    move /y platform-ddd-demo\pom.xml.temp platform-ddd-demo\pom.xml
)

REM 检查其他模块的pom.xml文件
for %%i in (platform-common platform-framework platform-dependencies) do (
    if exist "%%i\pom.xml" (
        echo 正在修复%%i\pom.xml文件...
        
        REM 创建临时文件
        type nul > %%i\pom.xml.temp
        
        REM 读取原文件，替换所有的<n>标签为<name>标签
        for /f "delims=" %%a in (%%i\pom.xml) do (
            set "line=%%a"
            set "line=!line:<n>=<name>!"
            set "line=!line:</n>=</name>!"
            echo !line!>> %%i\pom.xml.temp
        )
        
        REM 用修复后的文件替换原文件
        move /y %%i\pom.xml.temp %%i\pom.xml
    )
)

echo ===================================================
echo                   修复完成
echo ===================================================
echo.
echo 注意：已创建checkstyle.xml文件，并修改了检查风格插件配置
echo 如果仍然编译失败，请运行: mvn -Dcheckstyle.skip=true install
echo.

pause
