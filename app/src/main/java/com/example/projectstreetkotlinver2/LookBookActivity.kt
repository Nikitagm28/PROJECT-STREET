package com.example.projectstreetkotlinver2

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso

class LookBookActivity : AppCompatActivity() {

    private lateinit var lookBookRecyclerView: RecyclerView
    private lateinit var dragArea: RelativeLayout
    private lateinit var clearLookbookButton: Button
    private val draggedItems = mutableListOf<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lookbook)

        lookBookRecyclerView = findViewById(R.id.lookBookRecyclerView)
        dragArea = findViewById(R.id.drag_area)
        clearLookbookButton = findViewById(R.id.clear_lookbook_button)

        clearLookbookButton.setOnClickListener {
            clearLookBook()
        }

        val lookBookItems = getLookBookItems()
        lookBookRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        lookBookRecyclerView.adapter = LookBookAdapter(lookBookItems)

        setupDragListeners(dragArea)
        setupDragAreas()
    }

    private fun getLookBookItems(): List<Product> {
        val sharedPreferences = getSharedPreferences("basket_prefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("lookbook_items", null)
        return if (json != null) {
            val type = object : TypeToken<List<Product>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

    private fun clearLookBook() {
        // Очистка добавленных ImageView
        draggedItems.forEach { dragArea.removeView(it) }
        draggedItems.clear()

        // Очистка ImageView в dragArea
        val dragTargets = listOf(
            findViewById<ImageView>(R.id.dragged_item_1),
            findViewById<ImageView>(R.id.dragged_item_2),
            findViewById<ImageView>(R.id.dragged_item_3)
        )

        dragTargets.forEach { it.setImageResource(0) }

        Toast.makeText(this, "Образы очищены", Toast.LENGTH_SHORT).show()
    }

    private fun addDraggedItemToView(imageUrl: String) {
        val imageView = ImageView(this)
        imageView.layoutParams = RelativeLayout.LayoutParams(100, 100)
        Picasso.get().load(imageUrl).into(imageView)
        dragArea.addView(imageView)
        draggedItems.add(imageView)
    }

    private fun setupDragListeners(view: RelativeLayout) {
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
                    val draggedItemTag = event.localState as String
                    addDraggedItemToView(draggedItemTag)
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    v.setBackgroundColor(Color.TRANSPARENT)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupDragAreas() {
        val dragTargets = listOf(
            findViewById<ImageView>(R.id.dragged_item_1),
            findViewById<ImageView>(R.id.dragged_item_2),
            findViewById<ImageView>(R.id.dragged_item_3)
        )

        dragTargets.forEach { target ->
            setDragListener(target)
        }
    }

    private fun setDragListener(view: ImageView) {
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
                    val draggedItemTag = event.localState as String
                    Picasso.get().load(draggedItemTag).into(view)
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    v.setBackgroundColor(Color.TRANSPARENT)
                    true
                }
                else -> false
            }
        }
    }

    inner class LookBookAdapter(private val items: List<Product>) : RecyclerView.Adapter<LookBookAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.lookBookItemImage)

            init {
                view.setOnLongClickListener {
                    val item = items[adapterPosition]
                    val clipData = android.content.ClipData.newPlainText("", item.image)
                    val dragShadow = View.DragShadowBuilder(it)
                    it.startDragAndDrop(clipData, dragShadow, item.image, 0)
                    true
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.lookbook_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product = items[position]
            Picasso.get().load(product.image).into(holder.imageView)
        }

        override fun getItemCount(): Int = items.size
    }
}
