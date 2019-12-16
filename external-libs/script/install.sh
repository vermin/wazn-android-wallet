#!/bin/bash

set -e

packages=(boost openssl monero libsodium)
archs=(arm arm64 x86 x86_64)

for arch in ${archs[@]}; do
    case ${arch} in
        "arm")
			xarch="armeabi-v7a"
			;;
        "arm64")
			xarch="arm64-v8a"
            ;;
        "x86")
			xarch="x86"
            ;;
        "x86_64")
			xarch="x86_64"
            ;;
        *)
			exit 16
            ;;
    esac

	for package in ${packages[@]}; do
    INPUT_DIR=`pwd`/build/build/$package
		OUTPUT_DIR=`pwd`/$package/lib/$xarch
		mkdir -p $OUTPUT_DIR
		rm -f $OUTPUT_DIR/*.a
		cp -a $INPUT_DIR/$arch/lib/*.a $OUTPUT_DIR

		if [ $package = "monero" ]; then
			rm -rf $OUTPUT_DIR/../../include
		  cp -a $INPUT_DIR/include $OUTPUT_DIR/../..
		fi

	done
done

if [[ ! -z $1 ]] ; then
    rm -rf $build_dir
fi

exit 0

