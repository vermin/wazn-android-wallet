package io.wazniya.wallet.feature.wallet

import androidx.lifecycle.MutableLiveData
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseViewModel
import io.wazniya.wallet.core.WAZNRepository
import io.wazniya.wallet.core.WAZNWalletController
import io.wazniya.wallet.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BackupKeyViewModel : BaseViewModel() {

    private val repository = WAZNRepository()

    val publicViewKey = MutableLiveData<String>()
    val secretViewKey = MutableLiveData<String>()
    val publicSpendKey = MutableLiveData<String>()
    val secretSpendKey = MutableLiveData<String>()
    val address = MutableLiveData<String>()

    val showLoading = MutableLiveData<Boolean>()
    val hideLoading = MutableLiveData<Boolean>()
    val toast = MutableLiveData<String>()
    val toastRes = MutableLiveData<Int>()

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
                        publicViewKey.postValue(WAZNWalletController.getPublicViewKey())
                        secretViewKey.postValue(WAZNWalletController.getSecretViewKey())
                        publicSpendKey.postValue(WAZNWalletController.getPublicSpendKey())
                        secretSpendKey.postValue(WAZNWalletController.getSecretSpendKey())
                        address.postValue(wallet.address)
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
