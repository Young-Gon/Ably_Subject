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
import com.gondev.ably.subject.model.database.entify.BannerEntity
import com.gondev.ably.subject.model.dto.BannerType
import com.gondev.ably.subject.model.dto.ListType
import com.gondev.ably.subject.model.dto.ProductType
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
        val adapter = MultiViewDataBindingListAdapter(
            viewLifecycleOwner,
            object : DiffUtil.ItemCallback<ListType>() {
                override fun areItemsTheSame(oldItem: ListType, newItem: ListType): Boolean {
                    val isSameHeaderItem = oldItem is BannerType &&
                        newItem is BannerType &&
                        oldItem.id == newItem.id

                    val isSameProductItem = oldItem is ProductType &&
                        newItem is ProductType &&
                        oldItem.id == newItem.id

                    return isSameHeaderItem || isSameProductItem
                }

                override fun areContentsTheSame(oldItem: ListType, newItem: ListType) =
                    oldItem == newItem

                override fun getChangePayload(oldItem: ListType, newItem: ListType): Any? {
                    return if (oldItem is ProductType &&
                        newItem is ProductType &&
                        oldItem.favorite != newItem.favorite
                    ) {
                        newItem.favorite 
                    }else null
                }
            }
        ) {
            // 베너
            addSubType<BannerType, BannerViewPagerBinding>(R.layout.item_header_viewpager, null) {
                vm = viewModel
                viewpager.adapter = DataBindingListAdapter<BannerEntity, BannerItemBinding>(
                    viewLifecycleOwner,
                    R.layout.item_banner,
                    BR.banner,
                    object : DiffUtil.ItemCallback<BannerEntity>() {
                        override fun areItemsTheSame(oldItem: BannerEntity, newItem: BannerEntity) =
                            oldItem.id == newItem.id

                        override fun areContentsTheSame(
                            oldItem: BannerEntity,
                            newItem: BannerEntity
                        ) =
                            oldItem == newItem
                    }
                )
            }

            // 제품 목록
            addSubType<ProductType, ProductItemBinding>(R.layout.item_product, BR.product) {
                favoriteClickListener = viewModel
            }
        }

        binding.recyclerview.adapter = adapter

        // 베너는 좌우 2칸
        (binding.recyclerview.layoutManager as GridLayoutManager).spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == 0) 2 else 1
                }
            }

        // 아이템 간격
        binding.recyclerview.addItemDecoration(
            GridSpacingItemDecorator(2, 12.dp(resources), true, 1)
        )

        // 데이터 삽입
        lifecycleScope.launch {
            viewModel.productList.collectLatest {
                adapter.submitData(it)
            }
        }

        // 네트워크 상태 감시
        adapter.addLoadStateListener { state ->
            binding.swipeRefreshLayout.isRefreshing = state.refresh is LoadState.Loading
        }

        // 리프레쉬
        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }
    }
}
