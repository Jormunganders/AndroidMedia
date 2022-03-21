package me.juhezi.sub.noxus.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import me.juhezi.sub.noxus.R
import me.juhezi.sub.noxus.databinding.ItemConfigBinding
import me.juhezi.sub.noxus.databinding.ItemUserInfoBinding
import me.juhezi.sub.noxus.databinding.ItemUserInfoTitleBinding
import me.juhezi.sub.noxus.ftpconfig.database.userconfig.UserConfigModel
import javax.inject.Inject

class LiteConfigAdapter : RecyclerView.Adapter<LiteConfigAdapter.ViewHolder>() {

    private val mDataList = ArrayList<Any>()

    @SuppressLint("NotifyDataSetChanged")
    fun setDataList(dataList: List<Any>) {
        mDataList.clear()
        mDataList.addAll(dataList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = when (viewType) {
            HolderType.USER_INFO_TITLE.ordinal -> ItemUserInfoTitleBinding.inflate(
                inflater,
                parent,
                false
            ).root
            HolderType.USER_INFO.ordinal -> ItemUserInfoBinding.inflate(
                inflater,
                parent,
                false
            ).root
            HolderType.CONFIG.ordinal -> ItemConfigBinding.inflate(
                inflater,
                parent,
                false
            ).root
            else -> View(parent.context)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mDataList[position]
        when (getItemViewType(position)) {
            HolderType.USER_INFO_TITLE.ordinal -> {
                (data as? String)?.let {
                    holder.itemView.findViewById<TextView>(R.id.title).text = it
                }
            }
            HolderType.USER_INFO.ordinal -> {
                (data as? Pair<*, *>)?.let {
                    holder.itemView.findViewById<TextView>(R.id.user).text =
                        it.second.toString()
                    holder.itemView.findViewById<TextView>(R.id.name).text =
                        it.first.toString()
                }
            }
            HolderType.CONFIG.ordinal -> {
                (data as? UserConfigModel)?.let {
                    val desc = holder.itemView.findViewById<TextView>(R.id.desc)
                    val key = holder.itemView.findViewById<TextView>(R.id.key)
                    val value = holder.itemView.findViewById<TextView>(R.id.value)
                    val btnSwitch = holder.itemView.findViewById<SwitchCompat>(R.id.btn_switch)
                    val btnEdit = holder.itemView.findViewById<TextView>(R.id.btn_edit)
                    desc.text = it.desc
                    key.text = it.key
                    // TODO: "Boolean" const
                    if ("Boolean" == it.type) {
                        value.visibility = View.GONE
                        btnSwitch.visibility = View.VISIBLE
                        btnSwitch.isChecked = it.value.toBoolean()
                        btnEdit.visibility = View.GONE
                    } else {
                        value.visibility = View.VISIBLE
                        btnSwitch.visibility = View.GONE
                        btnEdit.visibility = View.VISIBLE
                        value.text = if (it.value.isEmpty()) {
                            // TODO: From String.xml
                            "还没有设置呢~"
                        } else {
                            it.value
                        }
                    }
                }
            }
            else -> Unit
        }
    }

    override fun getItemCount() = mDataList.size

    override fun getItemViewType(position: Int): Int {
        return when (mDataList[position]) {
            is String -> HolderType.USER_INFO_TITLE.ordinal
            is Pair<*, *> -> HolderType.USER_INFO.ordinal
            is UserConfigModel -> HolderType.CONFIG.ordinal
            else -> HolderType.UNKNOWN.ordinal
        }
    }

    enum class HolderType {
        UNKNOWN,
        USER_INFO_TITLE,
        USER_INFO,
        CONFIG,
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}