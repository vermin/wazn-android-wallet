package io.wazniya.wallet.feature.wallet

import android.arch.lifecycle.MutableLiveData
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseViewModel
import io.wazniya.wallet.core.WAZNRepository
import io.wazniya.wallet.core.WAZNWalletController
import io.wazniya.wallet.data.AppDatabase
import io.wazniya.wallet.data.entity.SubAddress
import io.wazniya.wallet.data.entity.Wallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressSettingViewModel : BaseViewModel() {

    private val repository = WAZNRepository()

    var walletId = -1
    var password: String? = null
    var currentAddress: String? = null
    var wallet: Wallet? = null

    val subAddresses = MutableLiveData<List<SubAddress>>()
    val showLoading = MutableLiveData<Boolean>()
    val hideLoading = MutableLiveData<Boolean>()
    val toast = MutableLiveData<String>()
    val toastRes = MutableLiveData<Int>()

    val copy = MutableLiveData<String>()

    val dataChanged = MutableLiveData<String>()


    fun loadSubAddresses() {
        if (password.isNullOrBlank()) {
            toastRes.postValue(R.string.data_exception)
            return
        }
        showLoading.postValue(true)
        uiScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    if (walletId < 0) {
                        loadActiveWallet()
                    } else {
                        loadWalletById()
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

    private fun loadActiveWallet() {
        val wallet = AppDatabase.getInstance().walletDao().getActiveWallet()
        if (wallet == null) {
            toastRes.postValue(R.string.data_exception)
            return
        }
        this.wallet = wallet
        walletId = wallet.id
        openWallet(wallet)
        currentAddress = wallet.address
        subAddresses.postValue(WAZNWalletController.getSubAddresses())
    }

    private fun loadWalletById() {
        val wallet = AppDatabase.getInstance().walletDao().getWalletById(walletId)
        if (wallet == null) {
            toastRes.postValue(R.string.data_exception)
            return
        }
        this.wallet = wallet
        val path = repository.getWalletFilePath(wallet.name)
        WAZNWalletController.openWallet(path, password!!)
        currentAddress = wallet.address
        subAddresses.postValue(WAZNWalletController.getSubAddresses())
    }

    fun refreshSubAddresses() {
        uiScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    openWallet(wallet)
                    subAddresses.postValue(WAZNWalletController.getSubAddresses())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun openWallet(wallet: Wallet?) {
        if (wallet == null) {
            return
        }
        if (WAZNWalletController.getWallet() == null) {
            val path = repository.getWalletFilePath(wallet.name)
            WAZNWalletController.openWallet(path, password!!)
        }
    }

    fun onAddressClick(subAddress: SubAddress) {
        copy.value = subAddress.address
    }

    fun onItemClick(subAddress: SubAddress) {
        if (walletId < 0) {
            toastRes.postValue(R.string.data_exception)
            return
        }
        uiScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val walletDao = AppDatabase.getInstance().walletDao()
                    val wallet = walletDao.getWalletById(walletId)
                    if (wallet == null) {
                        toastRes.postValue(R.string.data_exception)
                    } else {
                        walletDao.updateWallets(wallet.apply { address = subAddress.address })
                        currentAddress = subAddress.address
                        dataChanged.postValue(currentAddress)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                toast.postValue(e.message)
            }
        }
    }
}