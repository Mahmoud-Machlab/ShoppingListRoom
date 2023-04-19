package de.softdeveloper.shoppinglistroom.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import de.softdeveloper.shoppinglistroom.database.ShoppingMemo

class ShoppingMemoListAdapter : Adapter<ShoppingMemoListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    private var shoppingMemos: List<ShoppingMemo> = ArrayList()

    interface OnItemClickListener {
        fun onItemClick(memo: ShoppingMemo)
    }

    private var onItemClickListener:OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener){
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            android.R.layout.simple_list_item_multiple_choice,
            parent,
            false
        )
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentShoppingMemo = shoppingMemos[position]
        val textView = holder.itemView as TextView
        textView.text = currentShoppingMemo.toString()

        if(currentShoppingMemo.isSelected){
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textView.setTextColor(Color.rgb(175,175,175))
        }else{
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textView.setTextColor(Color.DKGRAY)
        }

        textView.setOnClickListener {
            if (onItemClickListener != null){
                onItemClickListener?.onItemClick(shoppingMemos[position])
            }
        }
    }

    override fun getItemCount() = shoppingMemos.size

    fun setShoppingMemos(memos: List<ShoppingMemo>) {
        shoppingMemos = memos
        notifyDataSetChanged()
    }
}