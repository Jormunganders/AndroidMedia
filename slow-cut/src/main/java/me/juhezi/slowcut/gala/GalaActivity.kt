package me.juhezi.slowcut.gala

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_gala.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.juhezi.slow_cut_base.core.SlowCutActivity
import me.juhezi.slowcut.R
import me.juhezi.slowcut.gala.data.PagingViewModel
import me.juhezi.slowcut.gala.ui.RepoAdapter
import me.juhezi.slowcut.gala.ui.ReposFooterAdapter

class GalaActivity : SlowCutActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(PagingViewModel::class.java)
    }

    private val repoAdapter = RepoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gala)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = repoAdapter.withLoadStateFooter(ReposFooterAdapter {
            repoAdapter.retry()
        })
        recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        lifecycleScope.launch {
            viewModel.getPagingData().collect {
                repoAdapter.submitData(it)  // 调用这个函数之后， paging3 就开始工作了
            }
        }
        repoAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading -> {
                    progress_bar.visibility = View.INVISIBLE
                    recycler_view.visibility = View.VISIBLE
                }
                is LoadState.Loading -> {
                    progress_bar.visibility = View.VISIBLE
                    recycler_view.visibility = View.INVISIBLE
                }
                is LoadState.Error -> {
                    val state = it.refresh as LoadState.Error
                    progress_bar.visibility = View.INVISIBLE
                    Toast.makeText(this, "Load Error: ${state.error.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}