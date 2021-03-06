package com.example.currencyconversion

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconversion.data.CurrencyView
import com.example.currencyconversion.databinding.CurrencyListItemBinding
import kotlin.random.Random

class CurrencyListAdapter :
    ListAdapter<CurrencyView, CurrencyListAdapter.CurrencyViewHolder>(DiffCallback) {


    companion object DiffCallback : DiffUtil.ItemCallback<CurrencyView>() {
        override fun areItemsTheSame(oldItem: CurrencyView, newItem: CurrencyView): Boolean {
            return oldItem.currencyCode == newItem.currencyCode
        }

        override fun areContentsTheSame(oldItem: CurrencyView, newItem: CurrencyView): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: CurrencyView, newItem: CurrencyView): Any? {
            return when {
                oldItem.convertedAmount != newItem.convertedAmount -> true
                else -> null
            }
        }

    }

    class CurrencyViewHolder(private var binding: CurrencyListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currencyView: CurrencyView) {
            binding.currencyAbbr.text = currencyView.currencyCode
            binding.currencyAbbr.setBackgroundColor(getBackgroundColor(binding.root.context))
            binding.convertedAmount.text = currencyView.convertedAmount
            binding.currencyFullName.text = currencyView.currencyFullName
        }

        fun bindConvertedAmount(currencyView: CurrencyView) {
            binding.convertedAmount.text = currencyView.convertedAmount
        }

        private fun getBackgroundColor(context: Context):Int {
            val currencyCodePalette =
                context.resources.obtainTypedArray(R.array.currency_code_palette)
            return currencyCodePalette.getColor(
                Random.nextInt(currencyCodePalette.length()),
                0
            ).also {
                currencyCodePalette.recycle()
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrencyViewHolder {
        return CurrencyViewHolder(CurrencyListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: CurrencyViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when {
            payloads.isEmpty() -> {
                super.onBindViewHolder(holder, position, payloads)
            }
            else -> {
                when {
                    payloads[0] == true -> holder.bindConvertedAmount(getItem(position))
                }
            }
        }
    }
}