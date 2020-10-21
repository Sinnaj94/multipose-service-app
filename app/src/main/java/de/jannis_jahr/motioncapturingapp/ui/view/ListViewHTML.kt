package de.jannis_jahr.motioncapturingapp.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView
import android.widget.ListView
import de.jannis_jahr.motioncapturingapp.R

public class GainFocusWebView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.onTouchEvent(event)
    }
}