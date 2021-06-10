package com.gondev.ably.subject.ui.home

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gondev.ably.subject.modlule.database.entify.ProductEntity
import com.gondev.ably.subject.repository.ProductsRepository
import com.gondev.ably.subject.util.ItemClickListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HomeViewModel  @Inject constructor(
    val productsRepository: ProductsRepository
) : ViewModel(), ItemClickListener<ProductEntity> {
    val productList = productsRepository.pager.flow.cachedIn(viewModelScope)

    override fun onItemClicked(view: View, product: ProductEntity) {
        TODO("Not yet implemented")

    }
}