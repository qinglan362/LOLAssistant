@echo off
:: 请求管理员权限
>nul 2>&1 "%SYSTEMROOT%\system32\cacls.exe" "%SYSTEMROOT%\system32\config\system"
if '%errorlevel%' NEQ '0' (
    echo 请求管理员权限...
    goto UACPrompt
) else ( goto gotAdmin )

:UACPrompt
    echo Set UAC = CreateObject^("Shell.Application"^) > "%temp%\getadmin.vbs"
    echo UAC.ShellExecute "%~s0", "", "", "runas", 1 >> "%temp%\getadmin.vbs"
    "%temp%\getadmin.vbs"
    exit /B

:gotAdmin
    if exist "%temp%\getadmin.vbs" ( del "%temp%\getadmin.vbs" )
    pushd "%CD%"
    CD /D "%~dp0"

:: 设置输出文件路径
set "outputFile=%~dp0yxlmpeizhi.txt"

:: 如果文件存在，则删除它
if exist "%outputFile%" del "%outputFile%"

:: 执行 WMIC 命令并将结果输出到临时文件
wmic process where caption='LeagueClientUx.exe' get commandline > "%temp%\temp_output.txt"

:: 使用 PowerShell 将临时文件转换为 UTF-8 编码并保存到最终输出文件
powershell -Command "Get-Content '%temp%\temp_output.txt' | Set-Content -Encoding UTF8 '%outputFile%'"

:: 删除临时文件
del "%temp%\temp_output.txt"

echo 命令已执行完毕。
if exist "%outputFile%" (
    echo 结果已保存到 %outputFile%（UTF-8 编码）
) else (
    echo 未找到 LeagueClientUx.exe 进程或执行出错
)

pause