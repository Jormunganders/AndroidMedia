package me.juhezi.sub.noxus.main.delegates

import android.view.ViewGroup
import me.juhezi.slow_cut_base.ext.inflater
import me.juhezi.sub.noxus.base.adaptermanager.AbsBindingAdapterDelegate
import me.juhezi.sub.noxus.databinding.ItemUserInfoBinding

class UserInfoDelegate : AbsBindingAdapterDelegate<ItemUserInfoBinding>() {
    override fun onCreateBinding(parent: ViewGroup): ItemUserInfoBinding {
        return ItemUserInfoBinding.inflate(parent.inflater(), parent, false)
    }

    override fun getModelType(): Class<*> = Pair::class.java

    override fun onBind(binding: ItemUserInfoBinding, data: Any) {
        (data as? Pair<*, *>)?.let {
            binding.name.text = it.first.toString()
            binding.user.text = it.second.toString()
        }
    }
}