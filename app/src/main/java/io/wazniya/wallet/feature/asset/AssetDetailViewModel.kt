package io.wazniya.wallet.feature.asset

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import android.content.Intent
import android.util.Log
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseViewModel
import io.wazniya.wallet.core.WAZNRepository
import io.wazniya.wallet.core.WAZNWalletController
import io.wazniya.wallet.data.AppDatabase
import io.wazniya.wallet.data.entity.Asset
import io.wazniya.wallet.data.entity.Node
import io.wazniya.wallet.data.entity.TransactionInfo
import io.wazniya.wallet.data.entity.Wallet
import io.wazniya.wallet.support.REQUEST_SELECT_NODE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AssetDetailViewModel : BaseViewModel() {

    private val repository = WAZNRepository()

    val activeWallet = MutableLiveData<Wallet>()

    val connecting = MutableLiveData<Int>()
    val indeterminate = MutableLiveData<Unit>()
    val synchronizing = MutableLiveData<Long>()
    val synchronizeProgress = MutableLiveData<Int>()
    val synchronizeFailed = MutableLiveData<Int>()
    val synchronized = MutableLiveData<Int>()

    val receiveEnabled = MutableLiveData<Boolean>()
    val sendEnabled = MutableLiveData<Boolean>()

    val allTransfers = MutableLiveData<List<TransactionInfo>>()
    val inTransfers = MutableLiveData<List<TransactionInfo>>()
    val outTransfers = MutableLiveData<List<TransactionInfo>>()

    val showPasswordDialog = MutableLiveData<Boolean>()
    val refreshWallet = MutableLiveData<Boolean>()

    val openSend = MutableLiveData<Boolean>()
    val openReceive = MutableLiveData<Boolean>()

    var password: String? = null
    var activeAsset: Asset? = null
    private var refreshEnabled = false
    private var currentNode: Node? = null

    private val observer = object : WAZNWalletController.Observer {
        var firstBlock = 0L

        override fun onWalletOpened() {
            receiveEnabled.postValue(true)
        }

        override fun onWalletOpenFailed(error: String?) {
            failed()
        }

        override fun onWalletStarted() {}

        override fun onWalletStartFailed(error: String?) {
            failed()
        }

        override fun onRefreshed(height: Long?) {
            firstBlock = refresh(firstBlock)
        }
    }

    override fun onCleared() {
        super.onCleared()
        WAZNWalletController.stopRefresh()
    }

    fun setAssetId(assetId: Int) {
        uiScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val dao = AppDatabase.getInstance().transactionInfoDao()
                    val list = dao.getTransactionInfoByAssetId(assetId)
                    convertData(list)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun convertData(list: List<TransactionInfo>) {
        val allInfo = mutableListOf<TransactionInfo>()
        val inInfo = mutableListOf<TransactionInfo>()
        val outInfo = mutableListOf<TransactionInfo>()
        allInfo.addAll(list)
        allTransfers.postValue(allInfo)
        inInfo.addAll(list.filter { it.direction == 0 })
        inTransfers.postValue(inInfo)
        outInfo.addAll(list.filter { it.direction == 1 })
        outTransfers.postValue(outInfo)
    }

    fun loadWallet(pwd: String) {

        password = pwd
        refreshEnabled = false

        uiScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    indeterminate.postValue(null)
                    connecting.postValue(R.string.block_connecting)

                    val wallet = AppDatabase.getInstance().walletDao().getActiveWallet()
                            ?: throw IllegalStateException()
                    activeWallet.postValue(wallet)

                    repository.insertNodes()
                    val node = AppDatabase.getInstance().nodeDao().getSymbolNode(wallet.symbol)
                            ?: throw IllegalStateException()
                    currentNode = node
                    val split = node.url.split(":")
                    WAZNWalletController.setNode(split[0], split[1].toInt())
                    val path = repository.getWalletFilePath(wallet.name)

                    WAZNWalletController.startWallet(path, pwd, wallet.restoreHeight, observer)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                failed()
            }
        }
    }

    private fun failed() {
        receiveEnabled.postValue(false)
        sendEnabled.postValue(false)
        synchronizeFailed.postValue(R.string.block_synchronize_failed)
        synchronizeProgress.postValue(0)
        refreshEnabled = true
    }

    @Synchronized
    private fun refresh(firstBlock: Long): Long {
        var firstBlockHeight = firstBlock
        if (WAZNWalletController.isSynchronized()) {
            receiveEnabled.postValue(true)
            sendEnabled.postValue(true)
            synchronized.postValue(R.string.block_synchronized)
            synchronizeProgress.postValue(100)
            updateBalance()
            updateHistory()
        } else {
            sendEnabled.postValue(false)
            // calculate progress
            val daemonHeight = WAZNWalletController.getDaemonBlockChainHeight()
            val blockChainHeight = WAZNWalletController.getBlockChainHeight()
            val n = daemonHeight - blockChainHeight
            Log.d("refresh", "daemonHeight: $daemonHeight, blockChainHeight: $blockChainHeight, n: $n, " +
                    "daemonBlockChainTargetHeight: ${WAZNWalletController.getDaemonBlockChainTargetHeight()}")
            if (n >= 0) {
                if (firstBlockHeight == 0L) {
                    firstBlockHeight = blockChainHeight
                }
                val x = 100 - Math.round(100f * n / (1f * daemonHeight - firstBlockHeight))
                synchronizeProgress.postValue(x)
                synchronizing.postValue(n)
            } else {
                receiveEnabled.postValue(true)
                sendEnabled.postValue(true)
                synchronized.postValue(R.string.block_synchronized)
                synchronizeProgress.postValue(100)
            }
        }

        return firstBlockHeight
    }

    @Synchronized
    private fun updateBalance() {
        val asset = activeAsset ?: return
        val wallet = AppDatabase.getInstance().walletDao().getActiveWallet() ?: return
        val balance = WAZNWalletController.getBalance() ?: return
        val assetDao = AppDatabase.getInstance().assetDao()
        assetDao.updateAsset(asset.also {
            it.balance = WAZNWalletController.getDisplayAmount(balance)
        })
        // 适用非合约
        val walletDao = AppDatabase.getInstance().walletDao()
        walletDao.updateWallets(wallet.also {
            it.balance = WAZNWalletController.getDisplayAmount(balance)
        })
    }

    @Synchronized
    private fun updateHistory() {
        val asset = activeAsset ?: return
        val wallet = AppDatabase.getInstance().walletDao().getActiveWallet() ?: return
        val value = allTransfers.value
        WAZNWalletController.refreshTransactionHistory()
        val list = WAZNWalletController.getTransactionHistory()
        list.forEach {
            it.token = asset.token
            it.assetId = asset.id
            it.walletId = wallet.id
        }
        if (value.isNullOrEmpty() || !(value.toTypedArray() contentDeepEquals list.toTypedArray())) {
            val dao = AppDatabase.getInstance().transactionInfoDao()
            val info = dao.getTransactionInfoByAssetId(asset.id)
            dao.deleteTransactionInfo(*info.toTypedArray())
            dao.insertTransactionInfo(*list.toTypedArray())
            convertData(list)
        }
    }

    private fun switchNode(node: Node) {

        val wallet = WAZNWalletController.getWallet()
        // 异常处理
        if (wallet == null) {
            if (password.isNullOrBlank()) {
                showPasswordDialog.postValue(true)
            } else {
                loadWallet(password!!)
            }
        } else {
            // 相同节点直接返回
            val curNode = currentNode
            if (curNode != null && curNode.id == node.id) {
                return
            }
            currentNode = node
            uiScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        val activeWallet = AppDatabase.getInstance().walletDao().getActiveWallet() ?: throw IllegalStateException()
                        WAZNWalletController.stopRefresh()
                        indeterminate.postValue(null)
                        connecting.postValue(R.string.block_connecting)
                        val split = node.url.split(":")
                        WAZNWalletController.setNode(split[0], split[1].toInt())
                        WAZNWalletController.startRefresh(wallet, activeWallet.restoreHeight, observer)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    failed()
                }
            }
        }
    }

    fun send() {
        openSend.value = true
    }

    fun receive() {
        openReceive.value = true
    }

    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_SELECT_NODE -> {
                val node = data?.getParcelableExtra<Node>("node") ?: return
                switchNode(node)
            }
        }
    }

}
