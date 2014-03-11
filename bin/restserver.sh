#!/bin/sh

unset CDPATH
workdir=`dirname $0`/..
workdir=`cd "${workdir}" && pwd`
cd "${workdir}"

java -cp `ls ${workdir}/target/*.jar | xargs | tr ' ' ':'` -Duser.timezone=UTC -Dfile.encoding=UTF-8 com.nilhcem.fakesmtp.RestServer 
