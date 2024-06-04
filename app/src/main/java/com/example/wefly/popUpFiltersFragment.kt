package com.example.wefly

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.RangeSlider
import java.text.NumberFormat
import java.util.Currency

class popUpFiltersFragment : DialogFragment() {

    private lateinit var rangeSliderBudget: RangeSlider
    private lateinit var rangeSliderPartecipanti: RangeSlider

    private lateinit var checkBox1: CheckBox
    private lateinit var checkBox2: CheckBox
    private lateinit var checkBox3: CheckBox
    private lateinit var checkBox4: CheckBox

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("popUpFiltersPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pop_up_filters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Save button
        val saveFiltersBtn = view.findViewById<MaterialButton>(R.id.saveFilters_btn)
        saveFiltersBtn.setOnClickListener {
            saveState()
            dismiss()
        }

        // Filters parameters
        // Checkboxes
        checkBox1 = view.findViewById(R.id.affinita1)
        checkBox2 = view.findViewById(R.id.affinita2)
        checkBox3 = view.findViewById(R.id.affinita3)
        checkBox4 = view.findViewById(R.id.affinita4)

        // Set the slider
        rangeSliderBudget = view.findViewById(R.id.sliderRange)
        rangeSliderPartecipanti = view.findViewById(R.id.sliderRange2)

        // Restore state
        restoreState()

        // Format the display values
        rangeSliderBudget.setLabelFormatter { value: Float ->
            val format = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 0
            format.currency = Currency.getInstance("EUR")
            format.format(value.toDouble())
        }

        // To access the start and end values of the RangeSlider
        rangeSliderBudget.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            val startValueBudget = values[0]
            val endValueBudget = values[1]
        }

        rangeSliderPartecipanti.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            val startValuePartecipanti = values[0]
            val endValuePartecipanti = values[1]
        }
    }

    private fun saveState() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("affinita1", checkBox1.isChecked)
        editor.putBoolean("affinita2", checkBox2.isChecked)
        editor.putBoolean("affinita3", checkBox3.isChecked)
        editor.putBoolean("affinita4", checkBox4.isChecked)
        editor.putFloat("budgetStart", rangeSliderBudget.values[0])
        editor.putFloat("budgetEnd", rangeSliderBudget.values[1])
        editor.putFloat("partecipantiStart", rangeSliderPartecipanti.values[0])
        editor.putFloat("partecipantiEnd", rangeSliderPartecipanti.values[1])
        editor.apply()
    }

    private fun restoreState() {
        checkBox1.isChecked = sharedPreferences.getBoolean("affinita1", false)
        checkBox2.isChecked = sharedPreferences.getBoolean("affinita2", false)
        checkBox3.isChecked = sharedPreferences.getBoolean("affinita3", false)
        checkBox4.isChecked = sharedPreferences.getBoolean("affinita4", false)
        rangeSliderBudget.values = listOf(
            sharedPreferences.getFloat("budgetStart", rangeSliderBudget.valueFrom),
            sharedPreferences.getFloat("budgetEnd", rangeSliderBudget.valueTo)
        )
        rangeSliderPartecipanti.values = listOf(
            sharedPreferences.getFloat("partecipantiStart", rangeSliderPartecipanti.valueFrom),
            sharedPreferences.getFloat("partecipantiEnd", rangeSliderPartecipanti.valueTo)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveState()
    }
}
