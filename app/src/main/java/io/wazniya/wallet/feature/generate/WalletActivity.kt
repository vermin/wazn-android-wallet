package io.wazniya.wallet.feature.generate

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseActivity
import io.wazniya.wallet.support.BackgroundHelper
import io.wazniya.wallet.support.WALLET_CREATE
import io.wazniya.wallet.support.WALLET_RECOVERY
import io.wazniya.wallet.support.extensions.putInt
import io.wazniya.wallet.support.extensions.putString
import io.wazniya.wallet.support.extensions.sharedPreferences
import io.wazniya.wallet.support.utils.StatusBarHelper
import kotlinx.android.synthetic.main.activity_wallet.*

class WalletActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        StatusBarHelper.translucent(this, ContextCompat.getColor(this, R.color.color_FFFFFF))
        StatusBarHelper.setStatusBarLightMode(this)

        createWallet.background = BackgroundHelper.getButtonBackground(this, R.color.color_339933)
        recoveryWallet.background = BackgroundHelper.getButtonBackground(this, R.color.color_006633)

        sharedPreferences().putString("symbol", "WAZN")
        createWallet.setOnClickListener {
            startActivity(Intent(this, GenerateWalletActivity::class.java).apply {
                sharedPreferences().putInt("type", WALLET_CREATE)
            })
        }
        recoveryWallet.setOnClickListener {
            startActivity(Intent(this, GenerateWalletActivity::class.java).apply {
                sharedPreferences().putInt("type", WALLET_RECOVERY)
            })
        }
    }
}
