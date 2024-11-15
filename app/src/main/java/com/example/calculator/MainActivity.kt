package com.example.calculator

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.Build
import androidx.activity.ComponentActivity
import com.google.android.material.button.MaterialButton
import android.widget.TextView
import net.objecthunter.exp4j.ExpressionBuilder
import android.util.Log
import android.widget.GridLayout
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class MainActivity : ComponentActivity() {
    private lateinit var displayText: TextView
    private var lastNumeric = false
    private var stateError = false
    private var lastDot = false
    private var openParentheses = 0
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayText = findViewById(R.id.displayText)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val buttonContainer = findViewById<GridLayout>(R.id.buttonContainer)
        val scientificButtonContainer = findViewById<GridLayout>(R.id.scientificButtonContainer)

        for (i in 0 until scientificButtonContainer.childCount) {
            val button = scientificButtonContainer.getChildAt(i) as MaterialButton
            button.setOnClickListener {
                vibrateOnClick()
                onScientificButtonClick(button.text.toString())
            }
        }

        for (i in 0 until buttonContainer.childCount) {
            val button = buttonContainer.getChildAt(i) as MaterialButton

            button.setOnClickListener {
                vibrateOnClick()
                when (button.text.toString()) {
                    in "0123456789" -> onNumericClick(button.text.toString())
                    "," -> onNumericClick(button.text.toString())
                    "+", "-", "×", "÷" -> onOperatorClick(button.text.toString())
                    "=" -> onEqual()
                    "C" -> onClear()
                    "( )" -> onParentheses()
                    "%" -> onPercentage()
                    "+/-" -> onPlusMinus()
                }
            }
        }
    }

    private fun onScientificButtonClick(button: String) {
        if (stateError) {
            displayText.text = ""
            stateError = false
        }

        val currentText = displayText.text.toString()
        when (button) {
            "sin" -> {
                if (currentText.isNotEmpty() && lastNumeric) {
                    displayText.append("×sin(")
                    openParentheses++
                } else {
                    displayText.append("sin(")
                    openParentheses++
                }
            }
            "cos" -> {
                if (currentText.isNotEmpty() && lastNumeric) {
                    displayText.append("×cos(")
                    openParentheses++
                } else {
                    displayText.append("cos(")
                    openParentheses++
                }
            }
            "tan" -> {
                if (currentText.isNotEmpty() && lastNumeric) {
                    displayText.append("×tan(")
                    openParentheses++
                } else {
                    displayText.append("tan(")
                    openParentheses++
                }
            }
            "ln" -> {
                if (currentText.isNotEmpty() && lastNumeric) {
                    displayText.append("×ln(")
                    openParentheses++
                } else {
                    displayText.append("ln(")
                    openParentheses++
                }
            }
            "log" -> {
                if (currentText.isNotEmpty() && lastNumeric) {
                    displayText.append("×log(")
                    openParentheses++
                } else {
                    displayText.append("log(")
                    openParentheses++
                }
            }
            "1/x" -> {
                if (currentText.isNotEmpty()) {
                    val value = currentText.replace(",", ".").toDouble()
                    val result = 1 / value
                    displayText.text = result.toString().replace(".", ",")
                } else {
                    displayText.append("1/")
                }
            }
            "e^x" -> {
                if (currentText.isNotEmpty() && lastNumeric) {
                    displayText.append("×e^")
                } else {
                    displayText.append("e^")
                }
            }
            "x^y" -> {
                if (currentText.isNotEmpty() && lastNumeric) {
                    displayText.append("^")
                } else {
                    displayText.append("^")
                }
            }
            "x²" -> {
                if (currentText.isNotEmpty()) {
                    val value = currentText.replace(",", ".").toDouble()
                    val result = value * value
                    displayText.text = result.toString().replace(".", ",")
                } else {
                    displayText.append("^2")
                }
            }
            "|x|" -> {
                if (currentText.isNotEmpty()) {
                    val value = currentText.replace(",", ".").toDouble()
                    val result = abs(value)
                    displayText.text = result.toString().replace(".", ",")
                } else {
                    displayText.append("|")
                }
            }
            "π" -> displayText.append(PI.toString().replace(".", ","))
            "e" -> displayText.append(exp(1.0).toString().replace(".", ","))
            "sqrt" -> {
                if (currentText.isNotEmpty() && lastNumeric) {
                    displayText.append("×sqrt(")
                    openParentheses++
                } else {
                    displayText.append("sqrt(")
                    openParentheses++
                }
            }
        }
        lastNumeric = false
        lastDot = false
    }


    private fun vibrateOnClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(10)
        }
    }

    private fun onNumericClick(number: String) {
        if (stateError) {
            displayText.text = number
            stateError = false
        } else {
            if (displayText.text.toString() == "0" && number != ",") {
                displayText.text = number
            } else {
                displayText.append(number)
            }
        }
        lastNumeric = true
        Log.d("Calculator", "Numeric click: $number")
    }

    private fun onOperatorClick(operator: String) {
        Log.d("Calculator", "Operator click: $operator")
        if (lastNumeric && !stateError) {
            val currentText = displayText.text.toString()
            if (!isLastCharOperator(currentText)) {
                displayText.append(when(operator) {
                    "×" -> "×"
                    "÷" -> "/"
                    else -> operator
                })
                lastNumeric = false
                lastDot = false
            }
        } else if (operator == "-" && displayText.text.toString().isEmpty()) {
            displayText.append(operator)
        }
    }

    private fun isLastCharOperator(str: String): Boolean {
        if (str.isEmpty()) return false
        return when (str.last()) {
            '+', '-', '*', '/', '×', '÷' -> true
            else -> false
        }
    }

    private fun onClear() {
        displayText.text = ""
        lastNumeric = false
        stateError = false
        lastDot = false
        openParentheses = 0
        Log.d("Calculator", "Clear clicked")
    }

    private fun onParentheses() {
        if (stateError) {
            displayText.text = ""
            stateError = false
        }

        val currentText = displayText.text.toString()
        if (currentText.isEmpty() || !lastNumeric) {
            displayText.append("(")
            openParentheses++
        } else if (openParentheses > 0) {
            displayText.append(")")
            openParentheses--
        }
        Log.d("Calculator", "Parentheses clicked, open count: $openParentheses")
    }

    private fun onPercentage() {
        if (lastNumeric && !stateError) {
            try {
                var value = displayText.text.toString()
                value = value.replace(",", ".")
                val percentResult = value.toDouble() / 100
                displayText.text = percentResult.toString().replace(".", ",")
                lastDot = true
            } catch (e: Exception) {
                displayText.text = "Error"
                stateError = true
            }
        }
        Log.d("Calculator", "Percentage clicked")
    }

    private fun onPlusMinus() {
        if (lastNumeric && !stateError) {
            try {
                var value = displayText.text.toString()
                value = value.replace(",", ".")
                val negatedResult = -value.toDouble()
                displayText.text = negatedResult.toString().replace(".", ",")
            } catch (e: Exception) {
                displayText.text = "Error"
                stateError = true
            }
        }
        Log.d("Calculator", "Plus/Minus clicked")
    }

    private fun onEqual() {
        Log.d("Calculator", "Equal clicked")
        if (!stateError) {
            try {
                var expression = displayText.text.toString()

                while (openParentheses > 0) {
                    expression += ")"
                    openParentheses--
                }

                expression = expression.replace("×", "*")
                expression = expression.replace("÷", "/")
                expression = expression.replace(",", ".")

                expression = expression.replace("ln(", "log(").replace(")", ")/log(e)")

                Log.d("Calculator", "Evaluating expression: $expression")

                val result = ExpressionBuilder(expression)
                    .build()
                    .evaluate()

                val formattedResult = if (result == result.toLong().toDouble()) {
                    result.toLong().toString()
                } else {
                    String.format("%.8f", result)
                        .trimEnd('0')
                        .trimEnd('.')
                        .replace(".", ",")
                }

                displayText.text = formattedResult
                lastNumeric = true
                stateError = false
                lastDot = formattedResult.contains(",")
                openParentheses = 0

                Log.d("Calculator", "Result: $formattedResult")
            } catch (e: Exception) {
                displayText.text = "Error"
                stateError = true
                lastNumeric = false
                Log.e("Calculator", "Calculation error", e)
            }
        }
    }
}