package io.wazniya.wallet.feature.swap

import androidx.lifecycle.MutableLiveData
import io.wazniya.wallet.base.BaseViewModel
import io.wazniya.wallet.data.AppDatabase
import io.wazniya.wallet.data.remote.RetrofitManager
import io.wazniya.wallet.data.remote.entity.RPCRequest
import io.wazniya.wallet.data.remote.entity.SwapTransaction
import io.wazniya.wallet.support.extensions.unWrap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SwapDetailViewModel : BaseViewModel() {

    val showLoading = MutableLiveData<Boolean>()
    val hideLoading = MutableLiveData<Boolean>()
    val toast = MutableLiveData<String>()

    val swapTransaction = MutableLiveData<SwapTransaction>()

    fun loadData(id: String) {
        showLoading.value = true
        uiScope.launch {
            try {
                val list = RetrofitManager.changellyService.getTransactionByID(
                    RPCRequest(
                        method = "getTransactions",
                        params = mapOf("id" to id, "currency" to "wazn")
                    )
                ).unWrap()
                if (list.isNullOrEmpty()) {
                    throw IllegalArgumentException("data exception")
                } else {
                    val transaction = list[0]
                    withContext(Dispatchers.IO) {
                        val swapRecordDao = AppDatabase.getInstance().swapRecordDao()
                        swapRecordDao.getSwapRecordBySwapId(id)?.let {
                            swapRecordDao.update(it.apply {
                                try {
                                    val amountFromNet = transaction.amountFrom
                                    if (amountFromNet.isNullOrBlank() || amountFromNet.toBigDecimal() <= "0.0".toBigDecimal()) {
                                        if (!transaction.amountExpectedFrom.isNullOrBlank()) {
                                            amountFrom = transaction.amountExpectedFrom
                                        }
                                    } else {
                                        amountFrom = amountFromNet
                                    }
                                    val amountToNet = transaction.amountTo
                                    if (amountToNet.isNullOrBlank() || amountToNet.toBigDecimal() <= "0.0".toBigDecimal()) {
                                        if (!transaction.amountExpectedTo.isNullOrBlank()) {
                                            amountTo = transaction.amountExpectedTo
                                        }
                                    } else {
                                        amountTo = amountToNet
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                createdAt = transaction.createdAt.toString()
                            })
                        }
                        if (!transaction.payoutAddress.isNullOrBlank()) {
                            val addressList =
                                AppDatabase.getInstance().swapAddressBookDao()
                                    .loadAddressBooksBySymbolAndAddress(
                                        transaction.currencyTo.toLowerCase(Locale.CHINA),
                                        transaction.payoutAddress
                                    )
                            if (!addressList.isNullOrEmpty()) {
                                transaction.addressTag = addressList[0].notes
                            }
                        }
                    }
                    swapTransaction.postValue(transaction)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                swapTransaction.postValue(null)
                toast.postValue(e.message)
            }
            hideLoading.postValue(true)
        }
    }
}
