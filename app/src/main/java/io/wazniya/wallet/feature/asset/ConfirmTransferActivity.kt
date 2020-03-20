package io.wazniya.wallet.feature.asset

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseTitleSecondActivity
import io.wazniya.wallet.dialog.PasswordDialog
import io.wazniya.wallet.support.BackgroundHelper
import io.wazniya.wallet.support.extensions.formatterAmountStrip
import io.wazniya.wallet.support.extensions.setImage
import io.wazniya.wallet.support.extensions.toast
import kotlinx.android.synthetic.main.activity_confirm_transfer.*

class ConfirmTransferActivity : BaseTitleSecondActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_transfer)
        setCenterTitle(R.string.confirm_transfer)

        val token = intent.getStringExtra("token")
        val addressValue = intent.getStringExtra("address")
        val paymentIDValue = intent.getStringExtra("paymentID")

        if (token.isNullOrBlank() || addressValue.isNullOrBlank()) {
            finish()
            return
        }

        val viewModel = ViewModelProviders.of(this).get(ConfirmTransferViewModel::class.java)

        icon.setImage(token)
        address.text = addressValue
        paymentID.text = paymentIDValue ?: ""

        next.background = BackgroundHelper.getButtonBackground(this)

        next.setOnClickListener {
            val id = viewModel.activeWallet?.id ?: return@setOnClickListener
            PasswordDialog.display(supportFragmentManager, id) {
                viewModel.next()
            }
        }

        viewModel.amount.observe(this, Observer { value ->
            value?.let {
                amount.text = "${it.formatterAmountStrip()} $token"
            }
        })

        viewModel.fee.observe(this, Observer { value ->
            value?.let {
                fee.text = it.formatterAmountStrip()
            }
        })

        viewModel.enabled.observe(this, Observer { value ->
            value?.let {
                next.isEnabled = it
            }
        })

        viewModel.toast.observe(this, Observer { toast(it) })
        viewModel.toastInt.observe(this, Observer { toast(it) })
    }
}