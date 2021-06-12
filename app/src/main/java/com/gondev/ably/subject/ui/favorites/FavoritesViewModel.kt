package com.gondev.ably.subject.ui.favorites

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondev.ably.subject.model.dto.ProductType
import com.gondev.ably.subject.repository.FavoritesRepository
import com.gondev.ably.subject.util.ItemClickListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
): ViewModel(), ItemClickListener<ProductType> {

    val favoritesList = favoritesRepository.favoritesList

    override fun onItemClicked(view: View, item: ProductType) {
        viewModelScope.launch {
            favoritesRepository.updateProduct(item.copy(favorite = !item.favorite))
        }
    }
}