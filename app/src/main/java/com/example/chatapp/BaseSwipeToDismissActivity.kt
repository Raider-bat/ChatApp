package com.example.chatapp


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig

abstract class BaseSwipeToDismissActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isActivity()){
            val config = SlidrConfig.Builder()
                .scrimStartAlpha(0.2f)
                .scrimEndAlpha(0f)
                .build()
            Slidr.attach(this, config)
        }
    }


    abstract  fun isActivity(): Boolean


}