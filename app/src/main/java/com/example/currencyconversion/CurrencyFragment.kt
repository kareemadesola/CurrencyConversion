package com.example.currencyconversion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconversion.data.CurrencyView
import com.example.currencyconversion.databinding.FragmentCurrencyBinding


/**
 * A simple [Fragment] subclass.
 * Use the [CurrencyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrencyFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel: CurrencyViewModel by viewModels()

    // Binding object instance corresponding to the fragment_currency.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private lateinit var _binding: FragmentCurrencyBinding
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter(context, android.R.layout.simple_spinner_item, listOf(1,2,3))

        viewModel.spinnerData.observe(viewLifecycleOwner) { currencyCodeList ->
            context?.let {
                ArrayAdapter(
                    it, android.R.layout.simple_spinner_item,
                    currencyCodeList
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.baseCurrencySpinner.adapter = adapter
                }
            }
        }
        val adapter = CurrencyListAdapter()
        binding.recyclerView.adapter = adapter

        viewModel.transformedAPIData.observe(viewLifecycleOwner){ currencyViewList ->
            currencyViewList.let {
                adapter.submitList(it)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.amountToConvert.doAfterTextChanged { text ->
            viewModel.setAmount(text?.toString())
        }

    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }


}