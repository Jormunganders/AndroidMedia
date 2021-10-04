package me.juhezi.mediademo.letme

import android.graphics.drawable.AnimatedVectorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import me.juhezi.mediademo.R

class LetmeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letme)
        findViewById<ImageView>(R.id.image).setOnClickListener {
            ((it as ImageView).drawable as AnimatedVectorDrawable).start()
        }
    }
}