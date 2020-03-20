package io.wazniya.wallet.feature.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import io.wazniya.wallet.App
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseTitleSecondActivity
import io.wazniya.wallet.support.BackgroundHelper
import io.wazniya.wallet.support.extensions.dp2px
import io.wazniya.wallet.support.extensions.openBrowser
import io.wazniya.wallet.support.extensions.versionName
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : BaseTitleSecondActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setCenterTitle(R.string.about_us)

        version.setLeftString(getString(R.string.version_placeholder, versionName()))
        version.setOnClickListener {
            openBrowser("https://wallet.wazn.io")
        }

        agreement.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.newVersion) {
            version.rightTextView.visibility = View.VISIBLE
            version.setRightString(getString(R.string.find_new_version))
            version.rightTextView.compoundDrawablePadding = dp2px(5)
            version.rightTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                    BackgroundHelper.getRedDotDrawable(this), null)
        } else {
            version.rightTextView.visibility = View.INVISIBLE
        }
    }
}
