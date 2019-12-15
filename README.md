WAZNIYA - WAZN Android Wallet
======================

[![License](https://img.shields.io/badge/license-EUPL--1.2-red)](https://opensource.org/licenses/EUPL-1.2)

### Quickstart
- Download the APK for the most current release [here](https://github.com/project-wazn/wazn-android-wallet/releases) and install it
- Run the App and select "Generate Wallet" to create a new wallet or recover a wallet
- Advanced users can copy over synced wallet files (all files) onto SDcard in directory wazn-wallet (created first time App is started)
- See the [FAQ](doc/FAQ.md)

### How to Build
See [the instructions](doc/BUILDING-external-libs.md)
Then, fire up Android Studio and build the APK.

### Issues / Pitfalls
- Users of Zenfone MAX & Zenfone 2 Laser (possibly others) **MUST** use the armeabi-v7a APK as the arm64-v8a build uses hardware AES
functionality these models don't have.
- You should backup your wallet files in the "wazn-wallet" folder periodically.
- Also note, that on some devices the backups will only be visible on a PC over USB after a reboot of the device (it's an Android bug/feature)

### Random Notes
- works on the mainnet & stagenet
- use your own daemon - it's easy

### Disclaimer
You may lose all your WAZN if you use this App. Be cautious when spending on the mainnet.

### License
```
Licensed under the EUPL-1.2
Copyright (c) 2019 WAZN Project  
Copyright (c) 2016-2018 moneroexamples
```
