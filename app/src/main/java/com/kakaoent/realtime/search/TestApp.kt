package com.kakaoent.realtime.search

import android.app.Application


class TestApp : Application() {
    companion object {
        private const val TAG = "MelonWearApp"

        @JvmStatic
        lateinit var instance: TestApp
            private set
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}