package com.example.android.politicalpreparedness.election.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ViewholderElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        //TODO: Bind ViewHolder
    }


    //TODO: Add companion object to inflate ViewHolder (from)
}

//TODO: Create ElectionViewHolder
class ElectionViewHolder(private val binding: ViewholderElectionBinding): RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: View): ElectionViewHolder {
            return ElectionViewHolder(ViewholderElectionBinding.bind(parent))
        }
    }
}

object ElectionDiffCallback: DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        TODO("Not yet implemented")
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        TODO("Not yet implemented")
    }

}

class ElectionListener(val clickListener: (election: Election) -> Unit) {
    fun onClick(election: Election) = clickListener(election)
}