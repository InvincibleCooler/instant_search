package com.kakaoent.realtime.search.fragment

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakaoent.realtime.search.R
import com.kakaoent.realtime.search.db.CryptoCurrencyEntity
import com.kakaoent.realtime.search.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

/**
 * https://www.hellsoft.se/instant-search-with-kotlin-coroutines/
 */
class MainFragment : Fragment() {
    companion object {
        private const val TAG = "MainFragment"
    }

    private lateinit var etSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var localAdapter: LocalAdapter
    private val viewModel by viewModels<MainViewModel> {
        MainViewModel.Factory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localAdapter = LocalAdapter(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel.cryptoCurrentSharedFlow.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                localAdapter.items = ArrayList()
            } else {
                localAdapter.items = it as MutableList
            }
            localAdapter.notifyDataSetChanged()
        })
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSearch = view.findViewById(R.id.et_search)
        etSearch.doAfterTextChanged {
            lifecycleScope.launch {
                Log.d(TAG, "it : ${it.toString()}")
                viewModel.cryptoCurrentFlow.emit(it.toString())
            }
        }

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = localAdapter
            setHasFixedSize(true)
            requestFocus()
        }
    }

    private inner class LocalAdapter(private val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val redColor = "#ff0000"
        val viewTypeItem = 1

        var items = mutableListOf<CryptoCurrencyEntity>()
            set(value) {
                field.clear()
                field.addAll(value)
            }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun getItemViewType(position: Int): Int {
            return viewTypeItem
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ItemViewHolder(
                LayoutInflater.from(activity).inflate(R.layout.listitem_search, parent, false)
            )
        }

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
            when (viewHolder.itemViewType) {
                viewTypeItem -> {
                    val vh = viewHolder as ItemViewHolder
                    val data = items[position]

                    val searchText = etSearch.text.toString().trim()
                    var cryptoCurrencyName = data.cryptoCurrencyName

                    if (cryptoCurrencyName.contains(searchText)) {
                        cryptoCurrencyName = cryptoCurrencyName.replace(searchText, "<font color=\"$redColor\">$searchText</font>")
                        Log.d(TAG, "changed cryptoCurrencyName : $cryptoCurrencyName")
                    }
                    vh.titleTv.text = Html.fromHtml(cryptoCurrencyName, Html.FROM_HTML_MODE_LEGACY)
                }
            }
        }

        private open inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val titleTv: TextView = view.findViewById(R.id.tv_title)
        }
    }
}