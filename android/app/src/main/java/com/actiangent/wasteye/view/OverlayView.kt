package com.actiangent.wasteye.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.ColorUtils
import com.actiangent.wasteye.R
import com.actiangent.wasteye.model.Waste
import org.tensorflow.lite.task.vision.detector.Detection
import java.util.LinkedList
import kotlin.math.max

class OverlayView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private var results: List<Detection> = LinkedList<Detection>()
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var scaleFactor: Float = 1f

    private var textBounds = Rect()

    private val boundingBoxes: MutableList<RectF> = mutableListOf()

    private var onClickListener: OnClickListener? = null

    init {
        initPaints()
    }

    fun clear() {
        textPaint.reset()
        textBackgroundPaint.reset()
        boxPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        val boundingBoxColor = context.getColor(R.color.bounding_box_color)

        textBackgroundPaint.color = ColorUtils.setAlphaComponent(boundingBoxColor, 50)
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        boxPaint.color = boundingBoxColor
        boxPaint.strokeWidth = 4F
        boxPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        for (result in results) {
            val rawBoundingBox = result.boundingBox

            val top = rawBoundingBox.top * scaleFactor
            val bottom = rawBoundingBox.bottom * scaleFactor
            val left = rawBoundingBox.left * scaleFactor
            val right = rawBoundingBox.right * scaleFactor

            // Draw bounding box around detected objects
            val boundingBox = RectF(left, top, right, bottom)
            canvas.drawRoundRect(boundingBox, 8f, 8f, boxPaint)

            boundingBoxes.add(boundingBox)

            // Create text to display alongside detected objects
            val label = result.categories[0].label
            val type: Waste = try {
                Waste.valueOf(label.uppercase())
            } catch (e: IllegalArgumentException) {
                Waste.UNKNOWN
            }
            val typeText = when (type) {
                Waste.CARDBOARD -> context.getString(R.string.waste_type_cardboard)
                Waste.GLASS -> context.getString(R.string.waste_type_glass)
                Waste.METAL -> context.getString(R.string.waste_type_metal)
                Waste.PAPER -> context.getString(R.string.waste_type_paper)
                Waste.PLASTIC -> context.getString(R.string.waste_type_plastic)
                Waste.UNKNOWN -> context.getString(R.string.waste_type_unknown)
            }
            val drawableText = "$typeText ${String.format("%.2f", result.categories[0].score)}"

            // Draw rect behind display text
            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, textBounds)
            val textWidth = textBounds.width()
            val textHeight = textBounds.height()
            canvas.drawRoundRect(
                left,
                top,
                left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                8f,
                8f,
                textBackgroundPaint
            )

            // Draw text for detected object
            canvas.drawText(
                drawableText,
                left + (BOUNDING_RECT_TEXT_PADDING / 2),
                top + textBounds.height() + (BOUNDING_RECT_TEXT_PADDING / 2),
                textPaint
            )

            // Draw clickable circle
            canvas.drawCircle(
                boundingBox.centerX(),
                boundingBox.centerY(),
                BOUNDING_CIRCLE_RADIUS,
                textPaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                boundingBoxes.forEachIndexed { index, bounds ->
                    if (motionEvent.x <= (bounds.centerX() + (BOUNDING_CIRCLE_RADIUS / 2)) &&
                        motionEvent.x >= (bounds.centerX() - (BOUNDING_CIRCLE_RADIUS / 2)) &&
                        motionEvent.y <= ((bounds.centerY()) - (BOUNDING_CIRCLE_RADIUS)) &&
                        motionEvent.y >= ((bounds.centerY()) + (BOUNDING_CIRCLE_RADIUS))
                    ) {
                        val result = results[index]
                        val label = result.categories[0].label
                        val type: Waste = try {
                            Waste.valueOf(label.uppercase())
                        } catch (e: IllegalArgumentException) {
                            Waste.UNKNOWN
                        }

                        onClickListener?.onWasteClicked(type)
                    }
                }
            }
        }
        return true
    }

    override fun invalidate() {
        super.invalidate()

        boundingBoxes.clear()
    }

    fun setResults(
        detectionResults: MutableList<Detection>,
        imageHeight: Int,
        imageWidth: Int,
    ) {
        results = detectionResults

        // PreviewView is in FILL_START mode. So we need to scale up the bounding box to match with
        // the size that the captured images will be displayed.
        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight)
    }

    interface OnClickListener {
        fun onWasteClicked(type: Waste)
    }

    fun setOnclickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    companion object {
        private const val BOUNDING_CIRCLE_RADIUS = 20f
        private const val BOUNDING_RECT_TEXT_PADDING = 16
    }
}
