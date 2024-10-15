# ELK

https://www.elastic.co/elastic-stack/

技术栈：Elasticsearch、Logstash、Kibana。



## Elasticsearch

https://www.elastic.co/downloads/elasticsearch

老版本地址：https://www.elastic.co/downloads/past-releases#elasticsearch



### 安装

~~~bash
tar -zxvf elasticsearch-8.15.0-linux-x86_64.tar.gz

cd elasticsearch-8.15.0/

bin/elasticsearch

# root用户需要新建用户启动
useradd elasticsearch
passwd oycm0610
# 更改/usr/local/elasticsearch-8.15.0的所有者为elasticsearch
chown elasticsearch /usr/local/elasticsearch-8.15.0
chmod u+rwx /usr/local/elasticsearch-8.15.0
usermod -d /usr/local/elasticsearch-8.15.0 elasticsearch
userdel username

# 默认以https协议启动

~~~

不调整配置直接启动，需要启动需要4g的内存。

![image-20240829105906132](http://47.101.155.205/image-20240829105906132.png)

![image-20240829112121202](http://47.101.155.205/image-20240829112121202.png)



![image-20240830161741349](http://47.101.155.205/image-20240830161741349.png)

![image-20240830162103879](http://47.101.155.205/image-20240830162103879.png)

### 启动

![image-20240904104649232](http://47.101.155.205/image-20240904104649232.png)

~~~yml
# 禁用https协议
xpack.security.enabled: false

xpack.security.enabled: false

~~~



~~~bash
# 查找elasticsearch的进程pid
ps aux | grep elasticsearch
# 安全干掉进程
kill -SIGTERM <PID>

# 后台启动
nohup ./bin/elasticsearch > ./logs/elasticsearch.log 2>&1 &

~~~

![image-20240905150548338](http://47.101.155.205/image-20240905150548338.png)



## Kibana

### 安装启动

~~~bash
tar -zvxf kibana-8.15.0-linux-x86_64.tar.gz

cd kibana-8.15.0/

# 启动命令,启动之后，访问地址后面的6位验证码连接ES使用
./bin/kibana

# https启动的ES会打印密码及可供kibana连接的密钥
elastic/_pwLsbtGW1aIsawB1IdV # 登录

~~~

![image-20240830162446634](http://47.101.155.205/image-20240830162446634.png)

![image-20240830161823841](http://47.101.155.205/image-20240830161823841.png)

![image-20240830162254438](http://47.101.155.205/image-20240830162254438.png)

![image-20240904112326678](http://47.101.155.205/image-20240904112326678.png)

### Http协议连接ES(中文)

编辑kibana.yml文档。

~~~kibana.yml
# 中文
i18n.locale: "zh-CN"

~~~

~~~bash
# 后台启动
nohup ./bin/kibana > ./logs/kibana.log 2>&1 &

~~~



![image-20240904112641605](http://47.101.155.205/image-20240904112641605.png)

![image-20240904125421824](http://47.101.155.205/image-20240904125421824.png)



### Kibana使用文档

https://www.elastic.co/guide/en/kibana/current/introduction.html

需要选中一个索引才能渲染数据。

![image-20240902142819308](http://47.101.155.205/image-20240902142819308.png)



## Kibana操作ES指南

Elasticsearch操作文档：https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html

https://www.elastic.co/guide/en/elasticsearch/reference/current/getting-started.html

Kibana开发工具

![image-20240830163743569](http://47.101.155.205/image-20240830163743569.png)

执行调用Elasticsearch的接口。

![image-20240830164008727](http://47.101.155.205/image-20240830164008727.png)



~~~bash
curl -u elastic:_pwLsbtGW1aIsawB1IdV https://192.168.125.165:9200/

~~~

![image-20240830164939589](http://47.101.155.205/image-20240830164939589.png)



### 往ES索引中添加数据

~~~bash
POST books/_doc
{"name": "fine", "author": "oycm", "release_date": "2024-08-30", "page_count": 360}

~~~

![image-20240830165211377](http://47.101.155.205/image-20240830165211377.png)



### 索引中添加多条数据

~~~bash
POST /_bulk
{ "index" : { "_index" : "books" } }
{"name": "Revelation Space", "author": "Alastair Reynolds", "release_date": "2000-03-15", "page_count": 585}
{ "index" : { "_index" : "books" } }
{"name": "1984", "author": "George Orwell", "release_date": "1985-06-01", "page_count": 328}
{ "index" : { "_index" : "books" } }
{"name": "Fahrenheit 451", "author": "Ray Bradbury", "release_date": "1953-10-15", "page_count": 227}
{ "index" : { "_index" : "books" } }
{"name": "Brave New World", "author": "Aldous Huxley", "release_date": "1932-06-01", "page_count": 268}
{ "index" : { "_index" : "books" } }
{"name": "The Handmaids Tale", "author": "Margaret Atwood", "release_date": "1985-06-01", "page_count": 311}

~~~

![image-20240830170124073](http://47.101.155.205/image-20240830170124073.png)



### 搜索索引中的数据

~~~bash
GET books/_search

~~~

![image-20240830171009681](http://47.101.155.205/image-20240830171009681.png)



#### 匹配搜索

~~~bash
GET books/_search
{
  "query": {
    "match": {
      "name": "brave"
    }
  }
}

~~~

![image-20240830171151084](http://47.101.155.205/image-20240830171151084.png)



~~~bash
GET books/_search
{
  "query": {
    "match": {
      "name": "The"
    }
  }
}

~~~

![image-20240830171340140](http://47.101.155.205/image-20240830171340140.png)



~~~bash
GET books/_search
{
  "query": {
    "match": {
      "name": "fi"
    }
  }
}

~~~

![image-20240830171410734](http://47.101.155.205/image-20240830171410734.png)



~~~bash
GET books/_search
{
  "query": {
    "match": {
      "name": "fine"
    }
  }
}

~~~

![image-20240830171433547](http://47.101.155.205/image-20240830171433547.png)



### 删除索引中匹配的数据

~~~bash
POST /log-nginx-2024-09/_delete_by_query
{
  "query": {
    "term": {
      "current_date": "2024-09-10"
    }
  }
}

~~~





### 搜索接口api

https://www.elastic.co/guide/en/elasticsearch/reference/current/search-with-elasticsearch.html

https://www.elastic.co/guide/en/elasticsearch/reference/current/search-search.html





### 添加数据进阶

kibana插入数据：https://www.elastic.co/guide/en/kibana/8.15/sample-data.html

上传文件：https://www.elastic.co/guide/en/kibana/8.15/sample-data.html

使用bulk数据到es：https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html



### ES客户端

官网文档：https://www.elastic.co/guide/en/elasticsearch/client/index.html





## Logstash

官网地址：https://www.elastic.co/logstash

使用文档首页：https://www.elastic.co/guide/en/logstash/current/index.html

快速开始使用文档：https://www.elastic.co/guide/en/logstash/current/getting-started-with-logstash.html

### 安装

下载地址：https://www.elastic.co/downloads/logstash

~~~bash
tar -zxvf logstash-8.15.0-linux-x86_64.tar.gz

cd logstash-8.15.0

~~~



### 启动

Logstash启动需要两个必须元素input和output，还有一个可选元素filter。input指定数据的来源，filter指定怎么修改数据，output决定将结果输出到哪。

![image-20240902145136949](http://47.101.155.205/image-20240902145136949.png)



~~~bash
# 启动
# -e 从命令行读取启动配置
# input stdin表示是标准的input输入
# output stdout表示是标准的output输出 意味着在控制台输入和输出
bin/logstash -e 'input { stdin { } } output { stdout {} }'
# ctrl + d停止

# --config.test_and_exit配置,解析配置文件并记录错误(并不启动)
# --config.reload.automatic修改配置即生效启动
bin/logstash -f config/first-pipeline.conf --config.test_and_exit
bin/logstash -f config/first-pipeline.conf --config.reload.automatic

~~~

![image-20240902145821304](http://47.101.155.205/image-20240902145821304.png)

![image-20240902150410418](http://47.101.155.205/image-20240902150410418.png)



### 结合Filebeat解析日志

官网文档介绍：https://www.elastic.co/guide/en/logstash/current/advanced-pipeline.html

使用Filebeat解析的日志作为input，从日志中自定义修改，输出到Elasticsearch。



#### 配置Filebeat发送日志到Logstash

git仓库地址：https://github.com/elastic/beats/tree/main/filebeat

Filebeat下载地址：https://www.elastic.co/downloads/beats/filebeat

Filebeat使用文档：https://www.elastic.co/guide/en/beats/filebeat/current/filebeat-installation-configuration.html

正常情况，Filebeat不会和Logstash运行在同一台计算机。

~~~bash
# curl命令下载压缩包
curl -L -O https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-8.15.0-linux-x86_64.tar.gz
# 解压缩
tar -zvxf filebeat-8.15.0-linux-x86_64.tar.gz

# 解压缩.log.gz日志文件得到文件logstash-tutorial.log
gzip -d logstash-tutorial.log.gz

# 新建一个filebeat_test.yml配置文件
touch filebeat_test.yml
# 启动报错提示执行,没有文件权限
chmod go-w /u01/lgsa/local/filebeat-8.15.0-linux-x86_64/filebeat_test.yml

# 启动filebeat,启动之后会一直尝试连接
./filebeat -e -c filebeat_test.yml -d "publish"

~~~



~~~filebeat.yml
filebeat.inputs:
- type: log
  paths:
    - /u01/lgsa/local/logstash-tutorial.log
output.logstash:
  hosts: ["localhost:5044"]

~~~

![image-20240902160825291](http://47.101.155.205/image-20240902160825291.png)



#### 使用beats(input)

~~~bash
cp logstash-sample.conf first-pipeline.conf

# 修改first-pipeline.conf文件内容

# --config.test_and_exit配置,解析配置文件并记录错误(并不启动)
# --config.reload.automatic修改配置即生效启动
bin/logstash -f config/first-pipeline.conf --config.test_and_exit
bin/logstash -f config/first-pipeline.conf --config.reload.automatic

# message是filebeat解析日志的源数据

~~~

~~~first-pipeline.conf
input {
  beats {
    port => 5044
  }
}

output {
  stdout {
    codec => rubydebug # 日志数据会以键值对的方式展示
  }
}

~~~

![image-20240902163226343](http://47.101.155.205/image-20240902163226343.png)

![image-20240902163537624](http://47.101.155.205/image-20240902163537624.png)

![image-20240903133529458](http://47.101.155.205/image-20240903133529458.png)



### 使用grok过滤器

待解析日志记录行。

~~~log
83.149.9.216 - - [04/Jan/2015:05:13:42 +0000] 
"GET /presentations/logstash-monitorama-2013/images/kibana-search.png HTTP/1.1" 
200 203023 
"http://semicomplete.com/presentations/logstash-monitorama-2013/" 
"Mozilla/5.0 (Macintosh; IntelMac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.77 Safari/537.36"

~~~

默认会将日志解析成key=value的json格式，json的key以最终结果为准。其中@version、@timestamp、tags(解析错误才有)是使用logstash后自动产生的字段。

| 字段名      | 内容                                                         | 信息         |
| ----------- | ------------------------------------------------------------ | ------------ |
| clientip    | 83.149.9.216                                                 | 客户端ip     |
| ident       | -                                                            |              |
| auth        | -                                                            |              |
| timestamp   | 04/Jan/2015:05:13:42 +0000                                   | 请求记录时间 |
| verb        | GET                                                          | 请求方式     |
| request     | /presentations/logstash-monitorama-2013/images/kibana-search.png | uri          |
| httpversion | 1.1                                                          | 请求版本     |
| response    | 200                                                          | 响应状态码   |
| bytes       | 203023                                                       | 响应体字节数 |
| referrer    | http://semicomplete.com/presentations/logstash-monitorama-2013/ |              |
| agent       | Mozilla/5.0 (Macintosh; IntelMac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.77 Safari/537.36 | 客户端版本   |

![image-20240902164057049](http://47.101.155.205/image-20240902164057049.png)



#### 结合标准input和output(json)使用

~~~first-pipeline.conf
input {
    stdin {}
}
filter {
    grok {
        match => { "message" => "%{COMBINEDAPACHELOG}"}
    }
}
output {
    stdout { codec => json } # json_lines 每行输出json
}

~~~



~~~log
86.1.76.62 - - [04/Jan/2015:05:30:37 +0000] "GET /style2.css HTTP/1.1" 200 4877 "http://www.semicomplete.com/projects/xdotool/" "Mozilla/5.0 (X11; Linux x86_64; rv:24.0) Gecko/20140205 Firefox/24.0 Iceweasel/24.3.0"

~~~

![image-20240903143712930](http://47.101.155.205/image-20240903143712930.png)

![image-20240903143107146](http://47.101.155.205/image-20240903143107146.png)



~~~log
127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326 "http://www.example.com/start.html" "Mozilla/4.08 [en] (Win98; I ;Nav)"

~~~

![image-20240903144247105](http://47.101.155.205/image-20240903144247105.png)



#### 结合标准input和output(rubydebug)使用

~~~first-pipeline.conf
input {
    stdin {}
}
filter {
    grok {
        match => { "message" => "%{COMBINEDAPACHELOG}"}
    }
}
output {
    stdout { codec => rubydebug }
}

~~~



~~~log
86.1.76.62 - - [04/Jan/2015:05:30:37 +0000] "GET /style2.css HTTP/1.1" 200 4877 "http://www.semicomplete.com/projects/xdotool/" "Mozilla/5.0 (X11; Linux x86_64; rv:24.0) Gecko/20140205 Firefox/24.0 Iceweasel/24.3.0"

~~~

![image-20240903144701170](http://47.101.155.205/image-20240903144701170.png)



~~~log
127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326 "http://www.example.com/start.html" "Mozilla/4.08 [en] (Win98; I ;Nav)"

~~~

![image-20240903144811485](http://47.101.155.205/image-20240903144811485.png)





#### 结合input(filebeat)和output(rubudub)使用

~~~first-pipeline.conf
input {
    beats {
        port => "5044"
    }
}
filter {
    grok {
        match => { "message" => "%{COMBINEDAPACHELOG}"}
    }
}
output {
    stdout { codec => rubydebug }
}

~~~

~~~filebeat_test.yml
# filebeat启动配置文件
filebeat.inputs:
- type: log
  paths:
    - /u01/lgsa/local/logstash-tutorial.log
output.logstash:
  hosts: ["localhost:5044"]

~~~





~~~bash
# filebeat已经读取了文件,停止filebeat(ctrl + c),然后再删除读取记录
rm -rf data/registry

./filebeat -e -c filebeat_test.yml -d "publish"

~~~

![image-20240902165555287](http://47.101.155.205/image-20240902165555287.png)

![image-20240902170810021](http://47.101.155.205/image-20240902170810021.png)

![image-20240903133815542](http://47.101.155.205/image-20240903133815542.png)



#### 结合input(filebeat)和output(json)使用

~~~first-pipeline.conf
input {
    beats {
        port => "5044"
    }
}
filter {
    grok {
        match => { "message" => "%{COMBINEDAPACHELOG}"}
    }
}
output {
    file { 
        codec => json
        path => "/u01/lgsa/local/logstash-8.15.0/logs/custom-json.log"
    }
}

~~~

filebeat配置文件不变

~~~bash
# 让filebeat重新推送日志
rm -rf data/registry/

./filebeat -e -c filebeat_test.yml -d "publish"

~~~

打印的json数据

~~~json
{
    "@version": "1",
    "message": "83.149.9.216 - - [04/Jan/2015:05:13:42 +0000] \"GET /presentations/logstash-monitorama-2013/images/kibana-search.png HTTP/1.1\" 200 203023 \"http://semicomplete.com/presentations/logstash-monitorama-2013/\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.77 Safari/537.36\"",
    "input": {
        "type": "log"
    },
    "http": {
        "version": "1.1",
        "response": {
            "status_code": 200,
            "body": {
                "bytes": 203023
            }
        },
        "request": {
            "method": "GET",
            "referrer": "http://semicomplete.com/presentations/logstash-monitorama-2013/"
        }
    },
    "timestamp": "04/Jan/2015:05:13:42 +0000",
    "event": {
        "original": "83.149.9.216 - - [04/Jan/2015:05:13:42 +0000] \"GET /presentations/logstash-monitorama-2013/images/kibana-search.png HTTP/1.1\" 200 203023 \"http://semicomplete.com/presentations/logstash-monitorama-2013/\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.77 Safari/537.36\""
    },
    "log": {
        "offset": 0,
        "file": {
            "path": "/u01/lgsa/local/logstash-tutorial.log"
        }
    },
    "agent": {
        "name": "V-YF116-D0099-HGJF001",
        "version": "8.15.0",
        "ephemeral_id": "6fec3991-12dc-4902-8c09-dcdcd75c7e41",
        "type": "filebeat",
        "id": "07170b2c-6b9b-4c8d-a4ef-46f79737b213"
    },
    "@timestamp": "2024-09-03T07:30:05.754Z",
    "user_agent": {
        "original": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.77 Safari/537.36"
    },
    "ecs": {
        "version": "8.0.0"
    },
    "tags": [
        "beats_input_codec_plain_applied"
    ],
    "source": {
        "address": "83.149.9.216"
    },
    "host": {
        "name": "V-YF116-D0099-HGJF001"
    },
    "url": {
        "original": "/presentations/logstash-monitorama-2013/images/kibana-search.png"
    }
}

~~~



![image-20240903153519318](http://47.101.155.205/image-20240903153519318.png)



### 使用geoip过滤器

解析ip地址

~~~first-pipeline.conf
input {
    beats {
        port => "5044"
    }
}
 filter {
    grok {
        match => { "message" => "%{COMBINEDAPACHELOG}"}
    }
    geoip {
        source => "clientip"
    }
}
output {
    stdout { codec => rubydebug }
}

~~~

插件修改后，启动不支持(现在应该是没有自带geoip所需要的数据源)。

~~~bash
# 查询geoip插件是否存在
bin/logstash-plugin list --verbose | grep logstash-filter-geoip

# 不存在,安装geoip插件命令
bin/logstash-plugin install logstash-filter-geoip

~~~

没有解析数据

![image-20240902172451119](http://47.101.155.205/image-20240902172451119.png)



#### 下载geoip数据源并使用

https://dev.maxmind.com/geoip/geolite2-free-geolocation-data

地址注册登录之后下载

~~~bash
tar -zxvf GeoLite2-City_20240830.tar.gz

# 启动filebeat
./filebeat -e -c filebeat_test.yml -d "publish"

# 启动logstash
bin/logstash -f config/first-pipeline.conf --config.test_and_exit
bin/logstash -f config/first-pipeline.conf --config.reload.automatic

# source,target,database配置都需要才能解析

~~~

~~~first-pipeline.conf
geoip {
    source => "client_ip"   # 指定包含IP地址的字段,josn多级配置格式[k1][k11]
    target => "geoip"       # 输出的目标字段
    database => "/u01/lgsa/local/GeoLite2-City_20240830/GeoLite2-City.mmdb"  # 指定数据库路径
    # optional：指定需要的字段
    fields => ["city_name", "country_name", "latitude", "longitude"]
}

~~~

启动配置文件

~~~first-pipeline.conf
input {
    beats {
        port => "5044"
    }
}
 filter {
    grok {
        match => { "message" => "%{COMBINEDAPACHELOG}"}
    }
    geoip {
        source => "[source][address]" # 前面解析的ip地址格式为"source": {"address": "83.149.9.216"}
        target => "geoip"
        database => "/u01/lgsa/local/GeoLite2-City_20240830/GeoLite2-City.mmdb"
    }
}
output {
    file { 
    	codec => json_lines # json输出后换行
    	path => "/u01/lgsa/local/logstash-8.15.0/logs/custom-geoip-json.log"
    }
}

~~~

解析后效果

~~~json
{
    "@version": "1",
    "message": "83.149.9.216 - - [04/Jan/2015:05:13:45 +0000] \"GET /presentations/logstash-monitorama-2013/css/fonts/Roboto-Bold.ttf HTTP/1.1\" 200 38720 \"http://semicomplete.com/presentations/logstash-monitorama-2013/\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.77 Safari/537.36\"",
    "input": {
        "type": "log"
    },
    "http": {
        "version": "1.1",
        "response": {
            "status_code": 200,
            "body": {
                "bytes": 38720
            }
        },
        "request": {
            "method": "GET",
            "referrer": "http://semicomplete.com/presentations/logstash-monitorama-2013/"
        }
    },
    "timestamp": "04/Jan/2015:05:13:45 +0000",
    "event": {
        "original": "83.149.9.216 - - [04/Jan/2015:05:13:45 +0000] \"GET /presentations/logstash-monitorama-2013/css/fonts/Roboto-Bold.ttf HTTP/1.1\" 200 38720 \"http://semicomplete.com/presentations/logstash-monitorama-2013/\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.77 Safari/537.36\""
    },
    "log": {
        "offset": 1945,
        "file": {
            "path": "/u01/lgsa/local/logstash-tutorial.log"
        }
    },
    "agent": {
        "version": "8.15.0",
        "ephemeral_id": "e9d3554d-b5b9-4809-bac7-2a841947723e",
        "name": "V-YF116-D0099-HGJF001",
        "type": "filebeat",
        "id": "07170b2c-6b9b-4c8d-a4ef-46f79737b213"
    },
    "@timestamp": "2024-09-03T07:54:25.356Z",
    "user_agent": {
        "original": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.77 Safari/537.36"
    },
    "ecs": {
        "version": "8.0.0"
    },
    "geoip": {
        "ip": "83.149.9.216",
        "geo": {
            "location": {
                "lat": 55.7483,
                "lon": 37.6171
            },
            "country_iso_code": "RU",
            "city_name": "Moscow",
            "postal_code": "102210",
            "country_name": "Russia",
            "region_iso_code": "RU-MOW",
            "continent_code": "EU",
            "region_name": "Moscow",
            "timezone": "Europe/Moscow"
        }
    },
    "tags": [
        "beats_input_codec_plain_applied"
    ],
    "source": {
        "address": "83.149.9.216"
    },
    "host": {
        "name": "V-YF116-D0099-HGJF001"
    },
    "url": {
        "original": "/presentations/logstash-monitorama-2013/css/fonts/Roboto-Bold.ttf"
    }
}

~~~



![image-20240903160128888](http://47.101.155.205/image-20240903160128888.png)



#### nginx日志grok和geoip过滤器解析效果

~~~conf
# nginx记录访问日志格式
'$remote_addr - $remote_user [$time_local] "$request" '
'$status $body_bytes_sent "$http_referer" '
'"$http_user_agent" "$http_x_forwarded_for"'

~~~



~~~log
180.166.146.226 - - [03/Sep/2024:16:05:47 +0800] "GET /image-20240830162103879.png HTTP/1.1" 200 21052 "-" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Typora/0.9.86 Chrome/76.0.3809.146 Electron/6.1.4 Safari/537.36" "-"

~~~

~~~json
{
    "@version": "1",
    "message": "180.166.146.226 - - [03/Sep/2024:16:05:47 +0800] \"GET /image-20240830162103879.png HTTP/1.1\" 200 21052 \"-\" \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Typora/0.9.86 Chrome/76.0.3809.146 Electron/6.1.4 Safari/537.36\" \"-\"",
    "input": {
        "type": "log"
    },
    "http": {
        "version": "1.1",
        "response": {
            "status_code": 200,
            "body": {
                "bytes": 21052
            }
        },
        "request": {
            "method": "GET"
        }
    },
    "timestamp": "03/Sep/2024:16:05:47 +0800",
    "event": {
        "original": "180.166.146.226 - - [03/Sep/2024:16:05:47 +0800] \"GET /image-20240830162103879.png HTTP/1.1\" 200 21052 \"-\" \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Typora/0.9.86 Chrome/76.0.3809.146 Electron/6.1.4 Safari/537.36\" \"-\""
    },
    "log": {
        "offset": 1039,
        "file": {
            "path": "/u01/lgsa/local/filebeat-8.15.0-linux-x86_64/nginx.log"
        }
    },
    "agent": {
        "name": "V-YF116-D0099-HGJF001",
        "version": "8.15.0",
        "ephemeral_id": "9c4c75e9-744a-4371-9f68-983d882210b3",
        "type": "filebeat",
        "id": "07170b2c-6b9b-4c8d-a4ef-46f79737b213"
    },
    "@timestamp": "2024-09-03T08:27:53.743Z",
    "user_agent": {
        "original": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Typora/0.9.86 Chrome/76.0.3809.146 Electron/6.1.4 Safari/537.36"
    },
    "ecs": {
        "version": "8.0.0"
    },
    "geoip": {
        "ip": "180.166.146.226",
        "geo": {
            "location": {
                "lat": 31.2222,
                "lon": 121.4581
            },
            "country_iso_code": "CN",
            "city_name": "Shanghai",
            "region_iso_code": "CN-SH",
            "country_name": "China",
            "timezone": "Asia/Shanghai",
            "continent_code": "AS",
            "region_name": "Shanghai"
        }
    },
    "tags": [
        "beats_input_codec_plain_applied"
    ],
    "source": {
        "address": "180.166.146.226"
    },
    "host": {
        "name": "V-YF116-D0099-HGJF001"
    },
    "url": {
        "original": "/image-20240830162103879.png"
    }
}

~~~

![image-20240903163353524](http://47.101.155.205/image-20240903163353524.png)



###  output to ELasticsearch

output格式

~~~first-pipeline.conf
# output
output {
	elasticsearch {
		hosts => ["请求协议Elasticsearch ServerIp + port"]
		index => "Elasticsearch索引名称"
		#user => "elastic"
    	#password => "changeme"
	}
}

~~~

没有配置到es的索引名称，默认的索引是数据流，索引名称不是官网文档的logstash-YYYY.MM.DD格式，而是生成了一个名为logs-generic-default的数据流。


~~~first-pipeline.conf
input {
    beats {
        port => "5044"
    }
}
 filter {
    grok {
        match => { "message" => "%{COMBINEDAPACHELOG}"}
    }
    geoip {
        source => "[source][address]"
        target => "geoip"
        database => "/u01/lgsa/local/GeoLite2-City_20240830/GeoLite2-City.mmdb"
    }
}
output {
    elasticsearch {
		hosts => ["http://192.168.125.165:9200"]
	}
}

~~~



~~~bash
# 启动filebeat
./filebeat -e -c filebeat_test.yml -d "publish"

# 启动logstash
bin/logstash -f config/first-pipeline.conf --config.test_and_exit
bin/logstash -f config/first-pipeline.conf --config.reload.automatic

~~~



~~~bash
# $DATE格式为当前时间格式YYYY.MM.DD
curl -XGET 'http://192.168.125.165:9200/logstash-2024.09.04/_search?pretty&q=response=200'

# 最终日志的索引是logs-generic-default且是数据流类型
# 日志中有37行数据，最终写入到ES的只有36，是因为最后一行日志没有换行，所以没有解析这一行日志。

~~~

![image-20240904131936628](http://47.101.155.205/image-20240904131936628.png)

![image-20240904133056364](http://47.101.155.205/image-20240904133056364.png)

![image-20240904143710490](http://47.101.155.205/image-20240904143710490.png)



#### 配置ES的index名称

~~~first.conf
input {
    beats {
        port => "5044"
    }
}
 filter {
    grok {
        match => { "message" => "%{COMBINEDAPACHELOG}"}
    }
    geoip {
        source => "[source][address]"
        target => "geoip"
        database => "/u01/lgsa/local/GeoLite2-City_20240830/GeoLite2-City.mmdb"
    }
}
output {
    elasticsearch {
		hosts => ["http://192.168.125.165:9200"]
		index => "log-nginx-%{+YYYY-MM-DD}" # 到日格式会有问题,%{+YYYY-MM}不会有问题
	}
}

~~~

生成的索引名称不对。

~~~bash
./filebeat -e -c filebeat_test.yml -d "publish"

~~~

![image-20240904153337923](http://47.101.155.205/image-20240904153337923.png)



#### json(input)toEs

~~~first-pipeline.conf
input {
    stdin {}
}

output {
    elasticsearch {
        hosts => ["http://192.168.125.165:9200"]
        index => "log-nginx-%{+YYYY-MM}"
    }
}

~~~

字段太长，不是符合规范的json，调整成读取文件(读取文件之后会有记录，更改文件名)，最终的字段结构和错误json一致。解析失败，显示表中会有字段tags=_jsonparsefailure。

~~~first-pipeline.conf
input {
    file {
    	path => "/u01/lgsa/local/logstash-8.15.0/json/gateway1.json"
    	start_position => "beginning"
  	}
}
output {
    elasticsearch {
        hosts => ["http://192.168.125.165:9200"]
        index => "log-nginx-%{+YYYY-MM}"
    }
}

~~~



~~~bsah
bin/logstash -f config/first-pipeline.conf --config.test_and_exit
bin/logstash -f config/first-pipeline.conf --config.reload.automatic

~~~



~~~json
{"afterReturningResult":"{\"status\":\"1\",\"note\":null,\"description\":null,\"map\":{\"data\":{\"content\":[{\"id\":867331,\"orgId\":104370,\"whRecPreentNo\":\"104370WH240826017119\",\"inExpWhNo\":\"PRE20230719E00201\",\"inExpType\":\"7\",\"bizopEtpsno\":\"3122S66009\",\"bizopEtpsNm\":\"上海语霆国际贸易股份有限公司\",\"bizopEtpsSccd\":\"91310115MA1K47Y39X\",\"businessTypecd\":\"25\",\"inExpTime\":null,\"mtpckEndprdTypecd\":\"I\",\"supvModecd\":\"0110\",\"trspModecd\":\"0\",\"impexpPortcd\":\"2249\",\"masterCuscd\":\"2249\",\"rltEntryNo\":null,\"rltWhRecNo\":null,\"whRecNo\":null,\"dclTime\":null,\"dclcusFlag\":\"2\",\"dclcusTypecd\":\"2\",\"preRecAccountTime\":null,\"recAccountTime\":null,\"invtIochkptStucd\":\"1\",\"vrfdedMarkcd\":\"0\",\"rmk\":\"/工单/11\",\"entryStucd\":null,\"optStatus\":\"1\",\"destinationNatcd\":\"142\",\"tradeCountry\":\"142\",\"tradingRegion\":\"142\",\"districtCode\":\"31166\",\"grossWt\":\"1000\",\"transMode\":\"1\",\"ieTypecd\":\"E\",\"passportUsedTypecd\":\"1\",\"relDclcusFlag\":\"1\",\"inexpRltType\":null,\"grtNo\":\"2\",\"isDrag\":\"1\",\"dclTypecd\":\"2\",\"chgTmsCnt\":\"0\",\"specialBusiness\":null,\"allocationMode\":null,\"enterNumber\":\"1\",\"releasePassDate\":null,\"whpassportUsedTypecd\":\"1\",\"entryRealeaseDate\":null,\"checkBondInvtNo\":null,\"area\":\"L\",\"declareCustomeName\":null,\"declareTradeCode\":null,\"declareNumber\":null,\"declareStatus\":null,\"declareFlag\":null,\"declareEdiNo\":null,\"endStatus\":null,\"endResult\":null,\"changeType\":\"1\",\"productCharCode\":\"0\",\"passTime\":null,\"receiveGoodsTime\":null,\"whSeqNo\":\"49E240800004879606\"},{\"id\":867257,\"orgId\":104370,\"whRecPreentNo\":\"104370WH230628015583\",\"inExpWhNo\":\"PRE20230506I0003\",\"inExpType\":\"1\",\"bizopEtpsno\":\"3122S66009\",\"bizopEtpsNm\":\"上海语霆国际贸易有限公司\",\"bizopEtpsSccd\":\"91310115MA1K47Y39X\",\"businessTypecd\":\"4\",\"inExpTime\":null,\"mtpckEndprdTypecd\":\"I\",\"supvModecd\":\"0110\",\"trspModecd\":\"1\",\"impexpPortcd\":\"2249\",\"masterCuscd\":\"2249\",\"rltEntryNo\":null,\"rltWhRecNo\":null,\"whRecNo\":null,\"dclTime\":\"2023-06-28 15:48:58\",\"dclcusFlag\":\"2\",\"dclcusTypecd\":\"2\",\"preRecAccountTime\":null,\"recAccountTime\":null,\"invtIochkptStucd\":\"1\",\"vrfdedMarkcd\":\"0\",\"rmk\":\"备注字段-添加备注\",\"entryStucd\":null,\"optStatus\":\"17\",\"destinationNatcd\":\"142\",\"tradeCountry\":\"142\",\"tradingRegion\":\"142\",\"districtCode\":\"31166\",\"grossWt\":\"1000\",\"transMode\":\"1\",\"ieTypecd\":\"I\",\"passportUsedTypecd\":\"1\",\"relDclcusFlag\":\"2\",\"inexpRltType\":null,\"grtNo\":\"0\",\"isDrag\":\"0\",\"dclTypecd\":\"3\",\"chgTmsCnt\":\"0\",\"specialBusiness\":null,\"allocationMode\":null,\"enterNumber\":null,\"releasePassDate\":null,\"whpassportUsedTypecd\":\"1\",\"entryRealeaseDate\":\"2023-06-28 15:54:17\",\"checkBondInvtNo\":null,\"area\":\"L\",\"declareCustomeName\":null,\"declareTradeCode\":null,\"declareNumber\":null,\"declareStatus\":null,\"declareFlag\":null,\"declareEdiNo\":null,\"endStatus\":null,\"endResult\":null,\"changeType\":null,\"productCharCode\":\"0\",\"passTime\":null,\"receiveGoodsTime\":null,\"whSeqNo\":null},{\"id\":867247,\"orgId\":104370,\"whRecPreentNo\":\"\",\"inExpWhNo\":\"PRE20230719E00204\",\"inExpType\":\"1\",\"bizopEtpsno\":\"3122S66009\",\"bizopEtpsNm\":\"上海语霆国际贸易股份有限公司\",\"bizopEtpsSccd\":\"91310115MA1K47Y39X\",\"businessTypecd\":\"4\",\"inExpTime\":null,\"mtpckEndprdTypecd\":\"I\",\"supvModecd\":\"0110\",\"trspModecd\":\"1\",\"impexpPortcd\":\"2249\",\"masterCuscd\":\"2249\",\"rltEntryNo\":null,\"rltWhRecNo\":null,\"whRecNo\":null,\"dclTime\":null,\"dclcusFlag\":\"2\",\"dclcusTypecd\":\"2\",\"preRecAccountTime\":null,\"recAccountTime\":null,\"invtIochkptStucd\":\"1\",\"vrfdedMarkcd\":\"0\",\"rmk\":\"备注字段-添加备注\",\"entryStucd\":null,\"optStatus\":\"99\",\"destinationNatcd\":\"142\",\"tradeCountry\":\"142\",\"tradingRegion\":\"142\",\"districtCode\":\"31166\",\"grossWt\":\"1000\",\"transMode\":\"1\",\"ieTypecd\":\"E\",\"passportUsedTypecd\":\"1\",\"relDclcusFlag\":\"1\",\"inexpRltType\":null,\"grtNo\":\"1\",\"isDrag\":\"1\",\"dclTypecd\":\"1\",\"chgTmsCnt\":\"0\",\"specialBusiness\":null,\"allocationMode\":null,\"enterNumber\":null,\"releasePassDate\":null,\"whpassportUsedTypecd\":\"1\",\"entryRealeaseDate\":null,\"checkBondInvtNo\":null,\"area\":\"L\",\"declareCustomeName\":null,\"declareTradeCode\":null,\"declareNumber\":null,\"declareStatus\":null,\"declareFlag\":null,\"declareEdiNo\":null,\"endStatus\":null,\"endResult\":null,\"changeType\":null,\"productCharCode\":null,\"passTime\":null,\"receiveGoodsTime\":null,\"whSeqNo\":\"49E240800004879699\"},{\"id\":867121,\"orgId\":104370,\"whRecPreentNo\":\"\",\"inExpWhNo\":\"PRE20230719E00203\",\"inExpType\":\"2\",\"bizopEtpsno\":\"3122S66009\",\"bizopEtpsNm\":\"上海语霆国际贸易股份有限公司\",\"bizopEtpsSccd\":\"91310115MA1K47Y39X\",\"businessTypecd\":\"4\",\"inExpTime\":null,\"mtpckEndprdTypecd\":\"I\",\"supvModecd\":\"0110\",\"trspModecd\":\"0\",\"impexpPortcd\":\"2249\",\"masterCuscd\":\"2249\",\"rltEntryNo\":null,\"rltWhRecNo\":null,\"whRecNo\":null,\"dclTime\":null,\"dclcusFlag\":\"2\",\"dclcusTypecd\":\"1\",\"preRecAccountTime\":null,\"recAccountTime\":null,\"invtIochkptStucd\":\"1\",\"vrfdedMarkcd\":\"0\",\"rmk\":\"/1/\",\"entryStucd\":null,\"optStatus\":\"99\",\"destinationNatcd\":\"142\",\"tradeCountry\":\"142\",\"tradingRegion\":\"142\",\"districtCode\":\"31166\",\"grossWt\":\"1000\",\"transMode\":\"1\",\"ieTypecd\":\"I\",\"passportUsedTypecd\":\"1\",\"relDclcusFlag\":\"1\",\"inexpRltType\":null,\"grtNo\":\"1\",\"isDrag\":\"1\",\"dclTypecd\":\"1\",\"chgTmsCnt\":\"0\",\"specialBusiness\":null,\"allocationMode\":null,\"enterNumber\":null,\"releasePassDate\":null,\"whpassportUsedTypecd\":\"1\",\"entryRealeaseDate\":null,\"checkBondInvtNo\":null,\"area\":\"L\",\"declareCustomeName\":null,\"declareTradeCode\":null,\"declareNumber\":null,\"declareStatus\":null,\"declareFlag\":null,\"declareEdiNo\":null,\"endStatus\":null,\"endResult\":null,\"changeType\":null,\"productCharCode\":null,\"passTime\":null,\"receiveGoodsTime\":null,\"whSeqNo\":\"49I240800004879608\"},{\"id\":867119,\"orgId\":104370,\"whRecPreentNo\":\"104370WH240826017119\",\"inExpWhNo\":\"PRE20230719E00201\",\"inExpType\":\"7\",\"bizopEtpsno\":\"3122S66009\",\"bizopEtpsNm\":\"上海语霆国际贸易股份有限公司\",\"bizopEtpsSccd\":\"91310115MA1K47Y39X\",\"businessTypecd\":\"25\",\"inExpTime\":null,\"mtpckEndprdTypecd\":\"I\",\"supvModecd\":\"0110\",\"trspModecd\":\"0\",\"impexpPortcd\":\"2249\",\"masterCuscd\":\"2249\",\"rltEntryNo\":null,\"rltWhRecNo\":null,\"whRecNo\":null,\"dclTime\":null,\"dclcusFlag\":\"2\",\"dclcusTypecd\":\"2\",\"preRecAccountTime\":null,\"recAccountTime\":null,\"invtIochkptStucd\":\"1\",\"vrfdedMarkcd\":\"0\",\"rmk\":\"/工单/11\",\"entryStucd\":null,\"optStatus\":\"8\",\"destinationNatcd\":\"142\",\"tradeCountry\":\"142\",\"tradingRegion\":\"142\",\"districtCode\":\"31166\",\"grossWt\":\"1000\",\"transMode\":\"1\",\"ieTypecd\":\"E\",\"passportUsedTypecd\":\"1\",\"relDclcusFlag\":\"1\",\"inexpRltType\":null,\"grtNo\":\"2\",\"isDrag\":\"1\",\"dclTypecd\":\"1\",\"chgTmsCnt\":\"0\",\"specialBusiness\":null,\"allocationMode\":null,\"enterNumber\":\"1\",\"releasePassDate\":null,\"whpassportUsedTypecd\":\"1\",\"entryRealeaseDate\":null,\"checkBondInvtNo\":null,\"area\":\"L\",\"declareCustomeName\":null,\"declareTradeCode\":null,\"declareNumber\":null,\"declareStatus\":null,\"declareFlag\":null,\"declareEdiNo\":null,\"endStatus\":null,\"endResult\":null,\"changeType\":null,\"productCharCode\":\"0\",\"passTime\":null,\"receiveGoodsTime\":null,\"whSeqNo\":\"49E240800004879606\"},{\"id\":867074,\"orgId\":104370,\"whRecPreentNo\":\"\",\"inExpWhNo\":\"PRE20230720E00404\",\"inExpType\":\"4\",\"bizopEtpsno\":\"3122S66009\",\"bizopEtpsNm\":\"上海语霆国际贸易股份有限公司\",\"bizopEtpsSccd\":\"91310115MA1K47Y39X\",\"businessTypecd\":\"26\",\"inExpTime\":null,\"mtpckEndprdTypecd\":\"I\",\"supvModecd\":\"0110\",\"trspModecd\":\"0\",\"impexpPortcd\":\"2249\",\"masterCuscd\":\"2249\",\"rltEntryNo\":null,\"rltWhRecNo\":\"1\",\"whRecNo\":null,\"dclTime\":null,\"dclcusFlag\":\"1\",\"dclcusTypecd\":null,\"preRecAccountTime\":null,\"recAccountTime\":null,\"invtIochkptStucd\":\"1\",\"vrfdedMarkcd\":\"0\",\"rmk\":\"备注字段-添加备注\",\"entryStucd\":null,\"optStatus\":\"1\",\"destinationNatcd\":\"142\",\"tradeCountry\":\"142\",\"tradingRegion\":\"142\",\"districtCode\":\"31166\",\"grossWt\":\"1000\",\"transMode\":\"1\",\"ieTypecd\":\"E\",\"passportUsedTypecd\":\"1\",\"relDclcusFlag\":\"1\",\"inexpRltType\":null,\"grtNo\":\"1\",\"isDrag\":\"1\",\"dclTypecd\":\"1\",\"chgTmsCnt\":\"0\",\"specialBusiness\":null,\"allocationMode\":\"1\",\"enterNumber\":null,\"releasePassDate\":null,\"whpassportUsedTypecd\":\"1\",\"entryRealeaseDate\":null,\"checkBondInvtNo\":null,\"area\":\"L\",\"declareCustomeName\":null,\"declareTradeCode\":null,\"declareNumber\":null,\"declareStatus\":null,\"declareFlag\":null,\"declareEdiNo\":null,\"endStatus\":null,\"endResult\":null,\"changeType\":null,\"productCharCode\":null,\"passTime\":null,\"receiveGoodsTime\":null,\"whSeqNo\":\"49E240800004879569\"},{\"id\":867067,\"orgId\":104370,\"whRecPreentNo\":\"104370WH240821017067\",\"inExpWhNo\":\"PRE20230720E00402\",\"inExpType\":\"7\",\"bizopEtpsno\":\"3122S66009\",\"bizopEtpsNm\":\"上海语霆国际贸易股份有限公司\",\"bizopEtpsSccd\":\"91310115MA1K47Y39X\",\"businessTypecd\":\"25\",\"inExpTime\":null,\"mtpckEndprdTypecd\":\"I\",\"supvModecd\":\"0110\",\"trspModecd\":\"0\",\"impexpPortcd\":\"2249\",\"masterCuscd\":\"2249\",\"rltEntryNo\":null,\"rltWhRecNo\":null,\"whRecNo\":null,\"dclTime\":\"2024-08-21 10:54:12\",\"dclcusFlag\":\"2\",\"dclcusTypecd\":\"2\",\"preRecAccountTime\":null,\"recAccountTime\":null,\"invtIochkptStucd\":\"1\",\"vrfdedMarkcd\":\"0\",\"rmk\":\"备注字段-添加备注\",\"entryStucd\":null,\"optStatus\":\"17\",\"destinationNatcd\":\"142\",\"tradeCountry\":\"142\",\"tradingRegion\":\"142\",\"districtCode\":\"31166\",\"grossWt\":\"1000\",\"transMode\":\"1\",\"ieTypecd\":\"E\",\"passportUsedTypecd\":\"1\",\"relDclcusFlag\":\"1\",\"inexpRltType\":null,\"grtNo\":\"1\",\"isDrag\":\"1\",\"dclTypecd\":\"1\",\"chgTmsCnt\":\"0\",\"specialBusiness\":null,\"allocationMode\":null,\"enterNumber\":null,\"releasePassDate\":null,\"whpassportUsedTypecd\":\"1\",\"entryRealeaseDate\":null,\"checkBondInvtNo\":null,\"area\":\"L\",\"declareCustomeName\":null,\"declareTradeCode\":null,\"declareNumber\":null,\"declareStatus\":null,\"declareFlag\":null,\"declareEdiNo\":null,\"endStatus\":null,\"endResult\":null,\"changeType\":null,\"productCharCode\":\"0\",\"passTime\":null,\"receiveGoodsTime\":null,\"whSeqNo\":\"49E240800004879562\"},{\"id\":867045,\"orgId\":104370,\"whRecPreentNo\":\"104370WH240820017045\",\"inExpWhNo\":\"PRE20230720E00401\",\"inExpType\":\"5\",\"bizopEtpsno\":\"3122S66009\",\"bizopEtpsNm\":\"上海语霆国际贸易股份有限公司\",\"bizopEtpsSccd\":\"91310115MA1K47Y39X\",\"businessTypecd\":\"27\",\"inExpTime\":null,\"mtpckEndprdTypecd\":\"I\",\"supvModecd\":\"0615\",\"trspModecd\":\"0\",\"impexpPortcd\":\"2249\",\"masterCuscd\":\"2249\",\"rltEntryNo\":null,\"rltWhRecNo\":null,\"whRecNo\":null,\"dclTime\":\"2024-08-20 16:40:35\",\"dclcusFlag\":\"2\",\"dclcusTypecd\":\"1\",\"preRecAccountTime\":null,\"recAccountTime\":null,\"invtIochkptStucd\":\"1\",\"vrfdedMarkcd\":\"1\",\"rmk\":\"备注字段-添加备注\",\"entryStucd\":null,\"optStatus\":\"5\",\"destinationNatcd\":\"142\",\"tradeCountry\":\"142\",\"tradingRegion\":\"142\",\"districtCode\":\"31166\",\"grossWt\":\"1000\",\"transMode\":\"1\",\"ieTypecd\":\"E\",\"passportUsedTypecd\":\"3\",\"relDclcusFlag\":\"2\",\"inexpRltType\":null,\"grtNo\":\"1\",\"isDrag\":\"1\",\"dclTypecd\":\"1\",\"chgTmsCnt\":\"0\",\"specialBusiness\":null,\"allocationMode\":null,\"enterNumber\":null,\"releasePassDate\":null,\"whpassportUsedTypecd\":\"1\",\"entryRealeaseDate\":\"2024-08-20 16:41:24\",\"checkBondInvtNo\":null,\"area\":\"L\",\"declareCustomeName\":null,\"declareTradeCode\":null,\"declareNumber\":null,\"declareStatus\":null,\"declareFlag\":null,\"declareEdiNo\":null,\"endStatus\":null,\"endResult\":null,\"changeType\":null,\"productCharCode\":\"0\",\"passTime\":null,\"receiveGoodsTime\":null,\"whSeqNo\":\"49E240800004879559\"},{\"id\":867044,\"orgId\":104370,\"whRecPreentNo\":\"104370WH240820017044\",\"inExpWhNo\":\"PRE20230720E00401\",\"inExpType\":\"7\",\"bizopEtpsno\":\"3122S66009\",\"bizopEtpsNm\":\"上海语霆国际贸易股份有限公司\",\"bizopEtpsSccd\":\"91310115MA1K47Y39X\",\"businessTypecd\":\"25\",\"inExpTime\":null,\"mtpckEndprdTypecd\":\"I\",\"supvModecd\":\"0110\",\"trspModecd\":\"0\",\"impexpPortcd\":\"2249\",\"masterCuscd\":\"2249\",\"rltEntryNo\":null,\"rltWhRecNo\":null,\"whRecNo\":null,\"dclTime\":\"2024-08-20 16:40:35\",\"dclcusFlag\":\"2\",\"dclcusTypecd\":\"2\",\"preRecAccountTime\":null,\"recAccountTime\":null,\"invtIochkptStucd\":\"1\",\"vrfdedMarkcd\":\"1\",\"rmk\":\"备注字段-添加备注\",\"entryStucd\":null,\"optStatus\":\"5\",\"destinationNatcd\":\"142\",\"tradeCountry\":\"142\",\"tradingRegion\":\"142\",\"districtCode\":\"31166\",\"grossWt\":\"1000\",\"transMode\":\"1\",\"ieTypecd\":\"E\",\"passportUsedTypecd\":\"3\",\"relDclcusFlag\":\"2\",\"inexpRltType\":null,\"grtNo\":\"1\",\"isDrag\":\"1\",\"dclTypecd\":\"1\",\"chgTmsCnt\":\"0\",\"specialBusiness\":null,\"allocationMode\":null,\"enterNumber\":null,\"releasePassDate\":null,\"whpassportUsedTypecd\":\"1\",\"entryRealeaseDate\":\"2024-08-20 16:41:18\",\"checkBondInvtNo\":null,\"area\":\"L\",\"declareCustomeName\":null,\"declareTradeCode\":null,\"declareNumber\":null,\"declareStatus\":null,\"declareFlag\":null,\"declareEdiNo\":null,\"endStatus\":null,\"endResult\":null,\"changeType\":null,\"productCharCode\":\"0\",\"passTime\":null,\"receiveGoodsTime\":null,\"whSeqNo\":\"49E240800004879558\"},{\"id\":867043,\"orgId\":104370,\"whRecPreentNo\":\"104370WH240820017043\",\"inExpWhNo\":\"PRE20230720E00401\",\"inExpType\":\"1\",\"bizopEtpsno\":\"3122S66009\",\"bizopEtpsNm\":\"上海语霆国际贸易股份有限公司\",\"bizopEtpsSccd\":\"91310115MA1K47Y39X\",\"businessTypecd\":\"28\",\"inExpTime\":null,\"mtpckEndprdTypecd\":\"I\",\"supvModecd\":\"0110\",\"trspModecd\":\"1\",\"impexpPortcd\":\"2249\",\"masterCuscd\":\"2249\",\"rltEntryNo\":null,\"rltWhRecNo\":null,\"whRecNo\":null,\"dclTime\":\"2024-08-20 16:40:35\",\"dclcusFlag\":\"2\",\"dclcusTypecd\":\"2\",\"preRecAccountTime\":null,\"recAccountTime\":null,\"invtIochkptStucd\":\"1\",\"vrfdedMarkcd\":\"1\",\"rmk\":\"一纳成品出境\",\"entryStucd\":null,\"optStatus\":\"5\",\"destinationNatcd\":\"142\",\"tradeCountry\":\"142\",\"tradingRegion\":\"142\",\"districtCode\":\"31166\",\"grossWt\":\"1000\",\"transMode\":\"1\",\"ieTypecd\":\"E\",\"passportUsedTypecd\":\"3\",\"relDclcusFlag\":\"2\",\"inexpRltType\":null,\"grtNo\":\"1\",\"isDrag\":\"1\",\"dclTypecd\":\"1\",\"chgTmsCnt\":\"0\",\"specialBusiness\":null,\"allocationMode\":null,\"enterNumber\":null,\"releasePassDate\":null,\"whpassportUsedTypecd\":\"1\",\"entryRealeaseDate\":\"2024-08-20 16:41:03\",\"checkBondInvtNo\":null,\"area\":\"L\",\"declareCustomeName\":null,\"declareTradeCode\":null,\"declareNumber\":null,\"declareStatus\":null,\"declareFlag\":null,\"declareEdiNo\":null,\"endStatus\":null,\"endResult\":null,\"changeType\":null,\"productCharCode\":\"0\",\"passTime\":null,\"receiveGoodsTime\":null,\"whSeqNo\":\"49E240800004879557\"}],\"pageable\":{\"sort\":{\"sorted\":false,\"unsorted\":true,\"empty\":true},\"pageSize\":10,\"pageNumber\":0,\"offset\":0,\"unpaged\":false,\"paged\":true},\"totalElements\":20,\"last\":false,\"totalPages\":2,\"first\":true,\"sort\":{\"sorted\":false,\"unsorted\":true,\"empty\":true},\"numberOfElements\":10,\"size\":10,\"number\":0,\"empty\":false}}}","businessFlag":null,"contentType":"application/json","contextPath":"","cookies":"{\"BDSVRTM\":[{\"name\":\"BDSVRTM\",\"value\":\"3\"}],\"BD_HOME\":[{\"name\":\"BD_HOME\",\"value\":\"1\"}]}","exceptionMsg":null,"fullUrl":"http://192.168.8.8:9002/wareHouse/getList","metadata":{"afterReturningResultLength":13508,"appCode":"lgsa-wareHouse-mgr","cmProjectCode":"","gatewayClusterName":"cluster-api-gateway-lgsa-test-gcc","gatewayLocalIpAddress":"[\"192.168.8.8\"]","gatewayRequestHost":"192.168.116.12","gatewayRequestUrl":"http://192.168.116.12:9101/wareHouse/getList","gatewaySendTime":"2024-09-04T07:49:26.545Z","host":"192.168.8.8","responseContentType":"application/json;charset=UTF-8","responseHttpStatusCode":200,"routeId":"CompositeDiscoveryClient_wareHouse-mgr","routeToString":"Route{id='CompositeDiscoveryClient_wareHouse-mgr', uri=lb://lgsa-wareHouse-mgr, order=0, predicate=Paths: [/wareHouse/**], match trailing slash: true, gatewayFilters=[[[EpOauthAuthority clientId = 'XPQ_XTJG', customClientIdSwitch = 'false', customClientIdHeader = 'Custom-Ep-Oauth-Client-Id', whiteList = '', validateList = '', permitSwitch = 'false', permitWhiteList = [null], userAuthCodeKey = 'userAuthCode,appId,auth_code,Gw-App-Id,Gw-Authorization-Ac,Fip-Authorization-Ac', refreshTokenKey = 'refresh_token,epToken,Gw-Authorization-Rt', accessTokenKey = 'access_token,Gw-Authorization-At', userInfoInjectSwitch = 'true', whiteListInject = '/v*/outside/ep-oauth/**,/v*/captcha/**', permitWhiteListInject = '/v*/inside/ep-oauth/**', diyErrorInfoTD = [null], diyErrorInfoTE = [null], diyErrorInfoPNM = [null], orgExtApplyKey = [null], orgExtJsonKeyGray = 'gray', userExtApplyKey = [null], userExtJsonKeyGray = 'gray', graySwitch = 'false', userInfoType = 'EpOauthUserInfo'], order = 1], [com.easipass.gateway.plugin.filter.LgsaAuthGatewayFilterFactory$1@4f13d2fd, order = 2]], metadata={response-timeout=100000, appCode=lgsa-wareHouse-mgr, multipart-form-data-white-list=, cm-project-code=}}","uummLoginId":"Ad_001","uummOrgCode":"MA1K47Y39","uummOrgName":"上海语霆国际贸易有限公司"},"proceedMills":3053,"queryString":null,"requestBody":"{\r\n  \"size\": 10,\r\n  \"page\": 1\r\n}","requestHeaders":"{\"Accept\":[\"*/*\"],\"Accept-Encoding\":[\"gzip, deflate, br\"],\"Content-Length\":[\"32\"],\"Content-Type\":[\"application/json\"],\"Cookie\":[\"BDSVRTM=3; BD_HOME=1\"],\"Eptoken\":[\"75b166bf-70d2-42ee-9cf4-fdf0698c6227\"],\"Host\":[\"192.168.8.8:9002\"],\"Postman-Token\":[\"df3b92f2-7600-4190-beb8-9e614fadf5ac\"],\"User-Agent\":[\"PostmanRuntime/7.41.1\"],\"X-B3-SpanId\":[\"0\"],\"X-B3-TraceId\":[\"c0a808081725436163546100132484\"]}","requestMethod":"POST","requestParames":null,"requestUri":"/wareHouse/getList","requestUrl":"http://192.168.8.8:9002/wareHouse/getList","responseHeaders":"{\"Content-Type\":[\"application/json;charset=UTF-8\"],\"Date\":[\"Wed, 04 Sep 2024 07:48:11 GMT\"],\"X-B3-TraceId\":[\"c0a808081725436163546100132484\"],\"transfer-encoding\":[\"chunked\"]}","restBizResultCode":null,"restResultCode":null,"serverIp":"192.168.8.8","servletPath":null,"sessionId":null,"stackTraceMsg":null,"topicProject":"ep-api-gateway","traceId":"c0a808081725436163546100132484","userAgent":"PostmanRuntime/7.41.1","userIp":null}

~~~

![image-20240904160246632](http://47.101.155.205/image-20240904160246632.png)

![image-20240904163855127](http://47.101.155.205/image-20240904163855127.png)

#### 添加json过滤器

~~~conf
json {
	source => "message" # 要解析的字段,应该是json字符串,通常是message
	target => "msg" # 可选,指定解析后的json数据存储在哪个字段下,没指定直接作为根字段
	remove_field => ["msginfo"] # 可选,移除指定字段,通常是原始的json字符串字段
	add_field => # 可选,解析完成后,添加额外的字段
}

~~~

使用stdin，json的长度超过4096会无法全部输入，切换成file文件输入。

**/logstash-8.15.0/data/plugins/inputs/file目录下隐藏文件删除，logstash会重头开始读取文件。**

解析message的json字段后再移除，json中的key作为json的根，message字段也删除了。

~~~first-pipeline.conf
input {
    file {
    	path => "/u01/lgsa/local/logstash-8.15.0/json/gateway.json"
    	start_position => "beginning"
  	}
}
filter {
	json {
		source => "message"
		remove_field => ["message"]
	}
}
output {
    elasticsearch {
        hosts => ["http://192.168.125.165:9200"]
        index => "log-nginx-%{+YYYY-MM}"
    }
}

~~~



~~~bash
bin/logstash -f config/first-pipeline.conf --config.test_and_exit
bin/logstash -f config/first-pipeline.conf --config.reload.automatic

~~~

![image-20240904162555758](http://47.101.155.205/image-20240904162555758.png)

![image-20240904162603107](http://47.101.155.205/image-20240904162603107.png)

![image-20240904162658884](http://47.101.155.205/image-20240904162658884.png)



#### json添加target效果

~~~first-pipeline.conf
input {
    file {
    	path => "/u01/lgsa/local/logstash-8.15.0/json/gateway2.json"
    	start_position => "beginning"
  	}
}
filter {
	json {
		source => "message"
		remove_field => ["message"]
		target => "kakfa"
	}
}
output {
    elasticsearch {
        hosts => ["http://192.168.125.165:9200"]
        index => "log-nginx-%{+YYYY-MM}"
    }
}

~~~

json对象有一个key为target的值。

![image-20240904164419543](http://47.101.155.205/image-20240904164419543.png)

![image-20240904164508813](http://47.101.155.205/image-20240904164508813.png)



#### grok搭配json使用

kibana有grok的表达式调试器

![image-20240905090837128](http://47.101.155.205/image-20240905090837128.png)



~~~first-pipeline.conf
input {
    file {
    	path => "/u01/lgsa/local/logstash-8.15.0/json/gateway6.json"
    	start_position => "beginning"
  	}
}
filter {
	grok {
	match => ["message", "(?<time>%{YEAR}[-]%{MONTHNUM}[-]%{MONTHDAY} %{TIME}) %{NOTSPACE:level} \[%{NOTSPACE:threadId}\] \[%{NOTSPACE:threadName}\] \[%{NOTSPACE:class}\]\s+\-+\s+%{GREEDYDATA:msginfo}"]
}
json {	
	source => "msginfo"
	remove_field=>["msginfo"]
}
}
output {
    elasticsearch {
        hosts => ["http://192.168.125.165:9200"]
        index => "log-nginx-%{+YYYY-MM}"
    }
}

~~~

~~~log
2024-09-04 12:34:56 INFO [12345] [pool-1-thread-1] [MyClass] - json字符串

~~~



~~~bash
bin/logstash -f config/first-pipeline.conf --config.test_and_exit
bin/logstash -f config/first-pipeline.conf --config.reload.automatic

~~~

![image-20240904172442698](http://47.101.155.205/image-20240904172442698.png)





### 解析特定格式log日志

日志格式，[%-5level]会在INOF后面补空格，导致[INFO ]的日志解析失败。

~~~xml
<pattern>%d{HH:mm:ss.SSS}[%X{traceId},%X{spanId}] [%-5level] [%thread] %logger{36} - %msg%n</pattern>

~~~

grok表达式，\\[%{LOGLEVEL:level}\\]需要对空格再处理

~~~conf
%{TIME:time}\[%{DATA:traceId},%{DATA:spanId}\] \[%{LOGLEVEL:level}\s*\] \[%{DATA:threadName}\] %{JAVACLASS:class} - %{GREEDYDATA:msg}

~~~



~~~first-pipeline.conf
input {
    file {
    	path => "/u01/lgsa/local/logstash-8.15.0/logFile.2024-09-05.log"
    	start_position => "beginning"
  	}
}
filter {
	grok {
	match => { "message" => "%{TIME:time}\[%{DATA:traceId},%{DATA:spanId}\] \[%{LOGLEVEL:level}\s*\] \[%{DATA:threadName}\] %{JAVACLASS:class} - %{GREEDYDATA:msg}" }
	}
	mutate  {
		# 添加属性
		add_field => { "current_date" => "%{+YYYY-MM-dd}" }
		# 删除属性
		remove_field => [ "message" ]
	}
}
output {
    elasticsearch {
        hosts => ["http://192.168.125.165:9200"]
        index => "log-nginx-%{+YYYY-MM}"
    }
}

~~~





![image-20240905161235538](http://47.101.155.205/image-20240905161235538.png)

![image-20240905163420840](http://47.101.155.205/image-20240905163420840.png)

解析效果

![image-20240905165333482](http://47.101.155.205/image-20240905165333482.png)

logstash.yml中编辑配置，强制保持事件(message消息)的顺序性质

![image-20240905165825560](http://47.101.155.205/image-20240905165825560.png)

**pipeline.ordered: auto修改为true**

![image-20240905170326388](http://47.101.155.205/image-20240905170326388.png)



#### 日志格式

~~~conf
input {
    file {
    	path => "/u01/lgsa/local/logstash-8.15.0/eg-info.2024-09-09.log"
    	start_position => "beginning"
  	}
}
filter {
	grok {
		match => { "message" => "%{TIME:time} \[%{LOGLEVEL:level}\s*\] \[%{DATA:threadName}\] %{JAVACLASS:class} - %{GREEDYDATA:msg}" }
	}
	
	if "_grokparsefailure" in [tags] {
    	drop { }
  	}
	mutate  {
		# 添加属性
		add_field => { "current_date" => "%{+YYYY-MM-dd}" }
		# 删除属性
		remove_field => [ "message", "hostName", "appType", "logid"]
	}
	json {	
		source => "msg"
		remove_field=>["msg"]
	}
}
output {
    elasticsearch {
        hosts => ["http://192.168.125.165:9200"]
        index => "log-nginx-%{+YYYY-MM}"
    }
}

~~~



~~~bash
bin/logstash -f config/first-pipeline.conf --config.test_and_exit
bin/logstash -f config/first-pipeline.conf --config.reload.automatic

~~~

![image-20240911103957579](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20240911103957579.png)

~~~yml
# 这里设置前置顺序执行，需要pipeline.workers改为1，默认应该是2启动的，有冲突
pipeline.ordered: true
pipeline.workers: 1

~~~

![image-20240911110322473](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20240911110322473.png)