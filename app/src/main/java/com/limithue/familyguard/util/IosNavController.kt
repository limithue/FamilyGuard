package com.limithue.familyguard.util

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView

/**
 * iOS 27 风格导航栏控制器：
 * - 大标题随滚动折叠为玻璃 bar 内的小标题
 * - 内容从玻璃 bar 下方柔和淡出（scroll edge effect）
 * - 右侧操作按钮以玻璃胶囊承载
 */
class IosNavController(
    private val navBar: View,
    private val compactTitle: TextView,
    private val largeTitle: TextView,
    private val actionButton: ImageButton
) {
    private var action: (() -> Unit)? = null
    private var density = 1f
    private var topInsetPx = 0

    fun setTopInset(px: Int) {
        topInsetPx = px
    }

    fun configure(title: String, iconRes: Int?, onAction: (() -> Unit)?) {
        largeTitle.text = title
        compactTitle.text = title
        largeTitle.alpha = 1f
        largeTitle.translationY = 0f
        compactTitle.alpha = 0f

        if (iconRes != null && onAction != null) {
            actionButton.setImageResource(iconRes)
            actionButton.visibility = View.VISIBLE
            action = onAction
        } else {
            actionButton.visibility = View.GONE
            action = null
        }
        actionButton.setOnClickListener { action?.invoke() }
    }

    /** 给当前页面预留导航栏高度，并绑定折叠动画 */
    fun attachScroll(root: View, density: Float) {
        this.density = density
        val scrollable = findScrollable(root) ?: return
        val inset = if (topInsetPx > 0) topInsetPx else (142f * density).toInt()
        val bottomInset = (72f * density).toInt()

        if (root === scrollable) {
            // 根即滚动容器：内容可滚入导航栏下方，形成 scroll edge 效果
            scrollable.setPadding(
                scrollable.paddingLeft, inset, scrollable.paddingRight, bottomInset
            )
            (scrollable as? ViewGroup)?.clipToPadding = false
        } else {
            // 根是普通容器（含固定头部）：整体下移，避免被导航栏遮挡
            root.setPadding(root.paddingLeft, inset, root.paddingRight, root.paddingBottom)
            scrollable.setPadding(
                scrollable.paddingLeft, scrollable.paddingTop,
                scrollable.paddingRight, bottomInset
            )
        }

        // 阻止子控件抢焦点导致页面启动时自动滚动
        (root as? ViewGroup)?.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        (scrollable as? ViewGroup)?.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS

        when (scrollable) {
            is NestedScrollView -> {
                scrollable.setOnScrollChangeListener { _, _, scrollY, _, _ -> onScroll(scrollY.toFloat()) }
                scrollable.post { scrollable.scrollTo(0, 0) }
                scrollable.postDelayed({
                    // 仅修正启动/数据加载造成的自动漂移，不清掉用户的主动滚动
                    if (scrollable.scrollY in 1..(420 * density).toInt()) {
                        scrollable.scrollTo(0, 0)
                        onScroll(0f)
                    }
                }, 260)
            }
            is RecyclerView -> {
                scrollable.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        onScroll(recyclerView.computeVerticalScrollOffset().toFloat())
                    }
                })
                scrollable.post { scrollable.scrollToPosition(0) }
                scrollable.postDelayed({ scrollable.scrollToPosition(0); onScroll(0f) }, 160)
            }
        }
    }

    private fun onScroll(scrollY: Float) {
        val progress = (scrollY / (76f * density)).coerceIn(0f, 1f)
        largeTitle.alpha = 1f - progress
        largeTitle.translationY = -18f * density * progress
        compactTitle.alpha = progress
        // 滚动后玻璃略微通透，让内容从下方透出（iOS 27 的 scroll edge 观感）
        navBar.background?.mutate()?.alpha = (255 - 28 * progress).toInt()
    }

    private fun findScrollable(view: View): View? {
        if (view is NestedScrollView || view is RecyclerView) return view
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val found = findScrollable(view.getChildAt(i))
                if (found != null) return found
            }
        }
        return null
    }
}
