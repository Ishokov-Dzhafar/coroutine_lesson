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
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    val supervisorJob = SupervisorJob()
    val coroutineScope = CoroutineScope(Dispatchers.IO + supervisorJob)
    private var repo = FactRepository()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadMore.setOnClickListener {
            supervisorJob.cancelChildren()
            loadFacts()
        }
        loadFacts()
    }

    private fun loadFacts() {
        coroutineScope.launch {
            val response = repo.fetchRandomFacts("cat", 3)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    showContent(result)
                } else {
                    showError(response)
                }
            }
        }
    }

    private fun showContent(result: List<FactRes>) {
        result.forEach {
            val textView = TextView(this@MainActivity)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(16, 0, 16, 16)
            textView.text = it.text
            factLayout.addView(textView, layoutParams)
        }
    }

    private fun showError(response: Response<List<FactRes>>) {
        Toast.makeText(
            factLayout.context,
            "Что-то пошло не так ${response.code()}",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        supervisorJob.cancel()
    }
}
