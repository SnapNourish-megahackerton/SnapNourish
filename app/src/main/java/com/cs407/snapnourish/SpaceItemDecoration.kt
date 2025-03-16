package com.cs407.snapnourish

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val spanCount = 2
        val spacePx = space

        val column = position % spanCount

        outRect.left = spacePx - column * spacePx / spanCount
        outRect.right = (column + 1) * spacePx / spanCount

        if (position < spanCount) {
            outRect.top = spacePx
        }
        outRect.bottom = spacePx
    }
}
