@echo off
for %%i in (.\build\libs\DreamHouse-bot-*-all.jar) do (
    java -jar %%i
    echo "Bot disconnected"
    goto :BREAK
)
:BREAK
pause