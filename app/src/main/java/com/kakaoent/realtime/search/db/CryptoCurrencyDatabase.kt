package com.kakaoent.realtime.search.db

import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kakaoent.realtime.search.TestApp


private const val DB_VERSION = 1

@Database(entities = [(CryptoCurrencyEntity::class)], version = DB_VERSION, exportSchema = false)
abstract class CryptoCurrencyDatabase : RoomDatabase() {

    abstract fun cryptoCurrencyDao(): CryptoCurrencyDao

    companion object {
        private const val TAG = "CryptoCurrencyDatabase"

        var initDatabaseCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                Log.d(TAG, "onCreate() - version : ${db.version}")
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                Log.d(TAG, "onOpen() - version : ${db.version}")
            }
        }

//        var migrations: Array<Migration> = arrayOf()

        private var instance: CryptoCurrencyDatabase? = null

        fun get(): CryptoCurrencyDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(TestApp.instance, CryptoCurrencyDatabase::class.java, "crypto_currency_db").apply {
                    addCallback(initDatabaseCallback)
//                    addMigrations(*migrations) // 추후 필요하면 추가
                    fallbackToDestructiveMigration()
                }.build().also { instance = it }
            }
        }
    }
}