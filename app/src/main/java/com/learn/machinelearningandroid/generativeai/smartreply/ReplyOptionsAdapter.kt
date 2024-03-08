package com.learn.machinelearningandroid.generativeai.smartreply

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.nl.smartreply.SmartReplySuggestion
import com.learn.machinelearningandroid.databinding.ItemOptionsSmartReplyBinding

class ReplyOptionsAdapter(
    private val onItemClickCallback: OnItemClickCallback
) : RecyclerView.Adapter<ReplyOptionsAdapter.ViewHolder>() {

    private val smartReplayOptions = ArrayList<SmartReplySuggestion>()

    interface OnItemClickCallback {
        fun onOptionClicked(optionText: String)
    }

    inner class ViewHolder(val binding: ItemOptionsSmartReplyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemOptionsSmartReplyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val optionText = smartReplayOptions[position].text
        holder.binding.tvSmartReplyOption.text = optionText
        holder.itemView.setOnClickListener {
            onItemClickCallback.onOptionClicked(optionText)
        }
    }

    override fun getItemCount(): Int {
        return smartReplayOptions.size
    }

    fun setReplyOptions(smartReplyOptions: List<SmartReplySuggestion>) {
        this.smartReplayOptions.clear()
        this.smartReplayOptions.addAll(smartReplyOptions)
        notifyDataSetChanged()
    }
}