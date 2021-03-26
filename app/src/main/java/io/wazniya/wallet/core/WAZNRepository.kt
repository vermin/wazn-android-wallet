package io.wazniya.wallet.core

import android.app.Application
import io.wazniya.wallet.App
import io.wazniya.wallet.data.AppDatabase
import io.wazniya.wallet.data.entity.Asset
import io.wazniya.wallet.data.entity.Node
import io.wazniya.wallet.data.entity.Wallet
import io.wazniya.wallet.support.ZH_CN
import io.wazniya.wallet.support.extensions.getCurrentLocale
import io.wazniya.wallet.support.nodeArray
import java.io.File

class WAZNRepository(val context: Application = App.instance) {

    fun getKeysFilePath(name: String): String {
        return "${context.filesDir.absolutePath}${File.separator}wallet${File.separator}wazn${File.separator}$name${File.separator}$name.keys"
    }

    fun getWalletFilePath(name: String): String {
        return "${context.filesDir.absolutePath}${File.separator}wallet${File.separator}wazn${File.separator}$name${File.separator}$name"
    }

    private fun generateWAZNFile(name: String): File {
        val dir = "${context.filesDir.absolutePath}${File.separator}wallet${File.separator}wazn${File.separator}$name"
        val walletFolder = File(dir)
        if (!walletFolder.exists()) {
            walletFolder.mkdirs()
        }
        val cacheFile = File(dir, name)
        val keysFile = File(walletFolder, "$name.keys")
        val addressFile = File(walletFolder, "$name..address.txt")
        if (cacheFile.exists() || keysFile.exists() || addressFile.exists()) {
            throw RuntimeException("Some wallet files already exist for $dir")
        }
        return cacheFile
    }

    fun createWallet(walletName: String, password: String): Wallet? {
        return WAZNWalletController.createWallet(generateWAZNFile(walletName), password)
    }

    fun recoveryWallet(walletName: String, password: String, mnemonic: String, restoreHeight: Long?): Wallet? {
        return WAZNWalletController.recoveryWallet(
            generateWAZNFile(walletName), password, mnemonic, restoreHeight
                ?: 0
        )
    }

    fun createWalletWithKeys(
        walletName: String, password: String, address: String, viewKey: String,
        spendKey: String, restoreHeight: Long?
    ): Wallet? {
        return WAZNWalletController.createWalletWithKeys(
            generateWAZNFile(walletName), password, restoreHeight
                ?: 0, address, viewKey, spendKey
        )
    }

    fun saveWallet(wallet: Wallet): Wallet? {
        val database = AppDatabase.getInstance()

        val actWallets = database.walletDao().getActiveWallets()
        if (!actWallets.isNullOrEmpty()) {
            actWallets.forEach {
                it.isActive = false
            }
            database.walletDao().updateWallets(*actWallets.toTypedArray())
        }
        wallet.isActive = true
        database.walletDao().insertWallet(wallet)
        val insert = database.walletDao().getWalletsByName(wallet.symbol, wallet.name)
        if (insert != null) {
            database.assetDao().insertAsset(Asset(walletId = insert.id, token = insert.symbol))
        }
        return insert
    }

    fun deleteWallet(name: String): Boolean {
        var success = false
        val dir = "${context.filesDir.absolutePath}${File.separator}wallet${File.separator}wazn${File.separator}$name"
        val walletFolder = File(dir)
        if (walletFolder.exists() && walletFolder.isDirectory) {
            success = walletFolder.deleteRecursively()
        }
        return success
    }

    fun insertNodes() {
        // 兼容旧版
        val nodes = AppDatabase.getInstance().nodeDao().getSymbolNodes("WAZN")
        var zhNode: Node? = null
        nodes?.forEach {
            if (it.url == "78.47.140.87:11787") {
                zhNode = it
                return@forEach
            }
        }
        AppDatabase.getInstance().nodeDao().insertNodes(nodes = *nodeArray)
        val node = AppDatabase.getInstance().nodeDao().getSymbolNode("WAZN")
        val locale = context.getCurrentLocale()
        if (locale == ZH_CN && zhNode == null && node != null && node.url == "95.217.185.212:11787") {
            // 兼容旧版，修改中文区默认节点
            val filter = AppDatabase.getInstance().nodeDao().getSymbolNodes("WAZN")?.filter {
                it.url == "168.119.189.236:11787"
            }
            if (!filter.isNullOrEmpty()) {
                AppDatabase.getInstance().nodeDao().updateNodes(
                    node.apply {
                        isSelected = false
                    },
                    filter[0].apply {
                        isSelected = true
                    })
            }
        }
    }

}
