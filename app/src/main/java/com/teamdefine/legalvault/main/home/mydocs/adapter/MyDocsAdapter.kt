package com.teamdefine.legalvault.main.home.mydocs.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamdefine.legalvault.databinding.ItemSignRequestsBinding
import com.teamdefine.legalvault.main.home.mydocs.models.SignatureRequest
import com.teamdefine.legalvault.main.utility.Utility

class MyDocsAdapter(
    private val itemClicks: ItemClicks
) : RecyclerView.Adapter<MyDocsAdapter.ViewHolder>() {

    private val signatureList = mutableListOf<SignatureRequest>()

    interface ItemClicks {
        fun onItemClick(signature: SignatureRequest)
    }

    inner class ViewHolder(private val binding: ItemSignRequestsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val contractName = binding.contractNameTv
        val contractBw = binding.contractBwTv
        val contractTime = binding.contractDateTv
        val options = binding.optionsIv
        val tamperSeal = binding.tamperSeal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSignRequestsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSign = signatureList[position]
        holder.apply {
            contractName.text = currentSign.subject
            contractBw.text = when (currentSign.signatures.size) {
                2 -> "${currentSign.signatures[0]?.signer_name?.substringBefore(" ")} - ${
                    currentSign.signatures[1]?.signer_name?.substringBefore(
                        " "
                    )
                }"

                1 -> "${currentSign.signatures[0]?.signer_name?.substringBefore(" ")}"
                else -> "Dummy User"
            }
            contractTime.text = Utility.convertTimestampToDateInIST(currentSign.created_at)
            tamperSeal.visibility = if(currentSign.is_complete) View.VISIBLE else View.GONE
            options.setOnClickListener {
                itemClicks.onItemClick(currentSign)
            }
        }
    }

    override fun getItemCount(): Int {
        return signatureList.size
    }

    fun setData(data: ArrayList<SignatureRequest>) {
        signatureList.clear()
        signatureList.addAll(data)
        notifyDataSetChanged()
    }
}