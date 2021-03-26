package io.wazniya.wallet.feature.generate.recovery

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseTitleSecondActivity
import kotlinx.android.synthetic.main.activity_recovery_wallet.*

class RecoveryWalletActivity : BaseTitleSecondActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_recovery_wallet)
        setCenterTitle(R.string.recovery_wallet)

        val walletName = intent.getStringExtra("walletName")
        val password = intent.getStringExtra("password")
        val passwordPrompt = intent.getStringExtra("passwordPrompt")

        val titles = arrayOf(getString(R.string.recovery_mnemonic), getString(R.string.recovery_private_key))

        val mnemonicFragment = RecoveryMnemonicFragment().apply {
            arguments = Bundle().also {
                it.putString("walletName", walletName)
                it.putString("password", password)
                it.putString("passwordPrompt", passwordPrompt)
            }
        }
        val privateKeyFragment = RecoveryPrivateKeyFragment().apply {
            arguments = Bundle().also {
                it.putString("walletName", walletName)
                it.putString("password", password)
                it.putString("passwordPrompt", passwordPrompt)
            }
        }

        val fragments = arrayOf(mnemonicFragment, privateKeyFragment)

        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): androidx.fragment.app.Fragment {
                return fragments[position]
            }

            override fun getCount(): Int = titles.size

            override fun getPageTitle(position: Int): CharSequence? {
                return titles[position]
            }

        }
        tabLayout.setupWithViewPager(viewPager)
    }
}
