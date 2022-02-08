package com.kakaoent.realtime.search.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.kakaoent.realtime.search.db.CryptoCurrencyDatabase
import com.kakaoent.realtime.search.db.CryptoCurrencyEntity
import com.kakaoent.realtime.search.utils.HangulUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"

        private const val TIME_INTERVAL = 500L
    }

    private val dao = CryptoCurrencyDatabase.get().cryptoCurrencyDao()

    @ExperimentalCoroutinesApi
    @FlowPreview
    val cryptoCurrentFlow = MutableSharedFlow<String>()

    @FlowPreview
    @ExperimentalCoroutinesApi
    val cryptoCurrentSharedFlow = cryptoCurrentFlow
        .asSharedFlow()
        .debounce(TIME_INTERVAL)
        .mapLatest { text ->
            withContext(Dispatchers.IO) {
                if (text.isEmpty()) {
                    emptyList()
                } else {
                    Log.d(TAG, "text : $text")
                    val isCho = HangulUtils.isChoSungList(text)
                    Log.d(TAG, "isCho : $isCho")
                    if (isCho) {
                        dao.getCryptoCurrencyListByCho(text)
                    } else {
                        val breaks = HangulUtils.breakKorean2Elements(text)
                        Log.d(TAG, "breaks : $breaks")
                        dao.getCryptoCurrencyListByText(breaks)
                    }
                }
            }
        }
        .catch {
            emptyList<CryptoCurrencyEntity>()
        }
        .asLiveData()

    class Factory : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel() as T
        }
    }
}