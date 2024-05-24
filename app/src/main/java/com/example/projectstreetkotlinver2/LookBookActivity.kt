package com.example.projectstreetkotlinver2

import android.graphics.Color
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class LookBookActivity : AppCompatActivity() {

    private lateinit var topSection: LinearLayout
    private lateinit var bottomSection: LinearLayout
    private lateinit var shoesSection: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lookbook)

        topSection = findViewById(R.id.topSection)
        bottomSection = findViewById(R.id.bottomSection)
        shoesSection = findViewById(R.id.shoesSection)

        val shirtItem: ImageView = findViewById(R.id.shirtItem)
        val pantsItem: ImageView = findViewById(R.id.pantsItem)
        val hatItem: ImageView = findViewById(R.id.hatItem)
        val shoesItem: ImageView = findViewById(R.id.shoesItem)
        val jacketItem: ImageView = findViewById(R.id.jacketItem)
        val scarfItem: ImageView = findViewById(R.id.scarfItem)

        // Установка слушателей для начала перетаскивания
        setTouchListener(shirtItem)
        setTouchListener(pantsItem)
        setTouchListener(hatItem)
        setTouchListener(shoesItem)
        setTouchListener(jacketItem)
        setTouchListener(scarfItem)

        // Установка слушателей для областей, в которые можно перетаскивать элементы
        setDragListener(topSection)
        setDragListener(bottomSection)
        setDragListener(shoesSection)
    }

    private fun setTouchListener(view: ImageView) {
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val shadowBuilder = View.DragShadowBuilder(v)
                v.startDragAndDrop(null, shadowBuilder, v, 0)
                v.visibility = View.INVISIBLE
                true
            } else {
                false
            }
        }
    }

    private fun setDragListener(view: LinearLayout) {
        view.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> true
                DragEvent.ACTION_DRAG_ENTERED -> {
                    v.setBackgroundColor(Color.LTGRAY)
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    v.setBackgroundColor(Color.TRANSPARENT)
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val draggedView = event.localState as View
                    val owner = draggedView.parent as LinearLayout
                    owner.removeView(draggedView)
                    val container = v as LinearLayout
                    container.addView(draggedView)
                    draggedView.visibility = View.VISIBLE
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    v.setBackgroundColor(Color.TRANSPARENT)
                    val draggedView = event.localState as View
                    draggedView.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }
    }
}
