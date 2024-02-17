package com.example.drawingapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.drawingapp.databinding.DrawingItemBinding

class DrawingAdapter(private var drawings: List<Drawing>, private var drawingViewModel: DrawingViewModel, private var lifecycle: LifecycleOwner,
                     private val onViewClick : (item: Drawing) -> Unit): RecyclerView.Adapter<DrawingAdapter.ViewHolder>() {

    /**
     * A ViewHolder class.
     */
    inner class ViewHolder(val binding: DrawingItemBinding): RecyclerView.ViewHolder(binding.root)

    /**
     * When the view is created, create views for each item.
     * @param parent - the recycler view? I'm assuming.
     * @param viewType - the Type of view for each element of the recycler view.
     * @return ViewHolder the view that will go into the recycler view.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(DrawingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        holder.binding.drawingView2.setViewModel(drawingViewModel, lifecycle)
        return holder
    }

    /**
     * Get the item count of the recycler view.
     * @return the Count of the items.
     */
    override fun getItemCount(): Int = drawings.size

    /**
     * What to run when the view is binded to the recycle view.
     * @param holder the holder of the view
     * @param position the position of the drawing, in Int.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val drawing = drawings[position]
        holder.binding.drawingView2.specifyDrawing(drawing)
        holder.binding.drawingView2.setOnClickListener {
            onViewClick(drawing)
        }
    }

    /**
     * Update the drawings list to the new list.
     * @param newList The list to replace the drawings list.
     */
    fun updateList(newList: List<Drawing>){
        drawings = newList
        notifyDataSetChanged()
    }
}