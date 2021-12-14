package com.example.currencyconversion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconversion.data.CurrencyApplication
import com.example.currencyconversion.databinding.FragmentCurrencyBinding
import com.example.currencyconversion.utils.Pref



class CurrencyFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel: CurrencyViewModel by viewModels {
        CurrencyViewModelFactory(
            (activity?.application as CurrencyApplication).database
                .currencyDao(), Pref(requireContext())
        )
    }

    // Binding object instance corresponding to the fragment_currency.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private var _binding: FragmentCurrencyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.amountToConvert.doAfterTextChanged { text ->
            viewModel.setAmount(text?.toString())
        }
        // Create an ArrayAdapter using the string array and a default spinner layout
        viewModel.spinnerData.observe(viewLifecycleOwner) { currencyCodeList ->
                ArrayAdapter(
                    requireContext(), android.R.layout.simple_spinner_item,
                    currencyCodeList
                ).also { adapter ->
                    adapter.setDropDownViewResource(R.layout.spinner_list)
                    binding.baseCurrencySpinner.adapter = adapter
                    binding.baseCurrencySpinner.onItemSelectedListener = this
                }

        }

        val adapter = CurrencyListAdapter()
        binding.recyclerView.adapter = adapter

        viewModel.transformedAPIData.observe(viewLifecycleOwner) { currencyViewList ->
            currencyViewList.let {
                adapter.submitList(it)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val spinnerSelectedValue = parent?.getItemAtPosition(position).toString()

        Log.i("spinner", spinnerSelectedValue)
        Toast.makeText(context, spinnerSelectedValue, Toast.LENGTH_SHORT).show()
        viewModel.setBaseCurrencyRate(spinnerSelectedValue)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}