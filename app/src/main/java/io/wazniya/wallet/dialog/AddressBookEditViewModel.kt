package io.wazniya.wallet.dialog

import androidx.lifecycle.MutableLiveData
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseViewModel
import io.wazniya.wallet.data.AppDatabase
import io.wazniya.wallet.data.entity.AddressBook
import io.wazniya.wallet.data.entity.SwapAddressBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressBookEditViewModel : BaseViewModel() {

    val success = MutableLiveData<Boolean>()
    val showLoading = MutableLiveData<Boolean>()
    val hideLoading = MutableLiveData<Boolean>()
    val toastRes = MutableLiveData<Int>()

    fun updateAddressBook(addressBook: AddressBook) {
        showLoading.value = true
        uiScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    AppDatabase.getInstance().addressBookDao().updateAddressBook(addressBook)
                    success.postValue(true)
                } catch (e: Exception) {
                    e.printStackTrace()
                    toastRes.postValue(R.string.data_exception)
                }
            }
            hideLoading.postValue(true)
        }
    }

    fun updateAddressBook(swapAddressBook: SwapAddressBook) {
        showLoading.value = true
        uiScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    AppDatabase.getInstance().swapAddressBookDao().update(swapAddressBook)
                    success.postValue(true)
                } catch (e: Exception) {
                    e.printStackTrace()
                    toastRes.postValue(R.string.data_exception)
                }
            }
            hideLoading.postValue(true)
        }
    }

}
