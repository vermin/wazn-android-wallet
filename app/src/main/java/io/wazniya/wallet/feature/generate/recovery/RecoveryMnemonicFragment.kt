package io.wazniya.wallet.feature.generate.recovery

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.wazniya.wallet.ActivityStackManager
import io.wazniya.wallet.MainActivity
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseFragment
import io.wazniya.wallet.feature.wallet.WalletManagerActivity
import io.wazniya.wallet.support.BackgroundHelper
import io.wazniya.wallet.support.extensions.*
import io.wazniya.wallet.widget.IOSDialog
import kotlinx.android.synthetic.main.fragment_recovery_mnemonic.*


class RecoveryMnemonicFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recovery_mnemonic, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val walletName = arguments?.getString("walletName")
        val password = arguments?.getString("password")
        val passwordPrompt = arguments?.getString("passwordPrompt")

        if (walletName.isNullOrBlank() || password.isNullOrBlank()) {
            activity?.finish()
            return
        }

        val viewModel = ViewModelProviders.of(this).get(RecoveryMnemonicViewModel::class.java)

        viewModel.initData(walletName, password, passwordPrompt)

        mnemonic.background = BackgroundHelper.getEditBackground(context)
        next.background = BackgroundHelper.getButtonBackground(context)

        mnemonic.afterTextChanged {
            viewModel.mnemonic.value = it
        }

        blockHeight.editText?.afterTextChanged {
            viewModel.setBlockHeight(it)
        }

        dateContainer.showTimePicker {
            viewModel.setTransactionDate(it.formatterDate())
        }

        viewModel.transactionDate.observe(this, Observer { value ->
            value?.let {
                transactionDate.editText?.setText(it)
            }
        })

        next.setOnClickListener {
            viewModel.next()
        }

        viewModel.enabled.observe(this, Observer { value ->
            value?.let {
                next.isEnabled = it
            }
        })

        viewModel.navigation.observe(this, Observer {
            navigation()
        })

        viewModel.showLoading.observe(this, Observer { showLoading() })
        viewModel.hideLoading.observe(this, Observer { hideLoading() })
        viewModel.toast.observe(this, Observer { toast(it) })

        viewModel.blockHeightError.observe(this, Observer {
            if (it != null && it) {
                blockHeight.error = getString(R.string.block_height_invalid)
            } else {
                blockHeight.error = null
            }
        })

        viewModel.showDialog.observe(this, Observer {
            IOSDialog(context)
                    .radius(dp2px(5))
                    .titleText("")
                    .contentText(getString(R.string.dialog_block_height_content))
                    .contentTextSize(16)
                    .contentTextBold(true)
                    .leftText(getString(R.string.dialog_block_height_cancel))
                    .rightText(getString(R.string.dialog_block_height_confirm))
                    .setIOSDialogLeftListener { viewModel.create() }
                    .cancelAble(true)
                    .layout()
                    .show()
        })
    }

    private fun navigation() {
        if (ActivityStackManager.getInstance().contain(WalletManagerActivity::class.java)) {
            ActivityStackManager.getInstance().finishToActivity(WalletManagerActivity::class.java)
        } else {
            startActivity(Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })
            activity?.finish()
        }
    }
}