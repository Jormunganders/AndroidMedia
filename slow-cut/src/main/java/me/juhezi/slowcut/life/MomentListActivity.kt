package me.juhezi.slowcut.life

import android.os.Bundle
import android.util.Log
import me.juhezi.slow_cut_base.core.SlowCutActivity
import me.juhezi.slowcut.R
import me.juhezi.slowcut.model.life.MomentManager

class MomentListActivity : SlowCutActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_life_list)
        MomentManager.getMoments().compose(bindToLifecycle())
            .subscribe {
                Log.i("Juhezi", "onCreate: $it")
            }
    }

}