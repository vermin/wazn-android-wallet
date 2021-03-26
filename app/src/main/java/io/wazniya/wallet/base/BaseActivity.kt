package io.wazniya.wallet.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager
import io.wazniya.wallet.dialog.LoadingDialog
import io.wazniya.wallet.support.extensions.getLocale
import io.wazniya.wallet.support.extensions.setLocale

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(setLocale(newBase, getLocale()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 禁止截图
        if (hide()) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    open fun hide(): Boolean = true

    var loadingDialog: LoadingDialog? = null

    open fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(this)
        }
        loadingDialog?.show()
    }

    open fun hideLoading() {
        if (!isFinishing && !isDestroyed) loadingDialog?.dismiss()
    }
}
