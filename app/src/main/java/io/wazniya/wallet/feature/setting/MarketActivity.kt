package io.wazniya.wallet.feature.setting

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseTitleSecondActivity
import io.wazniya.wallet.data.entity.Market
import io.wazniya.wallet.dialog.PickerDialog
import io.wazniya.wallet.support.extensions.dp2px
import io.wazniya.wallet.support.extensions.putString
import io.wazniya.wallet.support.extensions.sharedPreferences
import io.wazniya.wallet.widget.DividerItemDecoration
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_market.*
import java.math.RoundingMode
import java.util.*

class MarketActivity : BaseTitleSecondActivity() {

    val CURRENCY_LIST = listOf(
        "usd",
        "cny",
        "eur",
        "aed",
        "ars",
        "aud",
        "bdt",
        "bhd",
        "bmd",
        "brl",
        "cad",
        "chf",
        "clp",
        "czk",
        "dkk",
        "gbp",
        "hkd",
        "huf",
        "idr",
        "ils",
        "inr",
        "krw",
        "kwd",
        "lkr",
        "mmk",
        "mxn",
        "myr",
        "nok",
        "nzd",
        "php",
        "pkr",
        "pln",
        "rub",
        "sar",
        "sek",
        "sgd",
        "thb",
        "try",
        "twd",
        "uah",
        "vef",
        "vnd",
        "zar"
    )

    private var currentCurrency = sharedPreferences().getString("currentCurrency", CURRENCY_LIST[0])?:CURRENCY_LIST[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market)
        setCenterTitle(R.string.market)

        setRightIcon(R.drawable.icon_switch_currency)

        val viewModel = ViewModelProviders.of(this).get(MarketViewModel::class.java)

        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(this)
        val list = mutableListOf<Market>()

        list.add(Market(R.drawable.icon_market_btc, "BTC"))
        list.add(Market(R.drawable.icon_market_ltc, "LTC"))
        list.add(Market(R.drawable.icon_market_eos, "EOS"))
        list.add(Market(R.drawable.icon_market_eth, "ETH"))

        list.forEach {
            it.currency = currentCurrency
        }

        viewModel.loadData(currentCurrency)

        viewModel.priceMap.observe(this, Observer { map ->
            map?.let {
                if (currentCurrency == map["currency"]) {
                    val waznPrice = map["WAZN"]
                    val waznDec = waznPrice?.toBigDecimalOrNull()
                    list.forEach {
                        it.price = map[it.symbol] ?: ""
                        if (waznDec != null) {
                            try {
                                it.valuation =
                                    it.price.toBigDecimal()
                                        .divide(waznDec, 2, RoundingMode.HALF_EVEN)
                                        .stripTrailingZeros().toPlainString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                it.valuation = ""
                            }
                        } else {
                            it.valuation = ""
                        }
                    }
                }
            }
        })

        viewModel.loading.observe(this, Observer {
            if (it == true) {
                showLoading()
            } else {
                hideLoading()
            }
        })

        setRightIconClick(View.OnClickListener {
            PickerDialog.display(supportFragmentManager, CURRENCY_LIST, null) {
                if (it != currentCurrency) {
                    currentCurrency = it
                    list.forEach { m ->
                        m.currency = it
                    }
                    sharedPreferences().putString("currentCurrency", it)
                    viewModel.loadData(it)
                }
            }
        })
    }
}
