package com.dicoding.picodiploma.loginwithanimation.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.picodiploma.loginwithanimation.R

class MyNameEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {
    init {
        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val name = p0.toString()
                when {
                    name.isEmpty() -> setError(context.getString(R.string.error_name), ContextCompat.getDrawable(context, R.drawable.baseline_error_outline_24))
                    else -> error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                // Do nothing
            }

        })
    }
}