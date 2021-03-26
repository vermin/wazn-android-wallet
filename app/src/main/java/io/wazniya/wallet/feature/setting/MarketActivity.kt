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
import kotlinx.android.synthetic.main.item_market.*
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
        list.add(Market(R.drawable.icon_market_eth, "ETH"))

        list.forEach {
            it.currency = currentCurrency
        }

        val adapter = MarketAdapter(list, viewModel)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration().apply {
            setDividerColor(ContextCompat.getColor(this@MarketActivity, R.color.color_EBEDF0))
            setOrientation(DividerItemDecoration.VERTICAL)
            setMarginStart(dp2px(25))
        })

        viewModel.loadData(currentCurrency)

        viewModel.priceMap.observe(this, Observer { map ->
            map?.let {
                if (currentCurrency == map["currency"]) {
                    val xmrPrice = map["XMR"]
                    val xmrDec = xmrPrice?.toBigDecimalOrNull()
                    list.forEach {
                        it.price = map[it.symbol] ?: ""
                        if (xmrDec != null) {
                            try {
                                it.valuation =
                                    it.price.toBigDecimal()
                                        .divide(xmrDec, 2, RoundingMode.HALF_EVEN)
                                        .stripTrailingZeros().toPlainString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                it.valuation = ""
                            }
                        } else {
                            it.valuation = ""
                        }
                    }
                    adapter.notifyDataSetChanged()
                    if (xmrPrice.isNullOrBlank()) {
                        rate.text = "--"
                    } else {
                        rate.text = "1 XMR ≈ $xmrPrice ${currentCurrency.toUpperCase(Locale.CHINA)}"
                    }
                } else {
                    list.forEach {
                        it.price = ""
                        it.valuation = ""
                    }
                    adapter.notifyDataSetChanged()
                    rate.text = "--"
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

    class MarketAdapter(val data: List<Market>, val viewModel: MarketViewModel) :
        androidx.recyclerview.widget.RecyclerView.Adapter<MarketAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_market, parent, false)
            return ViewHolder(view, viewModel)
        }

        override fun getItemCount() = data.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindViewHolder(data[position])
        }

        class ViewHolder(override val containerView: View, val viewModel: MarketViewModel) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(containerView), LayoutContainer {

            fun bindViewHolder(market: Market) {
                icon.setImageResource(market.iconRec)
                symbol.text = market.symbol
                valuation.text = if (market.valuation.isBlank()) {
                    "--"
                } else {
                    "≈ ${market.valuation} XMR"
                }
                price.text = if (market.price.isBlank()) {
                    "--"
                } else {
                    market.price
                }
                currency.text = market.currency.toUpperCase(Locale.CHINA)
            }
        }
    }
}
