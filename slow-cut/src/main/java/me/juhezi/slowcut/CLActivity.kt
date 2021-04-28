package me.juhezi.slowcut

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import kotlinx.android.synthetic.main.activity_cl.*
import me.juhezi.slow_cut_base.core.SlowCutActivity
import me.juhezi.slowcut.span.core.RoundSpan
import me.juhezi.slowcut.span.model.EmptySpan
import me.juhezi.slowcut.span.model.FileSpan

/**
 * 命令行页面
 */
class CLActivity : SlowCutActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cl)

        //--- demo ---
        val spannableString = SpannableString("########大家好大家好大家好大家好大家好大家好大家好大家好")
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_span_demo)
        val imageSpan = ImageSpan(this, bitmap)
//        spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        val roundTagSpan =
            RoundSpan(R.color.black, R.color.accent)
        spannableString.setSpan(roundTagSpan, 1, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(EmptySpan(), 6, 7, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(
            FileSpan("/storage/emulated/0/nebula/"),
            8,
            9,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            FileSpan("/storage/emulated/0/in.mp4"),
            9,
            10,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            FileSpan("/storage/emulated/0/out.mp4"),
            10,
            11,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            FileSpan("/storage/emulated/1/"),
            11,
            12,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        command_edittext.setText(spannableString)
    }
}