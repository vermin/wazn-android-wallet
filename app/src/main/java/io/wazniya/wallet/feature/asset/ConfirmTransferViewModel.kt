package io.wazniya.wallet.feature.asset

import android.arch.lifecycle.MutableLiveData
import android.os.SystemClock
import io.wazniya.wallet.ActivityStackManager
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseViewModel
import io.wazniya.wallet.core.WAZNWalletController
import io.wazniya.wallet.data.AppDatabase
import io.wazniya.wallet.data.entity.Wallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmTransferViewModel : BaseViewModel() {

    val amount = MutableLiveData<String>()
    val fee = MutableLiveData<String>()
    val enabled = MutableLiveData<Boolean>()

    var activeWallet: Wallet? = null

    val toast = MutableLiveData<String>()
    val toastInt = MutableLiveData<Int>()

    init {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                activeWallet = AppDatabase.getInstance().walletDao().getActiveWallet()
                amount.postValue(WAZNWalletController.getTxAmount())
                fee.postValue(WAZNWalletController.getTxFee())
                if (activeWallet == null) {
                    toastInt.postValue(R.string.data_exception)
                    enabled.postValue(false)
                } else {
                    enabled.postValue(true)
                }
            }
        }
    }

    fun next() {
        enabled.postValue(false)
        uiScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    WAZNWalletController.sendTransaction()
                    SystemClock.sleep(300)
                    if (ActivityStackManager.getInstance().contain(AssetDetailActivity::class.java)) {
                        ActivityStackManager.getInstance().finishToActivity(AssetDetailActivity::class.java)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                toast.postValue(e.message)
            } finally {
                enabled.postValue(true)
            }
        }
    }
}