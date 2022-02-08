package com.kakaoent.realtime.search.db

import androidx.annotation.NonNull
import androidx.room.*


@Entity(indices = [(Index(value = ["crypto_currency_id"], unique = true))], tableName = "tb_crypto_currency")
data class CryptoCurrencyEntity @Ignore constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    @NonNull
    var uid: Long,

    @ColumnInfo(name = "crypto_currency_id")
    var cryptoCurrencyId: String,

    @ColumnInfo(name = "crypto_currency_name")
    var cryptoCurrencyName: String,

    @ColumnInfo(name = "korean_break") // 초성, 중성, 종성 조합
    var koreanBreak: String,

    @ColumnInfo(name = "initial") // 초성
    var initial: String,

    @ColumnInfo(name = "timestamp")
    var timestamp: Long = 0
) {
    constructor() : this(0, "", "", "", "", 0)
}