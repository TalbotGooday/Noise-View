package com.gapps.library

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.R.attr.start

class NoiseView : SurfaceView, SurfaceHolder.Callback {
	companion object{
		const val DEFAULT_FRAMES_SPEED = 50
	}

	private var drawThread: DrawThread? = null

	constructor(context: Context) : super(context) {
		init(null, 0)
	}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		init(attrs, 0)
	}

	constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
		init(attrs, defStyle)
	}

	fun setFramesSpeed(value: Int){
		drawThread?.framesSpeed = value
	}
	private fun init(attrs: AttributeSet?, defStyle: Int) {
		drawThread = DrawThread(holder, resources)

		holder.addCallback(this)

		attrs ?: return

		val arr = context?.obtainStyledAttributes(attrs, R.styleable.NoiseView)
		arr?.run {
			for (i in 0 until indexCount) {
				when (val oneAttr = getIndex(i)) {
					R.styleable.NoiseView_frames_speed -> {
						drawThread?.framesSpeed = getInt(oneAttr, DEFAULT_FRAMES_SPEED)
					}
				}

			}

			recycle()
		}

	}

	override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
	}

	override fun surfaceDestroyed(p0: SurfaceHolder?) {
		var retry = true
		drawThread?.setRunning(false)
		while (retry) {
			try {
				drawThread?.join()
				retry = false
			} catch (e: InterruptedException) {
				// если не получилось, то будем пытаться еще и еще
			}

		}
	}

	override fun surfaceCreated(p0: SurfaceHolder?) {
		drawThread?.setRunning(true)
		drawThread?.start()
	}
}
