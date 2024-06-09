package com.example.projectstreetkotlinver2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

data class Event(
    val id: Int,
    val name: String,
    val image: String,
    val date: String,
    val description: String,
    val organizer: Int
)

data class Users(
    val id: Int,
    val username: String
)

class EventAdapter(private val events: List<Event>, private val users: List<User>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventImage: ImageView = itemView.findViewById(R.id.event_image)
        val eventName: TextView = itemView.findViewById(R.id.event_name)
        val eventDate: TextView = itemView.findViewById(R.id.event_date)
        val eventDescription: TextView = itemView.findViewById(R.id.event_description)
        val eventOrganizer: TextView = itemView.findViewById(R.id.event_organizer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        Picasso.get().load(event.image).into(holder.eventImage)
        holder.eventName.text = event.name
        holder.eventDate.text = event.date
        holder.eventDescription.text = event.description
        val organizer = users.find { it.id == event.organizer }
        holder.eventOrganizer.text = "Организатор: ${organizer?.username ?: event.organizer.toString()}"
    }

    override fun getItemCount(): Int {
        return events.size
    }
}
