package com.example.currencyconversion

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconversion.data.CurrencyApplication
import com.example.currencyconversion.databinding.ActivityMainBinding
import com.example.currencyconversion.databinding.FragmentCurrencyBinding
import kotlinx.coroutines.launch
import kotlin.math.log


/**
 * A simple [Fragment] subclass.
 * Use the [CurrencyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrencyFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel: CurrencyViewModel by activityViewModels{
        CurrencyViewModelFactory(
            (activity?.application as CurrencyApplication).database
                .currencyDao()
        )
    }

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
        context?.let {
            viewModel.currencyView.value?.map { currencyList ->
                currencyList.currencyCode
            }?.let { currencyCodeList ->
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item,
                    currencyCodeList
                ).also { adapter ->
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // Apply the adapter to the spinner
                    binding.baseCurrencySpinner.adapter = adapter
                    binding.baseCurrencySpinner.onItemSelectedListener = this
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CurrencyListAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        binding.amountToConvert.doAfterTextChanged {
            text -> viewModel.setAmount(text?.toString())
        }

        /*viewModel.allCurrency.observe(viewLifecycleOwner){
            Log.i("allCurrency", it.toString())
        }
        Log.i("allCurrency", viewModel.allCurrency.observe(viewLifecycleOwner){}.toString())
        Log.i("currencyView", viewModel.currencyView.value.toString())*/
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }


}