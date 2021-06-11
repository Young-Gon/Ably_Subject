package com.gondev.ably.subject.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.gondev.ably.subject.BR
import com.gondev.ably.subject.R
import com.gondev.ably.subject.databinding.BannerItemBinding
import com.gondev.ably.subject.databinding.BannerViewPagerBinding
import com.gondev.ably.subject.databinding.HomeFragmentBinding
import com.gondev.ably.subject.databinding.ProductItemBinding
import com.gondev.ably.subject.modlule.database.entify.BannerEntity
import com.gondev.ably.subject.modlule.database.entify.BannerType
import com.gondev.ably.subject.modlule.database.entify.ListType
import com.gondev.ably.subject.modlule.database.entify.ProductType
import com.gondev.ably.subject.util.DataBindingListAdapter
import com.gondev.ably.subject.util.MultiViewDataBindingListAdapter
import com.gondev.ably.subject.util.dataBinding
import com.gondev.ably.subject.util.dp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding: HomeFragmentBinding by dataBinding()
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        val adapter = MultiViewDataBindingListAdapter(viewLifecycleOwner,
            object : DiffUtil.ItemCallback<ListType>() {
                override fun areItemsTheSame(oldItem: ListType, newItem: ListType): Boolean {
                    val isSameHeaderItem = oldItem is BannerType
                            && newItem is BannerType
                            && oldItem.id == newItem.id

                    val isSameProductItem = oldItem is ProductType
                            && newItem is ProductType
                            && oldItem.id == newItem.id

                    return isSameHeaderItem || isSameProductItem
                }

                override fun areContentsTheSame(oldItem: ListType, newItem: ListType) =
                    oldItem == newItem

            }) {
            // 베너
            subType<BannerType, BannerViewPagerBinding>(R.layout.item_header_viewpager, null){

                vm = viewModel
                viewpager.adapter = DataBindingListAdapter<BannerEntity, BannerItemBinding>(
                    viewLifecycleOwner,
                    R.layout.item_banner,
                    BR.banner,
                    object : DiffUtil.ItemCallback<BannerEntity>(){
                        override fun areItemsTheSame(oldItem: BannerEntity, newItem: BannerEntity)=
                            oldItem.id == newItem.id

                        override fun areContentsTheSame(oldItem: BannerEntity, newItem: BannerEntity) =
                            oldItem == newItem

                    }
                )
            }

            // 제품 목록
            subType<ProductType, ProductItemBinding>(R.layout.item_product, BR.product) {
                favoriteClickListener = viewModel
            }
        }

        binding.recyclerview.adapter = adapter

        (binding.recyclerview.layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return if(position==0) 2 else 1
            }
        }

        binding.recyclerview.addItemDecoration(GridSpacingItemDecorator(2, 12.dp(resources),true,1))

        lifecycleScope.launch {
            viewModel.productList.collectLatest {
                adapter.submitData(it)
            }
        }

        adapter.addLoadStateListener { state ->
            binding.swiperefreshlayout.isRefreshing = state.refresh is LoadState.Loading
        }

        binding.swiperefreshlayout.setOnRefreshListener {
            adapter.refresh()
        }
    }
}