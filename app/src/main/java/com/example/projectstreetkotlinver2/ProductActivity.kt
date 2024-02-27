package com.example.projectstreetkotlinver2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ProductActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private val imageList = listOf(R.drawable.zipka, R.drawable.zipka2) // Убедитесь, что эти изображения существуют в вашем каталоге drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        // Настройка ViewPager2 с адаптером для изображений
        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = ImageViewPagerAdapter(imageList)

        // Настройка TabLayout как индикатора для ViewPager2
        tabLayout = findViewById(R.id.tabDots)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Здесь можно оставить пустым, так как индикаторы будут управляться селектором
        }.attach()

        // Настройка кнопки и текстового поля для описания продукта
        val descriptionButton: Button = findViewById(R.id.description_button)
        val descriptionText: TextView = findViewById(R.id.description_text)

        descriptionButton.setOnClickListener {
            // Переключение видимости текста описания
            if (descriptionText.visibility == View.GONE) {
                descriptionText.visibility = View.VISIBLE
                descriptionButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.disup, 0) // Иконка, когда текст видим
            } else {
                descriptionText.visibility = View.GONE
                descriptionButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.disdown, 0) // Иконка, когда текст скрыт
            }
        }
    }

    class ImageViewPagerAdapter(private val images: List<Int>) : RecyclerView.Adapter<ImageViewPagerAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.imageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // Используйте layout inflater для инфляции вашего custom layout
            val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.imageView.setImageResource(images[position])
        }

        override fun getItemCount(): Int = images.size
    }

}
