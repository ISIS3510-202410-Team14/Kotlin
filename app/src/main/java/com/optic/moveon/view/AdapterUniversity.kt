package com.optic.moveon.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.optic.moveon.databinding.UniInfoItemBinding
import com.optic.moveon.model.entities.UniversityProperties

class AdapterUniversity(private val listener: UniversityPropertiesListener) : RecyclerView.Adapter<AdapterUniversity.ClassViewHolder>(){
    interface UniversityPropertiesListener{
        fun onUniversityClick(item: UniversityProperties, position: Int)
    }
    private var itemList = arrayListOf<UniversityProperties>()

    fun setItemList(list:List<UniversityProperties>){
        itemList.clear()
        itemList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val binding = UniInfoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.bind(itemList.get(position))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }



    inner class ClassViewHolder(private val binding: UniInfoItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }
        fun bind(item:UniversityProperties){
            binding.nombreItem.text = item.nombre
            binding.itemDescription.text = item.description
            if (item.selected){
                binding.itemDescription.visibility = View.VISIBLE
            }else{
                binding.itemDescription.visibility = View.GONE
            }

        }

        override fun onClick(p0: View?) {
            listener.onUniversityClick(itemList.get(adapterPosition),adapterPosition)
        }
    }
}