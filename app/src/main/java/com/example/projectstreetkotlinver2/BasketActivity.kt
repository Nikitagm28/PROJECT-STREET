package com.example.projectstreetkotlinver2

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso

class BasketActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var basketItemsContainer: LinearLayout
    private lateinit var emptyView: LinearLayout
    private lateinit var scrollView: ScrollView
    private lateinit var checkoutCard: CardView
    private lateinit var tvCheckoutItemCount: TextView
    private lateinit var tvCheckoutTotalPrice: TextView

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)

        sharedPreferences = getSharedPreferences("basket_prefs", Context.MODE_PRIVATE)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.navigation_basket

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_brands -> {
                    Toast.makeText(this, "Brands selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_basket -> {
                    Toast.makeText(this, "Basket selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_profile -> {
                    Toast.makeText(this, "Profile selected", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        basketItemsContainer = findViewById(R.id.basketItemsContainer)
        emptyView = findViewById(R.id.emptyView)
        scrollView = findViewById(R.id.scrollView)
        checkoutCard = findViewById(R.id.checkoutCard)
        tvCheckoutItemCount = findViewById(R.id.tvCheckoutItemCount)
        tvCheckoutTotalPrice = findViewById(R.id.tvCheckoutTotalPrice)

        loadBasketItems()
    }

    private fun loadBasketItems() {
        basketItemsContainer.removeAllViews()
        val basketItems = getBasketItems()

        if (basketItems.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            scrollView.visibility = View.GONE
            checkoutCard.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            scrollView.visibility = View.VISIBLE
            checkoutCard.visibility = View.VISIBLE

            var totalPrice = 0
            for (item in basketItems) {
                val itemView = layoutInflater.inflate(R.layout.product_item, null)
                val productImage = itemView.findViewById<ImageView>(R.id.product_image)
                val productTitle = itemView.findViewById<TextView>(R.id.product_title)
                val productBrand = itemView.findViewById<TextView>(R.id.product_brand)
                val productSize = itemView.findViewById<TextView>(R.id.product_size)
                val productPrice = itemView.findViewById<TextView>(R.id.product_price)
                val productDelete = itemView.findViewById<ImageView>(R.id.product_delete)

                productTitle.text = item.name
                productSize.text = item.selectedSize ?: ""
                productPrice.text = "${item.price} Р"
                totalPrice += item.price

                Picasso.get().load(item.image).into(productImage)

                productDelete.setOnClickListener {
                    removeItemFromBasket(item)
                }

                basketItemsContainer.addView(itemView)
            }
            updateCheckoutInfo(basketItems.size, totalPrice)
        }
    }

    private fun removeItemFromBasket(item: Product) {
        val basketItems = getBasketItems().toMutableList()
        basketItems.remove(item)
        saveBasketItems(basketItems)
        loadBasketItems()
    }

    private fun saveBasketItems(basketItems: List<Product>) {
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

    private fun updateCheckoutInfo(itemCount: Int, totalPrice: Int) {
        tvCheckoutItemCount.text = "$itemCount товара"
        tvCheckoutTotalPrice.text = "$totalPrice Р"
    }
}