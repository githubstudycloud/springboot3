### qustion1:
指导我开始搭建一个springboot+vue项目，有时还需要加入python，shell，sql，文档管理的目录，git要忽略一些文件，项目可能在idea，vscode，等编辑器进行，现在首先编写忽略文件和基本结构，使用最新的版本构建，给出你的设计，使用promptx工具来指导我们一步步进行

### qustion2:
```text
有时还有docker，k8s的构建管理，并且区分配置和不同环境，java的pom配置我希望以platform-开头，父级为platform-parent，我有构想使用3级结构，二级为各类公共，还有不同的business，3级把二级的小业务模块在分一分，启动器最好写个公共的，然后各类链接组件可以通过配置可控开启，读取gitlab的配置，并且使用springconfig，使用nacos为注册中心，并且还要对接rabbitmq，redis，mong，odb，kafka，mysql等组件，需要一个自己的gateway，虽然外层还套一个nginx，3级的细分，我是否多个小模块公用一个application，我还有所疑惑，比如我4个不同系统采集数据，采集完度量，要能全量，增量并且可选控制小版本采集，采集后，处理数据，并展示看板，还有写配置构建和采集，处理数据一个服务，计算数据1个服务，有的一个系统 的数据单独算，有的需要配合配置组合算，有时只更新一部分算，我该如何在目前基础上调整一下架构呢？
采集我想单独列个collect，数据处理给个fluxcore单独开个
```text

### qustion3:
```text
帮我再整理一下：我还有这些考虑，采集我想单独列个collect，数据处理给个fluxcore单独开个 ，定时任务注册 和执行分开

platform-collect
platform-fluxcore
platform-common
platform-buss-dashboard
platform-monitor-dashboard
platform-gateway
platform-scheduler
platform-scheduler-register
platform-scheduler-query
platform-config
platform-registry

判断自身内存和cpu过高 外部流控


日志与审计系统
消息队列

分布式事务管理器
权限与认证中心

告警
数据存储与缓存
CI\cd容器化
模块通信
网关灰度和回滚
文档和api
```

### qustion4:
```text
我还需要rocketmq，可能还需要各种不同厂商的rocketmq，以及其他等等，还有我需要充分利用设计模式，还有DDD六边形架构，
我目前主要用mysql+xml+mybatis，我不确定要引入mybatis-plus，如果需要，也行，starter是用来管理引入组件的还是什么吗？连接外部组件能采用插入式就好，
还有需要1个专门管理访问外部http接口的服务？还有有时我接别人的rabbitmq和kaka，单纯收发消息，再后面处理，如果隔离和日志安排让整个流程自然好，还有我需要写一些
编码规则，我有excel规则集，我应该如何让AI在编程中注意
```
### qustion5:
```text
那个app后缀改成service，然后把前端vue的架构设计，python和shell的架构设计也搞下，注意，python和sehll有时需要管理项目和单功能脚本，还有sql要有个目录做管理，管理初始脚本，后续迭代变更，还有按天的历史表和存储过程结构归档，还有配置文件，另外配置文件要做加密，用ENCODE()标签标记才解析，并且这个后续允许支持不同的标记，和采用不同加密，并且要是公共配置，做切面，我们的设计和开发计划也要做历史归档，方便人和AI写作
```

### qustion6:
```text
整理一下，把一些不符合我们设计的丢到冗余备份里去或者删除，同时再次检查设计和文档是否有缺漏，我们当前的文档除了readme，最好给个目录啊，不然太散乱了，可以github的好的风格
```

### qustion7:
```text
准备基础的docker单机环境，提供docker-compose和docker单独安装2种，作为测试环境，这个所有组件只装单机
再准备基础的docker单机环境2，提供docker-compose和docker单独安装2种，作为beta环境，这个组件尽量装集群
mysql和nginx可以组集群吗？不能就算了，给出配置和安装以及连接说明文档，并放置好目录以及更新文档
```


### qustion8:
```text
搭建springbootconfig配置服务，连接gitlab，然后如果gitlab不可用，模拟gitlab访问连接本地特定目录，后续给以把目录提交到gitlab或者离线测试处理
配置文件分pub，dev，test后缀用于编写不同环境配置文件，并且分别放3个不同目录，用于后续项目增加，然后每个都要有个common用来管理一些公共配置，
最好能分布式部署，支持docker单机，docker集群，k8s集群，
```

### qustion9:
```text
使用promptx工具来检视我们当前的项目设计，以及文档，代码的编写，发现不足，给出改进建议和优化方案，更新记忆和文档记录，确保我们项目的架构设计符合最新的技术趋势和最佳实践，同时满足我们的业务需求和扩展性要求。
还有给出一个单独的promptx的操作文档，最好和cursor的规则匹配，让cursor可以自动调用promptx功能来逐步和人写作开发项目，最好使用@web搜索网络查看怎么协作这2个工具，为我们后面的任务打下坚实基础
```


### qustion10:
```text
使用promptx，开始下一步行动，并且行动之前请先读取所有必要文档和代码
```

### qustion11:
```text
使用promptx，开始下一步行动，并且行动之前请先读取所有必要文档和代码
```