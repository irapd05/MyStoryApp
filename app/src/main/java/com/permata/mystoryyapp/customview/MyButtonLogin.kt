package com.permata.mystoryyapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.permata.mystoryyapp.R

class MyButtonLogin : AppCompatButton {

    private var txtColor: Int = 0
    private var enabledBackground: Drawable
    private var disabledBackground: Drawable

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        txtColor = ContextCompat.getColor(context, android.R.color.background_light)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.button_enabled_background) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.button_disabled_background) as Drawable
        setButtonText()
    }

    private fun setButtonText() {
        val buttonText = if (isEnabled) {
            context.getString(R.string.login)
        } else {
            context.getString(R.string.login)
        }
        text = buttonText
        setTextColor(txtColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isEnabled) enabledBackground else disabledBackground
        gravity = Gravity.CENTER
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        setButtonText()
    }
}

