package com.example.projectstreetkotlinver2

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

data class LookBookItem(
    val imageUrl: String,
    val x: Float,
    val y: Float
)

class LookBookActivity : AppCompatActivity() {

    private lateinit var lookBookRecyclerView: RecyclerView
    private lateinit var dragArea: RelativeLayout
    private lateinit var clearLookbookButton: Button
    private lateinit var saveLookbookButton: Button
    private lateinit var myLooksButton: Button
    private val draggedItems = mutableListOf<LookBookItem>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lookbook)

        lookBookRecyclerView = findViewById(R.id.lookBookRecyclerView)
        dragArea = findViewById(R.id.drag_area)
        clearLookbookButton = findViewById(R.id.clear_lookbook_button)
        saveLookbookButton = findViewById(R.id.save_lookbook_button)
        myLooksButton = findViewById(R.id.my_looks_button)

        clearLookbookButton.setOnClickListener {
            clearLookBook()
        }

        saveLookbookButton.setOnClickListener {
            saveLookBook()
        }

        myLooksButton.setOnClickListener {
            val intent = Intent(this, MyLooksActivity::class.java)
            startActivity(intent)
        }

        val lookBookItems = getLookBookItems()
        lookBookRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        lookBookRecyclerView.adapter = LookBookAdapter(lookBookItems)

        setupDragListeners(dragArea)
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
        draggedItems.clear()
        dragArea.removeAllViews()
        Toast.makeText(this, "Образы очищены", Toast.LENGTH_SHORT).show()
    }

    private fun addDraggedItemToView(lookBookItem: LookBookItem) {
        val imageView = ImageView(this)
        val size = 300 // Задаем размер изображения
        imageView.layoutParams = RelativeLayout.LayoutParams(size, size).apply {
            leftMargin = lookBookItem.x.toInt() - size / 2
            topMargin = lookBookItem.y.toInt() - size / 2
        }
        Picasso.get().load(lookBookItem.imageUrl).into(imageView)
        dragArea.addView(imageView)
        imageView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    v.x = event.rawX - v.width / 2
                    v.y = event.rawY - v.height / 2
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val updatedItem = LookBookItem(lookBookItem.imageUrl, v.x + size / 2, v.y + size / 2)
                    draggedItems.remove(lookBookItem)
                    draggedItems.add(updatedItem)
                    true
                }
                else -> false
            }
        }
        draggedItems.add(lookBookItem)
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
                    val lookBookItem = LookBookItem(draggedItemTag, event.x, event.y)
                    addDraggedItemToView(lookBookItem)
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

    private fun saveLookBook() {
        val sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("USERNAME", null)

        if (username != null) {
            val lookbookData = hashMapOf(
                "username" to username,
                "items" to draggedItems.map { item ->
                    mapOf(
                        "imageUrl" to item.imageUrl,
                        "x" to item.x,
                        "y" to item.y
                    )
                },
                "image" to saveImage() // Сохраняем образ в виде картинки
            )

            db.collection("lookbooks")
                .add(lookbookData)
                .addOnSuccessListener {
                    Toast.makeText(this, "LookBook saved successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving LookBook: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImage(): String {
        val bitmap = Bitmap.createBitmap(dragArea.width, dragArea.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        dragArea.draw(canvas)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
    }

    inner class LookBookAdapter(private val items: List<Product>) :
        RecyclerView.Adapter<LookBookAdapter.ViewHolder>() {

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
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.lookbook_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product = items[position]
            Picasso.get().load(product.image).into(holder.imageView)
        }

        override fun getItemCount(): Int = items.size
    }
}
