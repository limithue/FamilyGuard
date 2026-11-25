package com.limithue.familyguard.ui.widget

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator

/**
 * iOS 27 风格开关：
 * - 胶囊轨道为上亮下暗的玻璃渐变，顶部带高光线
 * - 白色圆形滑块带高光环
 * - 按下时滑块放大为液态玻璃透镜（iOS 27 交互特征），松开回弹
 */
class IosSwitch @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var isChecked: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                animateTo(if (value) 1f else 0f)
            }
        }

    private var listener: ((Boolean) -> Unit)? = null

    fun setOnCheckedChangeListener(listener: (Boolean) -> Unit) {
        this.listener = listener
    }

    private var progress = 0f
    private var pressProgress = 0f
    private var animator: ValueAnimator? = null
    private var pressAnimator: ValueAnimator? = null

    private val evaluator = ArgbEvaluator()
    private val trackOffTop = Color.parseColor("#F2EDEDF0")
    private val trackOffBottom = Color.parseColor("#DCD9D9DE")
    private val trackOnTop = Color.parseColor("#5CE07F")
    private val trackOnBottom = Color.parseColor("#26B14A")

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#66FFFFFF")
    }
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        setShadowLayer(6f, 0f, 2.5f, Color.parseColor("#33000000"))
    }
    private val lensPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        isClickable = true
        setLayerType(LAYER_TYPE_SOFTWARE, thumbPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val density = resources.displayMetrics.density
        setMeasuredDimension((52 * density).toInt(), (32 * density).toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val density = resources.displayMetrics.density
        val radius = h / 2f

        // 轨道：上亮下暗的玻璃渐变
        trackPaint.shader = LinearGradient(
            0f, 0f, 0f, h,
            evaluator.evaluate(progress, trackOffTop, trackOnTop) as Int,
            evaluator.evaluate(progress, trackOffBottom, trackOnBottom) as Int,
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(RectF(0f, 0f, w, h), radius, radius, trackPaint)
        trackPaint.shader = null

        // 轨道顶部高光线
        rimPaint.strokeWidth = 1f * density
        canvas.drawRoundRect(
            RectF(0.6f * density, 0.8f * density, w - 0.6f * density, h - 0.8f * density),
            radius, radius, rimPaint
        )

        // 滑块：按下时膨胀成透镜
        val baseRadius = h / 2f - 2.5f * density
        val thumbRadius = baseRadius * (1f + 0.3f * pressProgress)
        val inset = baseRadius + 2.5f * density
        val cx = inset + progress * (w - 2 * inset)
        val cy = h / 2f

        canvas.drawCircle(cx, cy, thumbRadius, thumbPaint)

        // 透镜高光：左上入射 + 边缘色散
        if (pressProgress > 0f) {
            lensPaint.shader = RadialGradient(
                cx - thumbRadius * 0.35f, cy - thumbRadius * 0.4f, thumbRadius * 1.15f,
                intArrayOf(
                    Color.argb((150 * pressProgress).toInt(), 255, 255, 255),
                    Color.argb((60 * pressProgress).toInt(), 255, 255, 255),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawCircle(cx, cy, thumbRadius, lensPaint)
            lensPaint.shader = null
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> animatePress(1f)
            MotionEvent.ACTION_CANCEL -> animatePress(0f)
            MotionEvent.ACTION_UP -> {
                animatePress(0f)
                isChecked = !isChecked
                listener?.invoke(isChecked)
                performClick()
                return true
            }
        }
        return true
    }

    private fun animateTo(target: Float) {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(progress, target).apply {
            duration = 210
            interpolator = DecelerateInterpolator(1.6f)
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun animatePress(target: Float) {
        pressAnimator?.cancel()
        pressAnimator = ValueAnimator.ofFloat(pressProgress, target).apply {
            duration = if (target > pressProgress) 110 else 220
            addUpdateListener {
                pressProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        pressAnimator?.cancel()
        super.onDetachedFromWindow()
    }
}
