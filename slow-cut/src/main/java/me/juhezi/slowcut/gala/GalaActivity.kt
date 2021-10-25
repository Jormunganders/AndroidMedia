package me.juhezi.slowcut.gala

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_gala.*
import kotlinx.android.synthetic.main.activity_gala.progress_bar
import kotlinx.android.synthetic.main.repos_load_state_footer_view_item.*
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
            viewModel.getPagingData2().collect {
                repoAdapter.submitData(it)  // 调用这个函数之后， paging3 就开始工作了
            }
        }
        retry.setOnClickListener {
            repoAdapter.retry()
        }
        launch {
            repoAdapter.loadStateFlow.collect {
                val isListEmpty = it.refresh is LoadState.NotLoading && repoAdapter.itemCount == 0
                empty.isVisible = isListEmpty
                recycler_view.isVisible = !isListEmpty
                with(it.source) {
                    progress_bar.isVisible = refresh is LoadState.Loading
                    retry.isVisible = refresh is LoadState.Error

                    val errorState = append as? LoadState.Error
                        ?: prepend as? LoadState.Error
                        ?: append as? LoadState.Error
                        ?: it.prepend as? LoadState.Error
                    errorState?.let {
                        Toast.makeText(
                            this@GalaActivity,
                            "Error: ${it.error}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
    }

}