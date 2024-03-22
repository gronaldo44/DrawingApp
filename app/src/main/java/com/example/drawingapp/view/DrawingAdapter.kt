package com.example.drawingapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.drawingapp.viewmodel.DrawingViewModel
import com.example.drawingapp.databinding.DrawingItemBinding
import com.example.drawingapp.model.Drawing

/**
 * Adapter for displaying a list of drawings in a RecyclerView.
 * This adapter manages the binding between the drawing data and the UI components.
 *
 * @property drawings The list of drawings to be displayed.
 * @property drawingViewModel The ViewModel responsible for managing drawing data and interactions.
 * @property lifecycle The lifecycle owner for observing LiveData in the ViewModel.
 * @property onViewClick A callback function invoked when a drawing item is clicked.
 */
class DrawingAdapter(private var drawings: List<Drawing>, private var drawingViewModel: DrawingViewModel, private var lifecycle: LifecycleOwner,
                     private val onViewClick : (item: Drawing) -> Unit): RecyclerView.Adapter<DrawingAdapter.ViewHolder>() {

    /**
     * ViewHolder class for managing individual items in the RecyclerView.
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