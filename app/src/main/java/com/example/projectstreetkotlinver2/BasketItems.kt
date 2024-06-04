package com.example.projectstreetkotlinver2

class BasketItems {
    private val items: MutableList<Product> = mutableListOf()

    fun addItem(product: Product) {
        items.add(product)
    }

    fun getItems(): List<Product> {
        return items
    }

    companion object {
        private var instance: BasketItems? = null

        fun getInstance(): BasketItems {
            if (instance == null) {
                instance = BasketItems()
            }
            return instance!!
        }
    }
}