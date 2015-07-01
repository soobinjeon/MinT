#!/bin/bash
export MINTPATH=/root/MinT
export MINTUSR=$MINTPATH/usr
export TARGET_MINT=/MinT
export TARGET_MINTUSR=$TARGET_MINT/usr
echo "Make MINT Location"
rm -rf /MinT
mkdir /MinT
mkdir /MinT/usr
echo "install java"
tar xzvf $MINTUSR/jdk-7u60-linux-arm-vfp-*.gz -C $TARGET_MINTUSR
echo "install BBBio"
cp -rf $MINTUSR/BBBio $TARGET_MINTUSR
echo "set bashrc"
cp -rf ./conf/bashrc ~/.bashrc
