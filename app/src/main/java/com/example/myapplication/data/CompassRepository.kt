package com.example.myapplication.data

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class CompassRepository {
    suspend fun getTextFromUrl(url: String): String {
        return withContext(IO) {
            val document = Jsoup.connect(url).get()
            val elementBody = document.body()
            elementBody.text()
        }
    }
}