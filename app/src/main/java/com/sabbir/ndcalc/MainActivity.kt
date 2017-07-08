package com.sabbir.ndcalc

import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayout
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.Button
import com.udojava.evalex.Expression
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val custom_font = Typeface.createFromAsset(assets, "CrimsonText-Roman.ttf")
        textview_expression.typeface = custom_font
        textview_result.typeface = custom_font
        val gridLayout = findViewById(R.id.grid_container) as GridLayout
        (0..gridLayout.childCount - 1)
                .map { gridLayout.getChildAt(it) as Button }
                .forEach { it.typeface = custom_font }

        textview_expression.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                if (textview_expression.lineCount == 2) {
                    textview_expression!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
                } else if (textview_expression.lineCount == 1) {
                    textview_expression.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)
                } else if (textview_expression.lineCount == 3) {
                    //need to disable adding character
                }
            }
        })

        textview_result.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                if (textview_result.lineCount == 2) {
                    textview_result.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)
                } else if (textview_result.lineCount == 1) {
                    textview_result.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
                } else if (textview_result.lineCount == 3) {
                    //need to disable adding character
                }
            }
        })
    }

    fun onKeyTap(view: View) {
        when (view.id) {
            R.id.button_0 -> appendCharacter('0')
            R.id.button_1 -> appendCharacter('1')
            R.id.button_2 -> appendCharacter('2')
            R.id.button_3 -> appendCharacter('3')
            R.id.button_4 -> appendCharacter('4')
            R.id.button_5 -> appendCharacter('5')
            R.id.button_6 -> appendCharacter('6')
            R.id.button_7 -> appendCharacter('7')
            R.id.button_8 -> appendCharacter('8')
            R.id.button_9 -> appendCharacter('9')
            R.id.button_plus -> appendCharacter('+')
            R.id.button_minus -> appendCharacter('-')
            R.id.button_mult -> appendCharacter('\u00D7')
            R.id.button_div -> appendCharacter('\u00F7')
            R.id.button_decimal_point -> appendCharacter('.')
            R.id.button_equal -> result()
            R.id.button_clear -> {
                textview_expression.text = ""
                textview_result.text = ""
            }
            R.id.button_back_space -> {
                val str = textview_expression.text.toString()
                when {
                    str.length > 1 && (str[str.length - 2] == ' ') ->
                        textview_expression.text = str.substring(0, str.length - 2)
                    str.isNotEmpty() ->
                        textview_expression.text = str.substring(0, str.length - 1)
                }
            }
            R.id.button_sqrt -> onSquareRoot()
            R.id.button_negate -> onPositiveNegative()
        }
    }

    private fun appendCharacter(ch: Char) {
        if (textview_expression.length() > 0 && isOperator(ch)) {
            textview_expression.append(" " + ch)
        } else {
            if (textview_expression.text.isNotEmpty() && isOperator(textview_expression.text.last())) {
                textview_expression.append(" " + ch)
            } else {
                textview_expression.append("" + ch)
            }
        }
    }

    private fun isOperator(ch: Char): Boolean {
        when (ch) {
            '+', '-', '\u00D7', '\u00F7' -> return true
            else -> return false
        }
    }

    private fun result() {
        val result = result
        if (result == null) {
            textview_result.text = "Invalid Expr"
            return
        }
        textview_result.text = result.toPlainString()
    }

    private fun onPositiveNegative() {
        val text = textview_expression.text
        if (text.isEmpty() || isOperator(text[text.lastIndex])) {
            return
        } else {
            val insertPos = text.lastIndexOf(' ')
            val prefix = text.substring(0, insertPos + 1)
            val suffix = text.substring(insertPos + 1)
            if (suffix.startsWith('-')) {
                textview_expression.text = prefix + suffix.substring(1)
            } else {
                textview_expression.text = prefix + '-' + suffix
            }
        }
    }

    private fun onSquareRoot() {
        val result = result
        if (result == null) {
            textview_result.text = "Invalid Expr"
            return
        }
        try {
            val expression = Expression("sqrt(${result.toPlainString()})")
            expression.setPrecision(15)
            val newResult = expression.eval()
            textview_result.text = newResult.toPlainString()
        } catch (ex: Exception) {
            textview_result.text = "Invalid Expr"
        }

    }

    private val result: BigDecimal?
        get() {
            try {
                var expressionStrTmp = textview_expression.text.toString()
                expressionStrTmp = expressionStrTmp.replace("\u00D7".toRegex(), "*")
                val expressionStr = expressionStrTmp.replace("\u00F7".toRegex(), "/")
                val expression = Expression(expressionStr)
                expression.setPrecision(15)
                val result = expression.eval()
                return result
            } catch (ex: Exception) {
                return null
            }

        }
}
