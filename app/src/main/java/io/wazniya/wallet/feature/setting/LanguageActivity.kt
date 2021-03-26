package io.wazniya.wallet.feature.setting

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.wazniya.wallet.ActivityStackManager
import io.wazniya.wallet.MainActivity
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseTitleSecondActivity
import io.wazniya.wallet.support.extensions.*
import io.wazniya.wallet.widget.DividerItemDecoration
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_language.*
import kotlinx.android.synthetic.main.item_language.*

class LanguageActivity : BaseTitleSecondActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)
        setCenterTitle(R.string.select_language)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val list = listOf("zh-CN", "en")
        val adapter = LanguageAdapter(list) {
            setLocale(this, it)
            recreate()
            ActivityStackManager.getInstance().get(MainActivity::class.java)?.recreate()
        }
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration().apply {
            setOrientation(DividerItemDecoration.VERTICAL)
            setMarginStart(dp2px(25))
        })
    }

    class LanguageAdapter(val data: List<String>, val listener: (String) -> Unit) :
        androidx.recyclerview.widget.RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_language, parent, false)
            return ViewHolder(view, listener)
        }

        override fun getItemCount() = data.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindViewHolder(data[position])
        }

        class ViewHolder(override val containerView: View, val listener: (String) -> Unit) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(containerView), LayoutContainer {
            fun bindViewHolder(lang: String) {
                content.text = getDisplayName(lang)
                if (containerView.context.isSelectedLanguage(lang)) {
                    selected.setImageResource(R.drawable.icon_selected)
                } else {
                    selected.setImageResource(R.drawable.icon_unselected)
                }
                itemView.setOnClickListener { listener(lang) }
            }
        }
    }
}
