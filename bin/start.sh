#!/bin/bash

for c in `ls lib/`
do 
	classpath="lib/$c"":"$classpath
done
echo $classpath
nohup java -Xms512M -Xmx1024M -cp conf/:$classpath cn.enilu.xuanyuan.application.Main > /dev/null 2>&1 &
