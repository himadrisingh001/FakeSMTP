#!/bin/bash

unset CDPATH
workdir=`dirname $0`/..
workdir=`cd "${workdir}" && pwd`
cd "${workdir}"

java -cp `ls ${workdir}/target/*.jar | xargs | tr ' ' ':'` -Duser.timezone=UTC -Dfile.encoding=UTF-8 com.nilhcem.fakesmtp.RestServer &
pid=$!
echo $pid > ${workdir}/bin/server.pid
echo "Started process with pid ${pid}"
sleep 5

