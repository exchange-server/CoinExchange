# Ubuntu 16.04 搭建ETH钱包

## 磁盘挂载（如有必要）

https://help.aliyun.com/document_detail/25426.html?spm=a2c4g.11186623.6.610.yemgNN

## 安装以太坊

```
sudo apt-get update
sudo apt-get install software-properties-common
sudo add-apt-repository -y ppa:ethereum/ethereum
sudo apt-get update
sudo apt-get install ethereum
```

## 创建目录启动链条

```
cd /mnt
mkdir eth-chain
nohup geth --cache=1024 --maxpeers 30 --datadir=/mnt/eth-chain --rpc --rpcapi db,eth,net,web3,personal --rpcport 8232 --rpcaddr 0.0.0.0 --rpccorsdomain "*" &
```

## 创建启动脚本

```
vim start_service.sh
```

```
#!/bin/bash
nohup geth --cache=1024 --maxpeers 30 --datadir=/mnt/eth-chain --rpc --rpcapi db,eth,net,web3,personal --rpcport 8232 --rpcaddr 0.0.0.0 --rpccorsdomain "*" &
exit 0
```

```
sudo chmod +x start_service.sh
sudo mv start_service.sh /etc/init.d/
cd /etc/init.d/
sudo update-rc.d start_service.sh defaults 90
```

