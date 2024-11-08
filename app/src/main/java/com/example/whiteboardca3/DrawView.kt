package com.example.whiteboardca3
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val TOUCH_TOLERANCE = 4f
    private var mX = 0f
    private var mY = 0f
    private lateinit var mPath: Path

    // the Paint class encapsulates the color
    // and style information about
    // how to draw the geometries, text, and bitmaps
    private val mPaint = Paint()

    // ArrayList to store all the strokes
    // drawn by the user on the Canvas
    private val paths = ArrayList<Stroke>()
    private var currentColor = Color.GREEN
    private var strokeWidth = 20
    private lateinit var mBitmap: Bitmap
    private lateinit var mCanvas: Canvas
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)

    init {
        // smoothens the drawings of the user
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = currentColor
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.alpha = 0xff
    }

    // this method instantiate the bitmap and object
    fun init(height: Int, width: Int) {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap)
    }

    // sets the current color of stroke
    fun setColor(color: Int) {
        currentColor = color
    }

    // sets the stroke width
    fun setStrokeWidth(width: Int) {
        strokeWidth = width
    }

    fun undo() {
        // remove the most recent stroke if the paths list is not empty
        if (paths.isNotEmpty()) {
            paths.removeAt(paths.size - 1)
            invalidate()
        }
    }

    // this method returns the current bitmap
    fun save(): Bitmap {
        return mBitmap
    }

    // this is the main method where
    // the actual drawing takes place
    override fun onDraw(canvas: Canvas) {
        // save the current state of the canvas to draw the background
        canvas.save()

        // default color of the canvas
        val backgroundColor = Color.WHITE
        mCanvas.drawColor(backgroundColor)

        // iterate over the list of paths and draw each path on the canvas
        for (fp in paths) {
            mPaint.color = fp.color
            mPaint.strokeWidth = fp.strokeWidth.toFloat()
            mCanvas.drawPath(fp.path, mPaint)
        }
        canvas.drawBitmap(mBitmap, 0f, 0f, mBitmapPaint)
        canvas.restore()
    }

    // this method manages the touch response of the user on the screen

    // firstly, we create a new Stroke and add it to the paths list
    private fun touchStart(x: Float, y: Float) {
        mPath = Path()
        val fp = Stroke(currentColor, strokeWidth, mPath)
        paths.add(fp)

        // reset any curve or line from the path
        mPath.reset()

        // set the starting point of the line being drawn
        mPath.moveTo(x, y)

        // save the current coordinates of the finger
        mX = x
        mY = y
    }

    // smooth the turns by calculating the mean position between
    // the previous position and current position
    private fun touchMove(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    // draw the line to the end position
    private fun touchUp() {
        mPath.lineTo(mX, mY)
    }

    // handle touch events to draw on canvas
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
        }
        return true
    }
}
