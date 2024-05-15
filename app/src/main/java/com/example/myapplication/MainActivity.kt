package com.example.myapplication

import MainViewModel
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.CompassRepository
import com.example.myapplication.ui.theme.Purple40


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = MainViewModel(CompassRepository())
        setContent {
            MainScreen(viewModel)
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    val every10thCharacter by viewModel.every10thCharacter.observeAsState(String())
    val wordCounter by viewModel.wordCounter.observeAsState(emptyMap())
    val isLoading by viewModel.isLoading.observeAsState(false)


    Column(modifier = Modifier.verticalScroll(scrollState)) {
        Button(
            onClick = { viewModel.getAboutContent() },
            modifier = Modifier
                .align(Alignment.Start)
                .padding(10.dp)
        ) {
            Text(text = "get Compass About")
        }
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 300.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Text(
                text = "Every 10th Character:",
                modifier = Modifier.padding(16.dp),
                color = Purple40,
                fontWeight = FontWeight.Bold
            )
            every10thCharacter?.let {
                Text(
                    text = it, modifier = Modifier.padding(16.dp)
                )
            }
            Text(
                text = "Word Count:",
                modifier = Modifier.padding(16.dp),
                color = Purple40,
                fontWeight = FontWeight.Bold
            )

            Column(modifier = Modifier.padding(16.dp)) {
                wordCounter.forEach { (word, count) ->
                    Text(text = buildAnnotatedString {
                        append("$word: ")
                        append(
                            AnnotatedString(
                                text = count.toString(),
                                spanStyle = SpanStyle(fontWeight = FontWeight.Bold)
                            )
                        )
                    })
                }
            }
        }
    }
}

