package com.gondev.ably.subject.ui.favorites

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gondev.ably.subject.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private val viewModel: FavoritesViewModel by viewModels()

}