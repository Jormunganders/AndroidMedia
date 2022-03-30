package me.juhezi.slow_cut_base.adaptermanager

interface DiffPredicate {
        fun areItemsTheSame(
            oldList: List<Any>,
            newList: List<Any>,
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean

        fun areContentsTheSame(
            oldList: List<Any>,
            newList: List<Any>,
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean
    }