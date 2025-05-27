@echo off

for /f "tokens=1,2 delims==" %%a in (config.conf) do (
    set %%a=%%b
)

if "%~3"=="" (
    echo use: %~nx0 workbench.jar addKeys.py filter.jar
    exit /b 1
)
REM workbench
set JAR1=%1
REM add keys
set PYTHON_SCRIPT=%2
REM filters
set JAR2=%3
set PSEUDOKNOT_PARAM=
if not "%MATCHER_TOLERANCE_PSEUDOKNOT%"=="" (
    set PSEUDOKNOT_PARAM=-p "%MATCHER_TOLERANCE_PSEUDOKNOT%"
)
java -jar "%JAR1%" "%MATCHER_INPUT%" "%MATCHER_OUTPUT%" -n "%MATCHER_PATTERN_SEQ%" -b "%MATCHER_PATTERN_BOND%" -st "%MATCHER_TOLERANCE_SEQ%" -ml "%MATCHER_ML%" -bt "%MATCHER_TOLERANCE_BOND%" -pt "%MATCHER_TOLERANCE_NP%" -ct "%MATCHER_TOLERANCE_CONS_BOND%" %PSEUDOKNOT_PARAM%

python "%PYTHON_SCRIPT%" "%PY_ADD_KEY_MATCH%" "%PY_ADD_KEY_XLSX%" "%PY_ADD_KEY_OUT%"

java -jar "%JAR2%" "%FILTER_INPUT%" "%FILTER_OUTPUT%" -t "%FILTER_TOLERANCE%" -p "%FILTER_PDB%"