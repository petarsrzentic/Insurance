package com.petarsrzentic.insurance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.petarsrzentic.insurance.data.Insurance
import kotlinx.android.synthetic.main.rec_dialog.view.*

class InsuranceRecyclerViewAdapter internal constructor(
    context: Context,
    private val te: TouchEvent
): RecyclerView.Adapter<InsuranceRecyclerViewAdapter.InsuranceViewHolder>() {

    interface TouchEvent {
        fun onClick(item: Insurance)
        fun onHold(item: Insurance)
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var objects = emptyList<Insurance>() // Cached copy of objects

    inner class InsuranceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsuranceViewHolder {
        val itemView = inflater.inflate(R.layout.rec_dialog, parent, false)
        return InsuranceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InsuranceViewHolder, position: Int) {
        val current = objects[position]
//        holder.itemView.textNumber.text = current.uid.toString()
        holder.itemView.textDate.text = current.date
        holder.itemView.textMsisdn.text = current.msisdn
        holder.itemView.textCategory.text = current.category
        holder.itemView.textHitno.text = current.hitno
        holder.itemView.textInsPrice.text = current.priceOfInsurance.toString()

        holder.itemView.setOnClickListener {
            te.onClick(current)
        }
        holder.itemView.setOnLongClickListener {
            te.onHold(current)
            return@setOnLongClickListener true
        }
    }


    internal fun setItems(items: List<Insurance>) {
        this.objects = items
        notifyDataSetChanged()
    }

    internal fun getItems(pos: Int): Insurance {
        return this.objects[pos]
    }

    override fun getItemCount() = objects.size

}