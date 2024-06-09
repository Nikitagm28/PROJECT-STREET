package com.example.projectstreetkotlinver2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectstreetkotlinver2.network.RetrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class FeedAdapter(private val feedItems: List<FeedLookBookItem>, private val saveLookBookItem: (FeedLookBookItem) -> Unit) :
    RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.profile_image)
        val profileName: TextView = view.findViewById(R.id.profile_name)
        val lookBookContainer: LinearLayout = view.findViewById(R.id.lookBookContainer)
        val likeButton: ImageView = view.findViewById(R.id.like_button)
        val likeCount: TextView = view.findViewById(R.id.like_count)
        val saveButton: ImageView = view.findViewById(R.id.save_button)

        init {
            likeButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val feedItem = feedItems[position]
                    handleLike(feedItem, position, it.context)
                }
            }

            saveButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val feedItem = feedItems[position]
                    saveLookBookItem(feedItem)
                    Toast.makeText(view.context, "Сохранено", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feedItem = feedItems[position]
        holder.profileName.text = feedItem.username

        if (feedItem.userProfileImage != null) {
            Glide.with(holder.itemView.context)
                .load(feedItem.userProfileImage)
                .into(holder.profileImage)
        } else {
            fetchUserProfileImage(feedItem.username, holder.profileImage)
        }

        holder.likeCount.text = feedItem.likeCount.toString()
        holder.lookBookContainer.removeAllViews()
        for (item in feedItem.lookBookItems) {
            val imageView = ImageView(holder.lookBookContainer.context)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                300
            )
            layoutParams.topMargin = 8
            imageView.layoutParams = layoutParams
            Picasso.get().load(item.imageUrl).into(imageView)
            holder.lookBookContainer.addView(imageView)
        }
    }

    override fun getItemCount(): Int = feedItems.size

    private fun fetchUserProfileImage(username: String, profileImageView: ImageView) {
        RetrofitClient.apiService.getUsers().enqueue(object : retrofit2.Callback<List<User>> {
            override fun onResponse(call: retrofit2.Call<List<User>>, response: retrofit2.Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                    val user = users?.find { it.username == username }
                    user?.let {
                        fetchUserProfile(it.id, profileImageView)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<User>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun fetchUserProfile(userId: Int, profileImageView: ImageView) {
        RetrofitClient.apiService.getProfiles().enqueue(object : retrofit2.Callback<List<Profile>> {
            override fun onResponse(call: retrofit2.Call<List<Profile>>, response: retrofit2.Response<List<Profile>>) {
                if (response.isSuccessful) {
                    val profiles = response.body()
                    val profile = profiles?.find { it.user == userId }
                    profile?.let {
                        Glide.with(profileImageView.context)
                            .load(it.image)
                            .into(profileImageView)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Profile>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun handleLike(feedItem: FeedLookBookItem, position: Int, context: Context) {
        val sharedPreferences = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("USERNAME", null)

        if (username != null) {
            val documentRef = db.collection("feed_lookbooks").document(feedItem.documentId)

            if (feedItem.likedBy.contains(username)) {
                // Удаление лайка
                feedItem.likedBy.remove(username)
                feedItem.likeCount--
            } else {
                // Добавление лайка
                feedItem.likedBy.add(username)
                feedItem.likeCount++
            }

            // Обновление документа в Firestore
            documentRef.update(
                mapOf(
                    "likeCount" to feedItem.likeCount,
                    "likedBy" to feedItem.likedBy
                )
            ).addOnSuccessListener {
                notifyItemChanged(position)
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
