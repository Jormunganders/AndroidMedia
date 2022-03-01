package me.juhezi.slowcut

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import me.juhezi.slow_cut_base.core.SlowCutActivity

class RegisterActivity : SlowCutActivity() {

    companion object {
        private const val TAG = "RegisterActivity"
    }

    private var mAvatarUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        btn_register.setOnClickListener { register() }
        avatar.setOnClickListener {
            pick()
        }
    }

    private fun register() {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(et_email.text.toString(), et_password.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i(TAG, "register: 注册成功")
                } else {
                    Log.i(TAG, "register: 注册失败，" + it.exception?.message)
                    it.exception?.printStackTrace()
                }
            }
    }

    private fun pick() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = MediaStore.Images.Media.CONTENT_TYPE
        }
        startActivityForResult(intent, 0x123)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x123 && resultCode == RESULT_OK && data != null) {
            avatar.setImageURI(data.data)
            mAvatarUri = data.data
        }
    }

}