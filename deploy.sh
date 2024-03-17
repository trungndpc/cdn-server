local_dir=./
server_dir=/home/stackops/home/production/cdn-server/webapp

server=stackops@61.28.229.63
jar_file=cdn-0.0.1-SNAPSHOT.jar

rsync -a $local_dir/target/$jar_file $server:$server_dir/jar/