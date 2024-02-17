package com.example.drawingapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.drawingapp.databinding.DrawingItemBinding

class DrawingAdapter(private var drawings: List<Drawing>, private var drawingViewModel: DrawingViewModel, private var lifecycle: LifecycleOwner,
                     private val onViewClick : (item: Drawing) -> Unit): RecyclerView.Adapter<DrawingAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: DrawingItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(DrawingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        holder.binding.drawingView2.setViewModel(drawingViewModel, lifecycle)
        return holder
    }

    override fun getItemCount(): Int = drawings.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val drawing = drawings[position]
        holder.binding.drawingView2.specifyDrawing(drawing)
        holder.binding.drawingView2.setOnClickListener {
            onViewClick(drawing)
        }
    }

    fun updateList(newList: List<Drawing>){
        drawings = newList
        notifyDataSetChanged()
    }
}