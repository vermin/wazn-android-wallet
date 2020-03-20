package io.wazniya.wallet.feature.asset

import android.content.Intent
import io.wazniya.wallet.base.BaseViewModel
import io.wazniya.wallet.data.entity.Asset
import io.wazniya.wallet.data.entity.Wallet
import io.wazniya.wallet.support.extensions.putBoolean
import io.wazniya.wallet.support.extensions.sharedPreferences
import io.wazniya.wallet.support.viewmodel.SingleLiveEvent

class AssetViewModel : BaseViewModel() {

    val showPasswordDialog = SingleLiveEvent<Unit>()
    val openAssetDetail = SingleLiveEvent<Intent>()

    var wallet: Wallet? = null

    val assetVisible = SingleLiveEvent<Unit>()
    val assetInvisible = SingleLiveEvent<Unit>()

    private var asset: Asset? = null

    fun initVisible() {
        val visible = sharedPreferences().getBoolean("assetVisible", true)
        if (visible) {
            assetVisible.call()
        } else {
            assetInvisible.call()
        }
    }

    fun onItemClick(value: Asset) {
        asset = value
        showPasswordDialog.call()
    }

    fun next(password: String) {
        if (asset != null) {
            openAssetDetail.value = Intent().apply {
                putExtra("password", password)
                putExtra("assetId", asset!!.id)
            }
            asset = null
        }
    }

    fun assetVisibleChanged() {
        val visible = sharedPreferences().getBoolean("assetVisible", true)
        sharedPreferences().putBoolean("assetVisible", !visible)
        if (!visible) {
            assetVisible.call()
        } else {
            assetInvisible.call()
        }
    }
}