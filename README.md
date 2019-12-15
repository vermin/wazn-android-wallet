# Loki Wallet
Android Wallet for Loki

### QUICKSTART
- Download the APK for the most current release [here](https://github.com/loki-project/loki-android-wallet/releases) and install it
- Run the App and select "Generate Wallet" to create a new wallet or recover a wallet
- Advanced users can copy over synced wallet files (all files) onto sdcard in directory loki-wallet (created first time App is started)
- See the [FAQ](doc/FAQ.md)

## Translations
Help us translate Loki Wallet! 

### Disclaimer
You may lose all your Loki if you use this App. Be cautious when spending on the mainnet.

### Random Notes
- works on the mainnet & stagenet
- use your own daemon - it's easy

### TODO
- see issues on github

### Issues / Pitfalls
- Users of Zenfone MAX & Zenfone 2 Laser (possibly others) **MUST** use the armeabi-v7a APK as the arm64-v8a build uses hardware AES
functionality these models don't have.
- You should backup your wallet files in the "loki-wallet" folder periodically.
- Also note, that on some devices the backups will only be visible on a PC over USB after a reboot of the device (it's an Android bug/feature)

### HOW TO BUILD
See [the instructions](doc/BUILDING-external-libs.md)

Then, fire up Android Studio and build the APK.
