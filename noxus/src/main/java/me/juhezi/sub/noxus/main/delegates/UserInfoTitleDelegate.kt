package me.juhezi.sub.noxus.main.delegates

import android.util.Log
import android.view.ViewGroup
import me.juhezi.slow_cut_base.ext.inflater
import me.juhezi.sub.noxus.base.adaptermanager.AbsBindingAdapterDelegate
import me.juhezi.sub.noxus.databinding.ItemUserInfoTitleBinding

class UserInfoTitleDelegate : AbsBindingAdapterDelegate<ItemUserInfoTitleBinding>() {

    override fun getModelType(): Class<String> = String::class.java
    override fun onCreateBinding(parent: ViewGroup): ItemUserInfoTitleBinding {
        return ItemUserInfoTitleBinding.inflate(parent.inflater(), parent, false)
    }

    override fun onBind(binding: ItemUserInfoTitleBinding, data: Any) {
        Log.i("Juhezi", "onBind: $data")
        (data as? String)?.let {
            binding.title.text = it
        }
    }
}