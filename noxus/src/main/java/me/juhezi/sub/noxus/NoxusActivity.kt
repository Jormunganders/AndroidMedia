package me.juhezi.sub.noxus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.juhezi.sub.noxus.databinding.NoxusActivityBinding
import me.juhezi.sub.noxus.main.NoxusFragment

class NoxusActivity : AppCompatActivity() {

    private lateinit var binding: NoxusActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NoxusActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, NoxusFragment.newInstance())
                .commitNow()
        }
    }
}