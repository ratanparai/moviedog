package com.ratanparai.moviedog.ui

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.leanback.app.GuidedStepFragment

class SelectMovieSourceDialogActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setBackgroundDrawable(ColorDrawable(Color.parseColor("#21272A")))

        if (savedInstanceState == null) {
            val fragment = SelectMovieSourceDialogFragment()
            GuidedStepFragment.addAsRoot(this, fragment, android.R.id.content)
        }

    }
}