package io.wazniya.wallet.feature.asset

import androidx.lifecycle.MutableLiveData
import android.graphics.Bitmap
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import io.wazniya.wallet.base.BaseViewModel
import io.wazniya.wallet.support.extensions.dp2px
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionDetailViewModel : BaseViewModel() {
    var QRCodeBitmap = MutableLiveData<Bitmap>()
    fun setTxId(hash: String?) {
        hash?.let {
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    QRCodeBitmap.postValue(QRCodeEncoder.syncEncodeQRCode(it, dp2px(80)))
                }
            }
        }
    }
}
