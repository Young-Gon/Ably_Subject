package com.gondev.ably.subject.util

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.ListenerUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.gondev.ably.subject.R


@BindingAdapter("hasFixedSize")
fun RecyclerView.hasFixedSize(fix: Boolean) {
    setHasFixedSize(fix)
}

fun interface ItemClickListener<T> {
    fun onItemClicked(view: View, item: T)
}

@BindingAdapter("items")
fun <T> RecyclerView.setItems(items: List<T>?) {
    (this.adapter as? ListAdapter<T, *>)?.run {
        submitList(items)
    }
}

@BindingAdapter("items")
fun <T> ViewPager2.setItems(items: List<T>?) {
    (this.adapter as? ListAdapter<T, *>)?.run {
        submitList(items)
    }
}

@BindingAdapter("onPageScrollStateChanged")
fun ViewPager2.setOnPageScrolledListener(onPageScrollStateChangedListener: OnPageScrolledListener) {
    val onPageChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            onPageScrollStateChangedListener.onPageSelected(position)
        }
    }
    ListenerUtil.trackListener<ViewPager2.OnPageChangeCallback>(
        this,
        onPageChangeListener, R.id.currentItemSelectedListener
    )?.let {
        unregisterOnPageChangeCallback(it)
    }

    registerOnPageChangeCallback(onPageChangeListener)
}

fun interface OnPageScrolledListener {
    fun onPageSelected(position: Int)
}


@BindingAdapter("imgUrl")
fun ImageView.loadThumbnail(src: String?) {
    if (src == null) {
        return
    }

    var glide = Glide.with(context).load(src)

    if (src.endsWith("gif"))
        glide = glide.optionalTransform(
            WebpDrawable::class.java, WebpDrawableTransformation(
                CenterCrop()
            )
        )

    glide.apply(getGlideRequestOption(src))
        //.transform(FitCenter(), RoundedCorners(30))
        .into(this)
}

/**
 * Best strategy to load images using Glide
 * https://android.jlelse.eu/best-strategy-to-load-images-using-glide-image-loading-library-for-android-e2b6ba9f75b2
 */
fun getGlideRequestOption(imageName: String) =
    RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .signature(ObjectKey(imageName))
        //.override(1024, 2048)

@BindingAdapter("checked")
fun Button.setCheckedItem(checked: Boolean) {
    this.isSelected = checked
}

fun Int.dp(res: Resources): Int {
    val resultPix = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        res.displayMetrics
    )
    return resultPix.toInt()
}