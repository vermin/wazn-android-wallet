package io.wazniya.wallet.feature.asset

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.graphics.Bitmap
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseViewModel
import io.wazniya.wallet.core.WAZNWalletController
import io.wazniya.wallet.data.AppDatabase
import io.wazniya.wallet.data.entity.Asset
import io.wazniya.wallet.data.entity.Wallet
import io.wazniya.wallet.support.REQUEST_SELECT_SUB_ADDRESS
import io.wazniya.wallet.support.extensions.dp2px
import io.wazniya.wallet.support.viewmodel.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReceiveViewModel : BaseViewModel() {

    val activeAsset = MutableLiveData<Asset>()
    val activeWallet = MutableLiveData<Wallet>()
    val address = MutableLiveData<String>()
    val visibilityIcon = MutableLiveData<Int>()
    var addressVisibility = true

    val QRCodeBitmap = MutableLiveData<Bitmap>()
    val toast = MutableLiveData<Int>()

    var isCollapsing = true
    val moreOptions = SingleLiveEvent<Unit>()
    val collapsingOptions = SingleLiveEvent<Unit>()

    val paymentId = MutableLiveData<String>()
    val integratedAddress = MutableLiveData<String>()

    val paymentIdError = MutableLiveData<Int>()
    val integratedError = MutableLiveData<Int>()

    fun setAssetId(assetId: Int) {
        uiScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val asset = AppDatabase.getInstance().assetDao().getAssetById(assetId)
                        ?: throw IllegalStateException()
                    activeAsset.postValue(asset)
                    val wallet = AppDatabase.getInstance().walletDao().getActiveWallet()
                        ?: throw IllegalStateException()
                    activeWallet.postValue(wallet)
                    address.postValue(wallet.address)
                    if (WAZNWalletController.isAddressValid(wallet.address)) {
                        QRCodeBitmap.postValue(QRCodeEncoder.syncEncodeQRCode(wallet.address, dp2px(115)))
                    } else {
                        QRCodeBitmap.postValue(null)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                toast.postValue(R.string.data_exception)
            }
        }
    }

    fun setAddressVisible() {
        val addressStr = activeWallet.value?.address ?: ""
        if (addressStr.isBlank()) {
            return
        }
        addressVisibility = !addressVisibility
        if (addressVisibility) {
            address.value = addressStr
            visibilityIcon.value = R.drawable.icon_visible_space
        } else {
            visibilityIcon.value = R.drawable.icon_invisible_space
            val str = StringBuilder()
            addressStr.forEach {
                str.append("*")
            }
            address.value = str.toString()
        }
    }

    fun more() {
        isCollapsing = !isCollapsing
        if (isCollapsing) {
            collapsingOptions.call()
        } else {
            moreOptions.call()
        }
    }

    fun generate() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                paymentId.postValue(WAZNWalletController.generatePaymentId())
            }
        }
    }

    fun paymentIdChanged(id: String) {
        if (id.isNullOrBlank()) {
            integratedAddress.value = ""
            return
        }
        uiScope.launch {
            withContext(Dispatchers.IO) {
                if (id.length == 16 || id.length == 64) {
                    if (WAZNWalletController.isPaymentIdValid(id)) {
                        integratedAddress.postValue(WAZNWalletController.getIntegratedAddress(id))
                        paymentIdError.postValue(null)
                    } else {
                        integratedAddress.postValue("")
                        paymentIdError.postValue(R.string.payment_id_invalid)
                    }
                } else {
                    integratedAddress.postValue("")
                }
            }
        }
    }

    fun integratedChanged(value: String) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                var address = value
                if (!address.isNullOrBlank()) {
                    if (WAZNWalletController.isAddressValid(address)) {
                        integratedError.postValue(null)
                    } else {
                        integratedError.postValue(R.string.integrated_invalid)
                    }
                } else {
                    address = activeWallet.value?.address ?: ""
                    integratedError.postValue(null)
                }
                if (WAZNWalletController.isAddressValid(address)) {
                    QRCodeBitmap.postValue(QRCodeEncoder.syncEncodeQRCode(address, dp2px(115)))
                } else {
                    QRCodeBitmap.postValue(null)
                }
            }
        }
    }

    fun generateQRCode(address: String) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                if (WAZNWalletController.isAddressValid(address)) {
                    QRCodeBitmap.postValue(QRCodeEncoder.syncEncodeQRCode(address, dp2px(115)))
                } else {
                    QRCodeBitmap.postValue(null)
                }
            }
        }
    }

    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_SELECT_SUB_ADDRESS -> {
                val subAddress = data?.getStringExtra("subAddress") ?: return
                address.value = subAddress
                generateQRCode(subAddress)
            }
        }
    }

}