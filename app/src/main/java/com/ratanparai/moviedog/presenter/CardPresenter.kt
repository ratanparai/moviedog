package com.ratanparai.moviedog.presenter

import android.util.Log
import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.ratanparai.moviedog.db.entity.Movie

class CardPresenter: Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        Log.d(TAG, "onCreateViewHolder")

        val cardView = ImageCardView(parent.context)

        cardView.isFocusable = true

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val movie = item as Movie
        val cardView = viewHolder.view as ImageCardView

        Log.d(TAG, "onBlindViewHolder")

        cardView.titleText = movie.title
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)

        Glide.with(viewHolder.view.context)
            .load(movie.cardImage)
            .centerCrop()
            .into(cardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        Log.d(TAG, "onUnbindViewHolder")

        val imageCardView = viewHolder.view as ImageCardView
        imageCardView.badgeImage = null
        imageCardView.mainImage = null
    }

    companion object {
        private const val TAG = "CardPresenter"

        private const val CARD_WIDTH = 313
        private const val CARD_HEIGHT = 500
    }
}