@echo off
setlocal

rem Define variables
set "local_dir=.\"
set "server_dir=/home/stackops/home/production/cdn-server/webapp"
set "server=stackops@61.28.229.63"
set "jar_file=cdn-0.0.1-SNAPSHOT.jar"

rem Execute scp command
scp "%local_dir%target\%jar_file%" "%server%:%server_dir%/jar/"

endlocal