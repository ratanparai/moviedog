package com.ratanparai.moviedog.presenter

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.ratanparai.moviedog.db.entity.Movie

class DetailsDescriptionPresenter: AbstractDetailsDescriptionPresenter() {
    override fun onBindDescription(vh: ViewHolder?, item: Any?) {
        val movie = item as Movie

        vh?.title?.text = movie.title
        vh?.subtitle?.text = movie.productionYear.toString()
        vh?.body?.text = movie.description
    }

}