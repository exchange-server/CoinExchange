# Ubuntu 16.04 搭建基于omni的USDT钱包

## 磁盘挂载（如有必要）

https://help.aliyun.com/document_detail/25426.html?spm=a2c4g.11186623.6.610.yemgNN

## 下载

```
cd /mnt
wget https://bitcoin.org/bin/bitcoin-core-0.16.1/bitcoin-0.16.1-x86_64-linux-gnu.tar.gz
tar -xzvf bitcoin-0.16.1-x86_64-linux-gnu.tar.gz
```

[其他版本下载](https://bitcoin.org/en/download)

## 配置

```
mkdir bitcoin
cd bitcoin
vim bitcoin.conf
```

添加以下信息（根据实际情况修改）：

```
listen=1
server=1
rpcuser=bitcoinuser
rpcpassword=kmgcB6MKdpXDvcjXeR8nubP9TrRnmtzN
rpcport=7010
rpcallowip=172.21.247.154
```

## 启动

```
nohup /mnt/bitcoin-0.16.1/bin/./bitcoind --datadir=/mnt/bitcoin &
```

## 创建启动脚本

```
vim start_service.sh
```

```
#!/bin/bash
nohup /mnt/bitcoin-0.16.1/bin/./bitcoind --datadir=/mnt/bitcoin --conf=/mnt/bitcoin/bitcoin.conf &
exit 0
```

```
sudo chmod +x start_service.sh
sudo mv start_service.sh /etc/init.d/
cd /etc/init.d/
sudo update-rc.d start_service.sh defaults 90
```

