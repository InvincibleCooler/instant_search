package com.kakaoent.realtime.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kakaoent.realtime.search.data.TestData
import com.kakaoent.realtime.search.db.CryptoCurrencyDatabase
import com.kakaoent.realtime.search.db.CryptoCurrencyEntity
import com.kakaoent.realtime.search.utils.HangulUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.min


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dao = CryptoCurrencyDatabase.get().cryptoCurrencyDao()
        CoroutineScope(Dispatchers.IO).launch {
            val count = dao.getTotalCount()
            if (count <= 0 && TestData.dataList.isNullOrEmpty()) {
                TestData.init()
                val dataList = TestData.dataList
                val cryptoCurrencyEntityList = dataList.map {
                    CryptoCurrencyEntity().apply {
                        cryptoCurrencyId = it.cryptoCurrencyId
                        cryptoCurrencyName = it.cryptoCurrencyName
                        koreanBreak = HangulUtils.breakKorean2Elements(it.cryptoCurrencyName)
                        initial = HangulUtils.getHangulInitial(it.cryptoCurrencyName)
                        timestamp = System.currentTimeMillis()
                    }
                }.toList()
                dao.insertCryptoCurrencyInfoList(cryptoCurrencyEntityList)
            }
        }
    }
}