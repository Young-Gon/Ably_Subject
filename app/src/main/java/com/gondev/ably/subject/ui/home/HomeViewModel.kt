package com.gondev.ably.subject.ui.home

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.map
import com.gondev.ably.subject.modlule.database.entify.ProductEntity
import com.gondev.ably.subject.repository.ProductsRepository
import com.gondev.ably.subject.util.ItemClickListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class HomeViewModel  @Inject constructor (
    val productsRepository: ProductsRepository
) : ViewModel(), ItemClickListener<ProductEntity> {
    val productList = productsRepository.pager.flow.map { pagingData ->
        pagingData.map { product ->
            Timber.v("item=$product")
            product
        }
    }

    override fun onItemClicked(view: View, item: ProductEntity) {
        TODO("Not yet implemented")
    }
}