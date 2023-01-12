package com.example.currencyexchangeapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchangeapp.R


internal enum class ViewState {
    GONE, RIGHT_VISIBLE
}

internal class ItemTouchController(val context: Context, buttonsActions: ItemTouchActions?) : Callback() {

    private var swipeBack = false
    private var buttonShowedState = ViewState.GONE
    private var buttonInstance: RectF? = null
    private var currentItemViewHolder: RecyclerView.ViewHolder? = null
    private var buttonsActions: ItemTouchActions? = null

    init {
        this.buttonsActions = buttonsActions
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, LEFT or RIGHT)
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = buttonShowedState != ViewState.GONE
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        var dX = dX
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonShowedState != ViewState.GONE) {
                if (buttonShowedState == ViewState.RIGHT_VISIBLE) dX =
                    dX.coerceAtMost(-buttonWidth)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            } else {
                setTouchListener(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
        if (buttonShowedState == ViewState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
        currentItemViewHolder = viewHolder
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack =
                event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                if (dX < -buttonWidth) buttonShowedState =
                    ViewState.RIGHT_VISIBLE
                if (buttonShowedState != ViewState.GONE) {
                    setTouchDownListener(
                        c,
                        recyclerView,
                        viewHolder,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    setItemsClickable(recyclerView, false)
                }
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchDownListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(
                    c,
                    recyclerView,
                    viewHolder,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchUpListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                super@ItemTouchController.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    0f,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                setItemsClickable(recyclerView, true)
                swipeBack = false
                if (buttonsActions != null && buttonInstance != null && buttonInstance!!.contains(
                        event.x,
                        event.y
                    )
                ) {
                    if (buttonShowedState == ViewState.RIGHT_VISIBLE) {
                        buttonsActions?.onRightClicked(viewHolder.adapterPosition)
                    }
                }
                buttonShowedState = ViewState.GONE
                currentItemViewHolder = null
            }
            false
        }
    }

    private fun setItemsClickable(recyclerView: RecyclerView, isClickable: Boolean) {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = isClickable
        }
    }

    private fun drawButtons(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
        val buttonWidthWithoutPadding = buttonWidth - 20
        val corners = 24f
        val itemView: View = viewHolder.itemView
        val p = Paint()
        val rightButton = RectF(
            itemView.right - buttonWidthWithoutPadding,
            itemView.top.toFloat(),
            itemView.right.toFloat(),
            itemView.bottom.toFloat()
        )
        p.color = Color.RED
        c.drawRoundRect(rightButton, corners, corners, p)
        drawText("DELETE", c, rightButton, p)
        buttonInstance = rightButton

    }

    private fun drawText(text: String, c: Canvas, button: RectF, p: Paint) {
        val textSize = 60f
        p.color = Color.WHITE
        p.isAntiAlias = true
        p.textSize = textSize
        p.typeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
        val textWidth: Float = p.measureText(text)
        c.drawText(text, button.centerX() - textWidth / 2, button.centerY() + p.textSize / 2, p)
    }

    fun onDraw(c: Canvas) {
        currentItemViewHolder?.let { drawButtons(c, it) }
    }

    companion object {
        private const val buttonWidth = 300f
    }
}

abstract class ItemTouchActions {
    abstract fun onRightClicked(position: Int)
}