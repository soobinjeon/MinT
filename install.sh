#!/bin/bash
export SNSDKPATH=/root/SnSDK
export SNSDKUSR=$SNSDKPATH/usr
export TARGET_SNSDK=/SnSDK
export TARGET_SNSDKUSR=$TARGET_SNSDK/usr
echo "Make SNSDK Location"
rm -rf /SnSDK
mkdir /SnSDK
mkdir /SnSDK/usr
echo "install java"
tar xzvf $SNSDKUSR/jdk-7u60-linux-arm-vfp-*.gz -C $TARGET_SNSDKUSR
echo "install BBBio"
cp -rf $SNSDKUSR/BBBio $TARGET_SNSDKUSR
echo "set bashrc"
cp -rf ./conf/bashrc /root/.bashrc
