package com.gondev.ably.subject.ui.home

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.TerminalSeparatorType
import androidx.paging.cachedIn
import androidx.paging.insertHeaderItem
import com.gondev.ably.subject.model.dto.BannerType
import com.gondev.ably.subject.model.dto.ListType
import com.gondev.ably.subject.model.dto.ProductType
import com.gondev.ably.subject.repository.FavoritesRepository
import com.gondev.ably.subject.repository.ProductsRepository
import com.gondev.ably.subject.util.ItemClickListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel(), ItemClickListener<ProductType> {

    val banners by productsRepository::banners

    val productList = productsRepository.pager.flow.map { pagingData ->
        (pagingData as PagingData<ListType>)
            .insertHeaderItem(TerminalSeparatorType.SOURCE_COMPLETE, BannerType())
    }.cachedIn(viewModelScope)

    /**
     * 좋아요 클릭!
     */
    override fun onItemClicked(view: View, item: ProductType) {
        viewModelScope.launch {
            favoritesRepository.updateProduct(item.copy(favorite = !item.favorite))
        }
    }
}