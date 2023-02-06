package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = TEXT_SIZE
    }

    private var currentSweepAngle = 0f;
    private val valueAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = 1000;
        interpolator = LinearInterpolator()
        addUpdateListener { valueAnimator ->
            currentSweepAngle = valueAnimator.animatedValue as Float
            invalidate()
        }
        doOnEnd { buttonState = ButtonState.Completed }
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        if (old == ButtonState.Completed && new == ButtonState.Clicked) {
            valueAnimator.start()
            buttonState = ButtonState.Loading
        }
    }

    private var textColor = 0
    private var arcColor = 0
    private var buttonColor = 0
    private var progressColor = 0

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            arcColor = getColor(R.styleable.LoadingButton_arcColor, 0)
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            progressColor = getColor(R.styleable.LoadingButton_progressColor, 0)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        if (buttonState == ButtonState.Completed) {
            buttonState = ButtonState.Clicked
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = buttonColor
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        if (valueAnimator.isRunning) {
            // TODO: Extract to other methods for readibility

            paint.color = progressColor
            canvas.drawRect(0f,
                0f,
                valueAnimator.animatedFraction * widthSize.toFloat(),
                heightSize.toFloat(),
                paint
            )

            paint.color = arcColor
            canvas.drawArc(widthSize.toFloat() * 3f / 4f,
                heightSize.toFloat() / 4f,
                (widthSize.toFloat() * 3f / 4f) + heightSize.toFloat() / 2f,
                heightSize.toFloat() * 3f / 4f,
                0f,
                currentSweepAngle,
                true,
                paint)
        }

        paint.color = textColor
        canvas.drawText(
            if (buttonState == ButtonState.Loading) context.getString(R.string.button_loading) else context.getString(
                R.string.download),
            widthSize.toFloat() / 2,
            heightSize.toFloat() / 2 + TEXT_SIZE / 2f,
            paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    companion object {
        private const val TEXT_SIZE = 64f;
    }
}