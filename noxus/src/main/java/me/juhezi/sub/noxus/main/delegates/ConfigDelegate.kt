package me.juhezi.sub.noxus.main.delegates

import android.view.View
import android.view.ViewGroup
import me.juhezi.slow_cut_base.ext.inflater
import me.juhezi.sub.noxus.base.adaptermanager.AbsBindingAdapterDelegate
import me.juhezi.sub.noxus.databinding.ItemConfigBinding
import me.juhezi.sub.noxus.ftpconfig.database.userconfig.UserConfigModel

class ConfigDelegate : AbsBindingAdapterDelegate<ItemConfigBinding>() {
    override fun onCreateBinding(parent: ViewGroup): ItemConfigBinding {
        return ItemConfigBinding.inflate(parent.inflater(), parent, false)
    }

    override fun getModelType(): Class<*> = UserConfigModel::class.java

    override fun onBind(binding: ItemConfigBinding, data: Any) {
        (data as? UserConfigModel)?.let {
            binding.desc.text = it.desc
            binding.key.text = it.key
            // TODO: "Boolean" const
            if ("Boolean" == it.type) {
                binding.value.visibility = View.GONE
                binding.btnSwitch.visibility = View.VISIBLE
                binding.btnSwitch.isChecked = it.value.toBoolean()
                binding.btnEdit.visibility = View.GONE
            } else {
                binding.value.visibility = View.VISIBLE
                binding.btnSwitch.visibility = View.GONE
                binding.btnEdit.visibility = View.VISIBLE
                binding.value.text = it.value.ifEmpty {
                    // TODO: From String.xml
                    "还没有设置呢~"
                }
            }
        }
    }
}