package com.gondev.ably.subject.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import com.gondev.ably.subject.R
import com.gondev.ably.subject.BR
import com.gondev.ably.subject.databinding.HomeFragmentBinding
import com.gondev.ably.subject.databinding.ProductItemBinding
import com.gondev.ably.subject.modlule.database.entify.ProductEntity
import com.gondev.ably.subject.util.MultiViewDataBindingListAdapter
import com.gondev.ably.subject.util.dataBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding: HomeFragmentBinding by dataBinding()
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        val adapter = MultiViewDataBindingListAdapter(viewLifecycleOwner,
            object : DiffUtil.ItemCallback<ProductEntity>() {
                override fun areItemsTheSame(
                    oldItem: ProductEntity,
                    newItem: ProductEntity
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: ProductEntity,
                    newItem: ProductEntity
                ): Boolean {
                    return oldItem == newItem
                }
            }) {
            subType<ProductEntity, ProductItemBinding>(R.layout.item_product, BR.product) {
                favoriteClickListener=viewModel
            }
        }

        binding.recyclerview.adapter = adapter

        lifecycleScope.launchWhenStarted {
            viewModel.productList.collectLatest {
                adapter.submitData(it)
                binding.swiperefreshlayout.isRefreshing = false
            }
        }

        binding.swiperefreshlayout.setOnRefreshListener{
            adapter.refresh()
        }
    }
}