# ln -s /opt/wazn wazn

WAZN_BRANCH=`git -C wazn branch | grep "^\*" | sed 's/^..//'`
WAZNIYA_BRANCH=`git -C . branch | grep "^\*" | sed 's/^..//'`

all: arm64-v8a armeabi-v7a x86_64 x86 include VERSION

arm64-v8a: wazn
	-rm -rf arm64-v8a
	-docker container rm wazn-android-arm64 -f
	docker build -f android64.Dockerfile -t wazn-android-arm64 wazn
	docker create -it --name wazn-android-arm64 wazn-android-arm64 bash
	docker cp wazn-android-arm64:/opt/android/prefix/lib/. arm64-v8a/
	docker cp wazn-android-arm64:/src/build/release/lib/. arm64-v8a//wazn

armeabi-v7a: wazn
	-rm -rf armeabi-v7a
	-docker container rm wazn-android-arm32 -f
	docker build -f android32.Dockerfile -t wazn-android-arm32 wazn
	docker create -it --name wazn-android-arm32 wazn-android-arm32 bash
	docker cp wazn-android-arm32:/opt/android/prefix/lib/. armeabi-v7a
	docker cp wazn-android-arm32:/src/build/release/lib/. armeabi-v7a/wazn

x86_64: wazn
	-rm -rf x86_64
	-docker container rm wazn-android-x86_64 -f
	docker build -f android64_x86.Dockerfile -t wazn-android-x86_64 wazn
	docker create -it --name wazn-android-x86_64 wazn-android-x86_64 bash
	docker cp wazn-android-x86_64:/opt/android/prefix/lib/. x86_64
	docker cp wazn-android-x86_64:/src/build/release/lib/. x86_64/wazn

x86: wazn
	-rm -rf x86
	-docker container rm wazn-android-x86 -f
	docker build -f android32_x86.Dockerfile -t wazn-android-x86 wazn
	docker create -it --name wazn-android-x86 wazn-android-x86 bash
	docker cp wazn-android-x86:/opt/android/prefix/lib/. x86
	docker cp wazn-android-x86:/src/build/release/lib/. x86/wazn

include: wazn
	-mkdir include
	cp wazn/src/wallet/api/wallet2_api.h include/

VERSION:
	echo WAZNIYA `git -C . branch | grep "^\*" | sed 's/^..//'` with WAZN `git -C wazn branch | grep "^\*" | sed 's/^..//'` > VERSION

clean:
	-docker container rm wazn-android-arm64 -f
	-docker container rm wazn-android-arm32 -f
	-docker container rm wazn-android-x86_64 -f
	-docker container rm wazn-android-x86 -f
	-rm -rf arm64-v8a
	-rm -rf armeabi-v7a
	-rm -rf x86_64
	-rm -rf x86
	-rm -rf include
	-rm -f VERSION

distclean: clean
	-docker image rm wazn-android-arm64 -f
	-docker image rm wazn-android-arm32 -f
	-docker image rm wazn-android-x86_64 -f
	-docker image rm wazn-android-x86 -f
	-rm wazn

wazn:
	$(error Please ln -s wazn)

.PHONY: all clean distclean include
