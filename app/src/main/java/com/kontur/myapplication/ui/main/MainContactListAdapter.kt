package com.kontur.myapplication.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kontur.myapplication.OnClickListener
import com.kontur.myapplication.databinding.ItemContactBinding
import com.kontur.myapplication.models.Contact

class MainContactListAdapter(private val clickListener: OnClickListener) :
    RecyclerView.Adapter<MainContactListAdapter.ContactListViewHolder>() {


    private val differ = AsyncListDiffer(this, DiffUtilItemCallback())


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListViewHolder {
        return ContactListViewHolder(
            ItemContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun getItems(): List<Contact> {
        return differ.currentList
    }

    override fun onBindViewHolder(holder: ContactListViewHolder, position: Int) {
        holder.bind(differ.currentList[position], clickListener)
    }

    fun updateList(newList: List<Contact>) {
        differ.submitList(newList)
    }

    override fun getItemCount(): Int = differ.currentList.size

    class ContactListViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Contact, clickListener: OnClickListener) {
            binding.root.setOnClickListener {
                clickListener.onClick(item)
            }

            binding.fullNameTextView.text = item.name
            binding.heightTextView.text = item.height.toString()
            binding.phoneNumberTextView.text = item.phone

        }
    }


    class DiffUtilItemCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }


}