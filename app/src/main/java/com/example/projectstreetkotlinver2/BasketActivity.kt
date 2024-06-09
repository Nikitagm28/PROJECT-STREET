package com.example.projectstreetkotlinver2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectstreetkotlinver2.BasicActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
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
    private lateinit var bottomNavigation: BottomNavigationView
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)

        sharedPreferences = getSharedPreferences("basket_prefs", Context.MODE_PRIVATE)

        basketItemsContainer = findViewById(R.id.basketItemsContainer)
        emptyView = findViewById(R.id.emptyView)
        scrollView = findViewById(R.id.scrollView)
        checkoutCard = findViewById(R.id.checkoutCard)
        tvCheckoutItemCount = findViewById(R.id.tvCheckoutItemCount)
        tvCheckoutTotalPrice = findViewById(R.id.tvCheckoutTotalPrice)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.navigation_basket
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, BasicActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_brands -> {
                    val intent = Intent(this, BrandsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_basket -> true
                R.id.navigation_profile -> {
                    val intent = Intent(this, SettingActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        loadBasketItems()
    }

    private fun loadBasketItems() {
        val username = getUsernameFromPrefs()
        if (username.isNullOrEmpty()) {
            showEmptyView()
            return
        }

        db.collection("basket_items").document(username).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val basketItemsJson = document.getString("items")
                    if (basketItemsJson != null) {
                        val basketItems: List<Product> = Gson().fromJson(basketItemsJson, object : TypeToken<List<Product>>() {}.type)
                        updateBasketItems(basketItems)
                    } else {
                        showEmptyView()
                    }
                } else {
                    showEmptyView()
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                showEmptyView()
            }
    }

    private fun showEmptyView() {
        emptyView.visibility = View.VISIBLE
        scrollView.visibility = View.GONE
        checkoutCard.visibility = View.GONE
    }

    private fun updateBasketItems(basketItems: List<Product>) {
        basketItemsContainer.removeAllViews()

        if (basketItems.isEmpty()) {
            showEmptyView()
        } else {
            emptyView.visibility = View.GONE
            scrollView.visibility = View.VISIBLE
            checkoutCard.visibility = View.VISIBLE

            for (item in basketItems) {
                val itemView = layoutInflater.inflate(R.layout.products_item, null)
                val productImage = itemView.findViewById<ImageView>(R.id.product_image)
                val productTitle = itemView.findViewById<TextView>(R.id.product_title)
                val productBrand = itemView.findViewById<TextView>(R.id.product_brand)
                val productSize = itemView.findViewById<TextView>(R.id.product_size)
                val productPrice = itemView.findViewById<TextView>(R.id.product_price)
                val productDelete = itemView.findViewById<ImageView>(R.id.product_delete)
                val productSelected = itemView.findViewById<CheckBox>(R.id.product_selected)
                val buttonDecrease = itemView.findViewById<Button>(R.id.button_decrease)
                val buttonIncrease = itemView.findViewById<Button>(R.id.button_increase)
                val quantityText = itemView.findViewById<TextView>(R.id.quantity_text)

                productTitle.text = item.name
                productSize.text = item.selectedSize ?: ""
                productPrice.text = "${item.price} Р"
                quantityText.text = item.quantity.toString()

                Picasso.get().load(item.image).into(productImage)

                productSelected.isChecked = true
                productSelected.setOnCheckedChangeListener { _, _ ->
                    updateCheckoutInfo()
                }

                productDelete.setOnClickListener {
                    removeItemFromBasket(item)
                }

                buttonDecrease.setOnClickListener {
                    var quantity = item.quantity
                    if (quantity > 1) {
                        quantity--
                        item.quantity = quantity
                        quantityText.text = quantity.toString()
                        updateCheckoutInfo()
                        saveBasketItems(basketItems)
                    }
                }

                buttonIncrease.setOnClickListener {
                    var quantity = item.quantity
                    quantity++
                    item.quantity = quantity
                    quantityText.text = quantity.toString()
                    updateCheckoutInfo()
                    saveBasketItems(basketItems)
                }

                basketItemsContainer.addView(itemView)
            }
            updateCheckoutInfo()
        }
    }

    private fun updateCheckoutInfo() {
        var itemCount = 0
        var totalPrice = 0

        for (i in 0 until basketItemsContainer.childCount) {
            val itemView = basketItemsContainer.getChildAt(i)
            val productSelected = itemView.findViewById<CheckBox>(R.id.product_selected)
            if (productSelected.isChecked) {
                val productPrice = itemView.findViewById<TextView>(R.id.product_price)
                val quantityText = itemView.findViewById<TextView>(R.id.quantity_text)
                val price = productPrice.text.toString().replace(" Р", "").toInt()
                val quantity = quantityText.text.toString().toInt()
                totalPrice += price * quantity
                itemCount += quantity
            }
        }

        tvCheckoutItemCount.text = "$itemCount товара"
        tvCheckoutTotalPrice.text = "$totalPrice Р"
    }

    private fun removeItemFromBasket(item: Product) {
        val basketItems = getBasketItems().toMutableList()
        basketItems.remove(item)
        saveBasketItems(basketItems)
        updateBasketItems(basketItems)
    }

    private fun saveBasketItems(basketItems: List<Product>) {
        val json = Gson().toJson(basketItems)
        sharedPreferences.edit().putString("basket_items", json).apply()

        val username = getUsernameFromPrefs()
        if (!username.isNullOrEmpty()) {
            db.collection("basket_items").document(username).set(mapOf("items" to json))
        }
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

    private fun getUsernameFromPrefs(): String? {
        val sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USERNAME", "")
    }
}
