@echo off
REM 修复所有 POM 文件中的 XML 标签问题

echo 开始修复 XML 标签问题...

REM 要处理的文件列表
set FILES=pom.xml platform-dependencies/pom.xml platform-common/pom.xml platform-framework/pom.xml

REM 遍历所有文件进行替换
for %%f in (%FILES%) do (
    echo 处理文件: %%f
    powershell -Command "(Get-Content %%f) -replace '<n>', '<name>' -replace '</n>', '</name>' | Set-Content %%f"
)

echo XML 标签修复完成！按任意键退出...
pause > nul