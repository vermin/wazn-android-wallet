package io.wazniya.wallet.feature.asset

import androidx.lifecycle.MutableLiveData
import android.os.SystemClock
import io.wazniya.wallet.ActivityStackManager
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseViewModel
import io.wazniya.wallet.core.WAZNRepository
import io.wazniya.wallet.core.WAZNWalletController
import io.wazniya.wallet.data.AppDatabase
import io.wazniya.wallet.data.entity.Wallet
import io.wazniya.wallet.data.entity.WalletRelease
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmTransferViewModel : BaseViewModel() {

    val amount = MutableLiveData<String>()
    val fee = MutableLiveData<String>()
    val enabled = MutableLiveData<Boolean>()

    var activeWallet: Wallet? = null
    var walletRelease: WalletRelease? = null

    val toast = MutableLiveData<String>()
    val toastInt = MutableLiveData<Int>()

    init {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                activeWallet = AppDatabase.getInstance().walletDao().getActiveWallet()
                activeWallet?.let {
                    walletRelease = AppDatabase.getInstance().walletReleaseDao().loadDataByWalletId(it.id)
                }
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

    fun next(password: String) {
        enabled.postValue(false)
        uiScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val verify = WAZNWalletController.verifyWalletPasswordOnly(WAZNRepository().getKeysFilePath(activeWallet!!.name), password)
                    if (verify) {
                        WAZNWalletController.sendTransaction()
                        SystemClock.sleep(300)
                        if (ActivityStackManager.getInstance().contain(AssetDetailActivity::class.java)) {
                            ActivityStackManager.getInstance().finishToActivity(AssetDetailActivity::class.java)
                        }
                    } else {
                        throw IllegalArgumentException("invalid password")
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
