#!/usr/bin/env bash

###############################################################################
# 获取局域网所有主机的 IP-MAC 对应表
# $ sudo ./ip-mac.sh
###############################################################################
echo "$(date '+%Y-%m-%d %H:%M:%S') 开始..."
ts=$(date +%Y%m%dT%H%M)
arp-scan --interface=br0 --localnet | sort -k1 -V > ip-mac-$ts.txt
cat ip-mac-$ts.txt
echo "$(date '+%Y-%m-%d %H:%M:%S') 完成: ip-mac-$ts.txt"
exit 0
