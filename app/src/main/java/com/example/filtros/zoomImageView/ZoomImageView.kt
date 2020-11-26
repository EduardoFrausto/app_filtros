package com.example.filtros.zoomImageView

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View.OnTouchListener
import androidx.appcompat.widget.AppCompatImageView


class ZoomImageView : AppCompatImageView {
    private var scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            state = STATE.ZOOM
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var tmpScale = detector.scaleFactor
            scale *= tmpScale
            if (scale > maxScale) {
                scale = maxScale
                tmpScale = maxScale / scale
            } else if (scale < minScale) {
                scale = minScale
                tmpScale = minScale / scale
            }
            if (origWidth * scale <= totalWidth
                || origHeight * scale <= totalHeight
            ) {
                mtx.postScale(
                    tmpScale, tmpScale, totalWidth / 2.toFloat(),
                    totalHeight / 2.toFloat()
                )
            } else {
                mtx.postScale(
                    tmpScale, tmpScale,
                    detector.focusX, detector.focusY
                )
            }
            evaluateTranslation()
            return true
        }
    }
    private val onGestureListener: GestureDetector.OnGestureListener = object :
        GestureDetector.OnGestureListener {
        override fun onDown(motionEvent: MotionEvent): Boolean = false
        override fun onShowPress(motionEvent: MotionEvent) {}
        override fun onSingleTapUp(motionEvent: MotionEvent): Boolean = false
        override fun onScroll(
            motionEvent: MotionEvent,
            motionEvent1: MotionEvent,
            v: Float,
            v1: Float
        ): Boolean = false

        override fun onLongPress(motionEvent: MotionEvent) {}
        override fun onFling(
            motionEvent: MotionEvent,
            motionEvent1: MotionEvent,
            v: Float,
            v1: Float
        ): Boolean = false
    }
    private var scaleDetector: ScaleGestureDetector = ScaleGestureDetector(context, scaleListener)
    private var gestureDetector: GestureDetector = GestureDetector(context, onGestureListener)
    var mtx: Matrix = Matrix()
    private var mtxValues: FloatArray = FloatArray(9)
    var state: STATE = STATE.NONE
    private val onTouchListener: OnTouchListener = OnTouchListener { view, event ->
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        val currentPoint = PointF(event.x, event.y)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                endPoint.set(currentPoint)
                startPoint.set(endPoint)
                state = STATE.DRAG
            }
            MotionEvent.ACTION_MOVE -> if (state == STATE.DRAG) {
                mtx.postTranslate(
                    processDragTrans(
                        currentPoint.x - endPoint.x,
                        totalWidth.toFloat(),
                        origWidth * scale
                    ),
                    processDragTrans(
                        currentPoint.y - endPoint.y,
                        totalHeight.toFloat(),
                        origHeight * scale
                    )
                )
                evaluateTranslation()
                endPoint[currentPoint.x] = currentPoint.y
            }
            MotionEvent.ACTION_POINTER_UP -> state = STATE.NONE
        }
        imageMatrix = mtx
        view.performClick()
    }

    var scale = 1f
    var minScale = 1f
    var maxScale = 4f
    var origWidth = 0f
    var origHeight = 0f
    var totalWidth = 0
    var totalHeight = 0
    private var endPoint = PointF()
    private var startPoint = PointF()

    constructor(ctx: Context) : super(ctx) {
        iniciar()
    }

    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs) {
        iniciar()
    }

    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        ctx,
        attrs,
        defStyleAttr
    ) {
        iniciar()
    }

    private fun iniciar() {
        super.setClickable(true)
        imageMatrix = mtx
        scaleType = ScaleType.MATRIX
        setOnTouchListener(onTouchListener)
    }


    private fun adapt() {
        scale = 1f
        val minScale: Float
        if (!(drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0)) {
            minScale = (totalWidth.toFloat() / drawable.intrinsicWidth.toFloat()).coerceAtMost(
                totalHeight.toFloat() / drawable.intrinsicHeight.toFloat()
            )
            mtx.setScale(minScale, minScale)
            var y = (totalHeight.toFloat()
                    - minScale * drawable.intrinsicHeight.toFloat())
            var x = (totalWidth.toFloat()
                    - minScale * drawable.intrinsicWidth.toFloat())
            y /= 2.toFloat()
            x /= 2.toFloat()
            mtx.postTranslate(x, y)
            origWidth = totalWidth - 2 * x
            origHeight = totalHeight - 2 * y
            imageMatrix = mtx
        }
    }

    fun evaluateTranslation() {
        mtx.getValues(mtxValues)
        val fixTransX = processTranslation(
            mtxValues[Matrix.MTRANS_X],
            totalWidth.toFloat(),
            origWidth * scale
        )
        val fixTransY = processTranslation(
            mtxValues[Matrix.MTRANS_Y],
            totalHeight.toFloat(),
            origHeight * scale
        )
        if (fixTransX != 0f || fixTransY != 0f) {
            mtx.postTranslate(fixTransX, fixTransY)
        }
    }

    private fun processTranslation(trans: Float, viewSize: Float, contentSize: Float): Float {
        val min: Float
        val max: Float
        when {
            contentSize <= viewSize -> {
                min = 0f
                max = viewSize - contentSize
            }
            else -> {
                min = viewSize - contentSize
                max = 0f
            }
        }
        return when {
            trans < min -> -trans + min
            trans > max -> -trans + max
            else -> 0F
        }
    }

    private fun processDragTrans(diff: Float, viewSize: Float, contentSize: Float): Float =
        if (contentSize <= viewSize) 0F else diff


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        totalWidth = MeasureSpec.getSize(widthMeasureSpec)
        totalHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (scale == 1f) {
            adapt()
        }
    }


    enum class STATE {
        NONE,
        ZOOM,
        DRAG
    }
}