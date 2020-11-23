#!/bin/bash

export DOLLAR='$'
envsubst < ./src/config/nginx/nginx.conf.template > /etc/nginx/nginx.conf
nginx -g "daemon off;"

echo ">>>>>>>>>>>>>>>> TransPaaS 启动成功 <<<<<<<<<<<<<<<<<"
