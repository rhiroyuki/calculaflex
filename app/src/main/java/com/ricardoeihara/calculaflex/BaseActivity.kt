package com.ricardoeihara.calculaflex

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ricardoeihara.calculaflex.utils.CalculaFlexTracker
import com.ricardoeihara.calculaflex.utils.ScreenMap

open class BaseActivity : AppCompatActivity() {

    open fun getScreenName(): String {
        return ScreenMap.getScreenNameBy(this)
    }

    override fun onStart() {
        super.onStart()
        CalculaFlexTracker.trackScreen(this, getScreenName())
    }
}

