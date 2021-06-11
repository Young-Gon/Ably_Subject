package com.gondev.ably.subject.ui.home

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.TerminalSeparatorType
import androidx.paging.cachedIn
import androidx.paging.insertHeaderItem
import androidx.paging.map
import com.gondev.ably.subject.modlule.database.entify.*
import com.gondev.ably.subject.repository.ProductsRepository
import com.gondev.ably.subject.util.ItemClickListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class HomeViewModel  @Inject constructor(
    val productsRepository: ProductsRepository
) : ViewModel(), ItemClickListener<IProductEntity> {
    val banners by productsRepository::banners
    val productList = productsRepository.pager.flow.map { pagingData ->

        pagingData.map<ProductEntity, ListType> { ProductType(it)}
            .insertHeaderItem(TerminalSeparatorType.SOURCE_COMPLETE, BannerType())
    }.cachedIn(viewModelScope)

    /**
     * banner ViewPager의 현재 페이지
     */
    val currentViewPage= MutableLiveData(0)
    fun onPageSelected(position: Int){
        currentViewPage.value = position
    }

    /**
     * 좋아요 클릭!
     */
    override fun onItemClicked(view: View, product: IProductEntity) {
        TODO("Not yet implemented")

    }
}