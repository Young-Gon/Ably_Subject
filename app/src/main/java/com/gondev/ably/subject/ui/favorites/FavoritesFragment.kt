package com.gondev.ably.subject.ui.favorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import com.gondev.ably.subject.BR
import com.gondev.ably.subject.R
import com.gondev.ably.subject.databinding.FavoritesFragmentBinding
import com.gondev.ably.subject.databinding.ProductItemBinding
import com.gondev.ably.subject.model.dto.ProductType
import com.gondev.ably.subject.ui.home.GridSpacingItemDecorator
import com.gondev.ably.subject.util.DataBindingListAdapter
import com.gondev.ably.subject.util.dataBinding
import com.gondev.ably.subject.util.dp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private val binding: FavoritesFragmentBinding by dataBinding()
    private val viewModel: FavoritesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.recyclerview.adapter =
            DataBindingListAdapter<ProductType, ProductItemBinding>(
                viewLifecycleOwner,
                R.layout.item_product,
                BR.product,
                object : DiffUtil.ItemCallback<ProductType>() {
                    override fun areItemsTheSame(oldItem: ProductType, newItem: ProductType) =
                        oldItem.id == newItem.id

                    override fun areContentsTheSame(oldItem: ProductType, newItem: ProductType) =
                        oldItem == newItem
                }
            ) {
                favoriteClickListener = viewModel
            }

        binding.recyclerview.addItemDecoration(
            GridSpacingItemDecorator(
                2,
                12.dp(resources),
                true,
                0
            )
        )
    }
}
