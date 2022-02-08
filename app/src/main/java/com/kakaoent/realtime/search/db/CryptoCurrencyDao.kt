package com.kakaoent.realtime.search.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


/**
 * 쿼리 배치
 * select : getXXXX
 * insert : insertXXXX
 * update : updateXXXX
 * delete : deleteXXXX
 */
@Dao
interface CryptoCurrencyDao {
    @Query("select count(*) from tb_crypto_currency")
    fun getTotalCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCryptoCurrencyInfoList(list: List<CryptoCurrencyEntity>)

    @Query("select * from tb_crypto_currency where initial like '%' || :initial || '%'")
    fun getCryptoCurrencyListByCho(initial: String): List<CryptoCurrencyEntity>

    @Query("select * from tb_crypto_currency where korean_break like '%' || :text || '%'")
    fun getCryptoCurrencyListByText(text: String): List<CryptoCurrencyEntity>
}