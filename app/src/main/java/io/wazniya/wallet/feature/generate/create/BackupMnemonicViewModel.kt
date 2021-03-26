package io.wazniya.wallet.feature.generate.create

import androidx.lifecycle.MutableLiveData
import android.view.View
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseViewModel
import io.wazniya.wallet.core.WAZNRepository
import io.wazniya.wallet.core.WAZNWalletController
import io.wazniya.wallet.data.AppDatabase
import io.wazniya.wallet.support.viewmodel.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BackupMnemonicViewModel : BaseViewModel() {

    private val repository = WAZNRepository()

    val title = MutableLiveData<Int>()
    val showPasswordDialog = SingleLiveEvent<Unit>()
    val showBackupDialog = MutableLiveData<Boolean>()
    val nextVisibility = MutableLiveData<Int>()

    val seedList = MutableLiveData<List<String>>()
    val showLoading = MutableLiveData<Boolean>()
    val hideLoading = MutableLiveData<Boolean>()
    val toast = MutableLiveData<String>()
    val toastRes = MutableLiveData<Int>()

    fun openWallet(walletId: Int, password: String?, seed: String?) {
        if (!seed.isNullOrBlank()) {
            // 创建钱包
            title.value = R.string.create_wallet
            nextVisibility.value = View.VISIBLE
            showBackupDialog.value = true
            seedList.value = seed.split(" ")
        } else if (!password.isNullOrBlank()) {
            // 导出助记词
            title.value = R.string.backup_mnemonic
            nextVisibility.value = View.GONE
            openWallet(walletId, password)
        } else {
            // 创建钱包后未备份助记词
            title.value = R.string.create_wallet
            nextVisibility.value = View.VISIBLE
            showPasswordDialog.call()
        }
    }

    fun openWallet(walletId: Int, password: String) {
        showLoading.postValue(true)
        uiScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val wallet = AppDatabase.getInstance().walletDao().getWalletById(walletId)
                    if (wallet == null) {
                        toastRes.postValue(R.string.data_exception)
                    } else {
                        val path = repository.getWalletFilePath(wallet.name)
                        WAZNWalletController.openWallet(path, password)
                        seedList.postValue(WAZNWalletController.getSeed().split(" "))
                        showBackupDialog.postValue(true)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                toast.postValue(e.message)
            } finally {
                hideLoading.postValue(true)
            }
        }
    }
}
