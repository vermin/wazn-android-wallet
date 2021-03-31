#!/usr/bin/env bash

set -e

source script/env.sh

cd $EXTERNAL_LIBS_BUILD_ROOT

version=1_68_0
dot_version=1.68.0

if [ ! -f "boost_${version}.tar.gz" ]; then
  wget https://dl.bintray.com/boostorg/release/${dot_version}/source/boost_${version}.tar.bz2
fi

if [ ! -d "boost_${version}" ]; then
  tar xvf boost_${version}.tar.bz2
fi
