# 账户中心

## 业务背景

电商业务中，需要给电商app设计一个用户钱包，用户可以往钱包中充值，购买商品时用户可以使用钱包中的钱消费，商品申请退款成功后钱会退回钱包中，用户也可以申请提现把钱提到银行卡中

用程序实现如下api接口

1. 查询用户钱包余额
2. 用户消费100元的接口
3. 用户退款20元接口
4. 查询用户钱包金额变动明细的接口

## 需求

请给出建表语句和对应的代码（只要能实现上面api接口要求即可，不相关的表和代码不用写）

如果对部分场景细节不清，或涉及到外部接口调用部分，可以用伪代码替代。

## 快速使用

### 主机有安装docker，在项目目录下执行 

```shell
# 运行服务
make up_service

# 关闭服务
# make down_service
```

之后访问 localhost:8080 即可

### 主机没有安装docker

主机则需要配置 mysql与redis

#### mysql

1. mysql 监听 localhost:13306，或 修改项目配置
2. 需要执行 doc/data/sql 下的sql

#### redis

1. redis 监听 localhost:16379 并 允许redis免密码登录 或 修改项目配置

### 联合idea调试

#### 主机有安装docker

```shell
# 安装开发环境
make up_env

# 移除开发环境
# make down_env
```

然后使用ide调试

#### 主机没有安装docker

1. 主机需要配置 mysql与redis，可以参考前文
2. 使用ide调试
