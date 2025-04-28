@echo off
setlocal enabledelayedexpansion

echo ====================================================
echo 正在修复所有 pom.xml 文件中的错误标签...
echo ====================================================

REM 创建一个临时文件
set TEMP_FILE=temp-pom.xml

REM 修复主 pom.xml
echo 正在修复 pom.xml...
type pom.xml > !TEMP_FILE!
powershell -Command "(Get-Content !TEMP_FILE!) -replace '<n>', '<name>' -replace '</n>', '</name>' | Set-Content -Path 'pom.xml' -Encoding UTF8"

REM 修复 platform-ddd-demo/pom.xml
echo 正在修复 platform-ddd-demo/pom.xml...
type platform-ddd-demo\pom.xml > !TEMP_FILE!
powershell -Command "(Get-Content !TEMP_FILE!) -replace '<n>', '<name>' -replace '</n>', '</name>' | Set-Content -Path 'platform-ddd-demo/pom.xml' -Encoding UTF8"

REM 修复其他模块的 pom.xml (如果存在)
for %%m in (platform-common platform-framework platform-dependencies) do (
    if exist "%%m\pom.xml" (
        echo 正在修复 %%m/pom.xml...
        type %%m\pom.xml > !TEMP_FILE!
        powershell -Command "(Get-Content !TEMP_FILE!) -replace '<n>', '<name>' -replace '</n>', '</name>' | Set-Content -Path '%%m/pom.xml' -Encoding UTF8"
    )
)

REM 删除临时文件
del !TEMP_FILE!

echo ====================================================
echo 所有 pom.xml 文件修复完成！
echo ====================================================

echo.
echo 提示：现在你可以重新运行 Maven 命令 mvn install 来编译项目
echo.

endlocal
