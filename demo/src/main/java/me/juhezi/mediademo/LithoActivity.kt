package me.juhezi.mediademo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.litho.Column
import com.facebook.litho.ComponentContext
import com.facebook.litho.LithoView
import com.facebook.litho.widget.Text
import com.facebook.yoga.YogaEdge

class LithoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val componentContext = ComponentContext(this)
        /*val component = */
        val component = Column.create(componentContext)
            .paddingDip(YogaEdge.ALL, 16f)
            .child(
                Text.create(componentContext)
                    .text("HelloWorld")
                    .textSizeSp(40f)
                    .build()
            )
            .child(
                Text.create(componentContext)
                    .text("Nice to meet you.")
                    .textSizeSp(20f)
            )
            .build()
        setContentView(LithoView.create(componentContext, component))
    }

}