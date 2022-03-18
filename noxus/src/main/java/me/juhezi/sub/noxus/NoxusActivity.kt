package me.juhezi.sub.noxus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.juhezi.sub.noxus.ui.main.NoxusFragment

class NoxusActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noxus_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, NoxusFragment.newInstance())
                .commitNow()
        }
    }
}