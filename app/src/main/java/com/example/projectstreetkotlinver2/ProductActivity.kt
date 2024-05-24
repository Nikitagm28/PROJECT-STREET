package com.example.projectstreetkotlinver2

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var product: Product
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var descriptionText: TextView
    private lateinit var selectedSize: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var addToCartButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        sharedPreferences = getSharedPreferences("basket_prefs", Context.MODE_PRIVATE)


        product = intent.getSerializableExtra("product") as Product


        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = ImageViewPagerAdapter(listOf(product.image))

        productName = findViewById(R.id.product_name)
        productPrice = findViewById(R.id.product_price)
        descriptionText = findViewById(R.id.description_text)
        addToCartButton = findViewById(R.id.add_to_cart_button)


        productName.text = product.name
        productPrice.text = "${product.price} Р"
        descriptionText.text = product.description


        val descriptionButton: Button = findViewById(R.id.description_button)
        descriptionButton.setOnClickListener {

            if (descriptionText.visibility == View.GONE) {
                descriptionText.visibility = View.VISIBLE
                descriptionButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.disup, 0) // Иконка, когда текст видим
            } else {
                descriptionText.visibility = View.GONE
                descriptionButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.disdown, 0) // Иконка, когда текст скрыт
            }
        }


        val sizeButtons = listOf<Button>(
            findViewById(R.id.sizeXS),
            findViewById(R.id.sizeS),
            findViewById(R.id.sizeM),
            findViewById(R.id.sizeL),
            findViewById(R.id.sizeXL)
        )

        for (button in sizeButtons) {
            button.setOnClickListener {
                selectSize(button, sizeButtons)
            }
        }


        addToCartButton.setOnClickListener {
            addToCart()
        }
    }

    private fun selectSize(selectedButton: Button, sizeButtons: List<Button>) {
        for (button in sizeButtons) {
            button.isSelected = false
            button.setBackgroundResource(R.drawable.button_border)
        }
        selectedButton.isSelected = true
        selectedButton.setBackgroundResource(R.drawable.button_border)
        selectedSize = selectedButton.text.toString()
    }

    private fun addToCart() {
        if (::selectedSize.isInitialized) {

            product.selectedSize = selectedSize
            saveProductToBasket(product)
            addToCartButton.text = "В корзине"
        }
    }

    private fun saveProductToBasket(product: Product) {
        val basketItems = getBasketItems().toMutableList()
        basketItems.add(product)
        val json = Gson().toJson(basketItems)
        sharedPreferences.edit().putString("basket_items", json).apply()
    }

    private fun getBasketItems(): List<Product> {
        val json = sharedPreferences.getString("basket_items", null)
        return if (json != null) {
            val type = object : TypeToken<List<Product>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

    class ImageViewPagerAdapter(private val images: List<String>) : RecyclerView.Adapter<ImageViewPagerAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.imageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Picasso.get().load(images[position]).into(holder.imageView)
        }

        override fun getItemCount(): Int = images.size
    }
}