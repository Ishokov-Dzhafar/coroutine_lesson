package com.dzhafar.coroutine_lesson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private var myJob: Job? = null
    private var repo = FactRepository()
    lateinit var factLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        factLayout = this.findViewById(R.id.factLayout)
    }

    override fun onResume() {
        super.onResume()
        loadFacts()
        loadMore.setOnClickListener {
            myJob?.cancel()
            loadFacts()
        }
    }

    fun loadFacts() {
        myJob = CoroutineScope(Dispatchers.IO).launch {
            val response = repo.fetchRandomFacts("cat", 3)
            if(response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                result.forEach {
                    Log.d("RESULT", it.text)
                }
                withContext(Dispatchers.Main) {
                    result.forEach {
                        val textView = TextView(factLayout.context)
                        textView.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        val margin = textView.layoutParams as ViewGroup.MarginLayoutParams
                        margin.bottomMargin = 16
                        textView.text = it.text
                        factLayout.addView(textView)
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        factLayout.context,
                        "Что-то пошло не так ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        myJob?.cancel()
        super.onDestroy()
    }
}
