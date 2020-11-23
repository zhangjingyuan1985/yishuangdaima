# TransPaaS
It's TransPaaS docker compose file, to run TransPaaS products with docker on one single machine.

```
                      TransPaaS前端
                  transpaas.abc.com:80
                     基于Nginx1.17.4
                           +
       +-----------------------------------------+
       |                   |                     |
       v                   v                     v
  TransPaaS后端          系统管理             接口管理网关
 [host:tp:8080]   [host:sysman:9051]      [host:zuul:10088]
       +                   +                     |
       |                   |                     |
       +-----------------------------------------+
                           |
                           v
                        PostgreSQL
                   [host:database:5432]
                           +
                           |
       +------------------------------------------+
       |                   |                      |
       v                   v                      v
    [db:tp]             [db:sysman]              [db:zuul]
```

# 快速开始

1. 内网环境（可连内网Docker Registry）

```
docker network create transpaas-network
cp .env.sample .env
docker-compose up -d
```

2. 其他环境
* 首先把镜像同步到需要部署的机器

```
$ docker save -o ./paas-view.image nexus.sutpc.cc:9091/paas-view_nginx1.17.4:1.0.0
$ docker save -o ./paas-cloud.image nexus.sutpc.cc:9091/paas-cloud:latest
$ docker save -o ./auth-1.0.image nexus.sutpc.cc:9091/auth-1.0:latest
$ docker save -o ./api-zuul.image nexus.sutpc.cc:9091/api-zuul:latest
$ docker save -o ./pgsql.image postgres:11.4
$ docker save -o ./s3.image minio/minio:latest
$ scp *.image root@targethost:~/

% docker load -i xxx.image
% ...
```
* 然后同1

# 数据初始化

* 对象存储初始化
```
unzip minio1.zip
mv data1/* volume_data/minio1/data/
```

数据的初始化主要包括：
* 1. tp的指标库元数据
* 2. sysman的初始化数据

需要初始化的数据需要以csv格式放置在 volume_data/{sysman|tp}/init 目录下，然后 docker-compose.yml 中相应的 spring 配置增加 load profile。文件必须为：
* 1. algorithm_info.csv, index_common_query.csv, index_foreign_key.csv, index_info.csv, index_second_layer_description.csv, tree_node.csv
* 2. t_sys_action.csv, t_sys_menu.csv, t_sys_profiles_value.csv, t_sys_profiles.csv

目前需要注意的是 1 中的所有数据在导出成csv格式时，所有日期类型需去掉，并且tree_node这个表里面的数据title为root的parent要手动设置为NULL，否则无法实现数据初始化

# 单个表数据更新，以t_sys_action 为例

* 拷贝csv文件到容器中
```
docker cp sysman/init/t_sys_action.csv database:/var/lib/postgresql/data/
```
* （清除旧数据）插入新数据
```
$ docker exec -tiu postgres database psql -d sysman #其中database为容器名称，sysman为要连接的数据库名
sysman=# delete from t_sys_action;
sysman=# \copy t_sys_action FROM '/var/lib/postgresql/data/t_sys_action.csv' DELIMITER ',' CSV HEADER;
COPY 15
```

# 整体数据更新，以 tp 数据库为例
```
docker exec -u postgres database pg_dump -Cc tp > tp-backup-$(date -u +%Y-%m-%d).sql
```
```
# 先暂停容器tp
$ docker exec -tiu postgres database psql
postgres=# drop database tp;

$ psql -U postgres -h localhost -W < tp-backup-2019-12-10.sql #前提是database容器5432端口已映射到宿主机
$ docker-compose down
```

* 然后修改docker-compose.yml中tp的启动参数配置如下，即去掉load
```
  tp:
    environment:
      - SPRING_PROFILES_ACTIVE=prod
```

* 然后启动
```
$ docker-compose up -d
```
# 在没有指标库的情况下，需要先建立一个虚拟指标库，运行
