package com.example.unitconverter

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.unitconverter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up category spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = adapter
        }

        // Set default unit spinners
        updateUnitSpinners(0)

        // Handle category changes
        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateUnitSpinners(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Set up convert button
        binding.convertButton.setOnClickListener {
            performConversion()
        }
    }

    private fun updateUnitSpinners(categoryPosition: Int) {
        val unitArrayId = when (categoryPosition) {
            0 -> R.array.length_units
            1 -> R.array.weight_units
            2 -> R.array.temperature_units
            else -> R.array.length_units
        }

        ArrayAdapter.createFromResource(
            this,
            unitArrayId,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.fromUnitSpinner.adapter = adapter
            binding.toUnitSpinner.adapter = adapter
            binding.toUnitSpinner.setSelection(1)
        }
    }

    private fun performConversion() {
        binding.errorText.text = ""

        try {
            val inputStr = binding.inputValue.text.toString()
            if (inputStr.isEmpty()) {
                binding.errorText.text = "Please enter a value"
                return
            }

            val inputValue = inputStr.toDouble()
            val fromUnit = binding.fromUnitSpinner.selectedItem.toString()
            val toUnit = binding.toUnitSpinner.selectedItem.toString()

            if (fromUnit == toUnit) {
                binding.resultText.text = "%.2f %s = %.2f %s".format(inputValue, fromUnit, inputValue, toUnit)
                return
            }

            val category = binding.categorySpinner.selectedItem.toString()
            val result = when (category) {
                "Length" -> convertLength(inputValue, fromUnit, toUnit)
                "Weight" -> convertWeight(inputValue, fromUnit, toUnit)
                "Temperature" -> convertTemperature(inputValue, fromUnit, toUnit)
                else -> inputValue
            }

            binding.resultText.text = "%.2f %s = %.2f %s".format(inputValue, fromUnit, result, toUnit)

        } catch (e: NumberFormatException) {
            binding.errorText.text = "Please enter a valid number"
        }
    }

    private fun convertLength(value: Double, fromUnit: String, toUnit: String): Double {
        val inCm = when (fromUnit) {
            "Inch" -> value * 2.54
            "Foot" -> value * 30.48
            "Yard" -> value * 91.44
            "Mile" -> value * 160934.0
            "cm" -> value
            "km" -> value * 100000.0
            else -> value
        }

        return when (toUnit) {
            "Inch" -> inCm / 2.54
            "Foot" -> inCm / 30.48
            "Yard" -> inCm / 91.44
            "Mile" -> inCm / 160934.0
            "cm" -> inCm
            "km" -> inCm / 100000.0
            else -> value
        }
    }

    private fun convertWeight(value: Double, fromUnit: String, toUnit: String): Double {
        val inKg = when (fromUnit) {
            "Pound" -> value * 0.453592
            "Ounce" -> value * 0.0283495
            "Ton" -> value * 907.185
            "kg" -> value
            "g" -> value / 1000.0
            else -> value
        }

        return when (toUnit) {
            "Pound" -> inKg / 0.453592
            "Ounce" -> inKg / 0.0283495
            "Ton" -> inKg / 907.185
            "kg" -> inKg
            "g" -> inKg * 1000.0
            else -> value
        }
    }

    private fun convertTemperature(value: Double, fromUnit: String, toUnit: String): Double {
        val inCelsius = when (fromUnit) {
            "Celsius" -> value
            "Fahrenheit" -> (value - 32) / 1.8
            "Kelvin" -> value - 273.15
            else -> value
        }

        return when (toUnit) {
            "Celsius" -> inCelsius
            "Fahrenheit" -> (inCelsius * 1.8) + 32
            "Kelvin" -> inCelsius + 273.15
            else -> value
        }
    }
}