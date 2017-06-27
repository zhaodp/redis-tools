Redis 数据迁移工具：
之前公司项目都是自己安装的Redis Server,后来项目部署到阿里云上，购买了阿里云的KV Store Redis版，不方便数据迁移和备份，因此写了个工具。
支持阿里云的Redis数据库服务和自建Redis Server。

数据导出：
java -cp redis-tools.jar RedisDumper -h 127.0.0.1 -p 6379 -a password -i 0 -d /usr/local/redis_data/data.txt

数据导入:
java -cp redis-tools.jar RedisLoader -h 127.0.0.1 -p 6379 -a password -i 0 -d /usr/local/redis_data/data.txt

参数说明：
-h Redis Server IP
-p Redis端口，默认6379
-a Redis密码，默认为空
-i Redis DB Index
-d 数据文件路径
