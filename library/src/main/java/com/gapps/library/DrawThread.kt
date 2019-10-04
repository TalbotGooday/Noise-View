package com.gapps.library

import android.content.res.Resources
import android.graphics.*
import android.view.SurfaceHolder

internal class DrawThread(private val surfaceHolder: SurfaceHolder, resources: Resources, var frames: Array<Int>?) : Thread() {
	var framesSpeed: Int = NoiseView.DEFAULT_FRAMES_SPEED

	private var runFlag = false
	private val matrix: Matrix = Matrix()
	private var prevTime: Long = 0
	private var patternsShader: MutableList<BitmapShader> = mutableListOf()
	private val paint = Paint()
	private var counter = 0

	init {
		matrix.postScale(3.0f, 3.0f)
		matrix.postTranslate(100.0f, 100.0f)

		// сохраняем текущее время
		prevTime = System.currentTimeMillis()
		val framesArray = frames ?: arrayOf(
				R.drawable.noise,
				R.drawable.noise1,
				R.drawable.noise2,
				R.drawable.noise3,
				R.drawable.noise4
		)

		framesArray.forEach {
			val pattern = BitmapFactory.decodeResource(resources, it)
			val shader = BitmapShader(pattern, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)

			patternsShader.add(shader)
		}

	}

	fun setRunning(run: Boolean) {
		runFlag = run
	}

	override fun run() {
		var canvas: Canvas?
		while (runFlag) {
			val now = System.currentTimeMillis()
			val elapsedTime = now - prevTime
			if (elapsedTime > framesSpeed) {
				paint.shader = patternsShader[counter]
				counter++
				if (counter >= patternsShader.size) {
					counter = 0
				}

				prevTime = now
			}
			canvas = null
			try {
				// получаем объект Canvas и выполняем отрисовку
				canvas = surfaceHolder.lockCanvas()
				synchronized(surfaceHolder) {
					canvas.drawPaint(paint)

					surfaceHolder.unlockCanvasAndPost(canvas)
				}
			} catch (e: Exception) {
			} finally {
			}
		}
	}
}
