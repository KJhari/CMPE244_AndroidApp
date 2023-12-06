package com.hari_priyam.iot_curtain

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.annotations.SerializedName
import com.hari_priyam.iot_curtain.ui.theme.IoTCurtainTheme
import org.json.JSONObject
import java.net.URL

@ExperimentalUnitApi
@ExperimentalMaterial3Api
class ChatGptSupportActivity : ComponentActivity() {

    val GPT_URL = "10.0.0.160:8000"

    val response = mutableStateOf("")
    val request = mutableStateOf("")
    val isRequestInProgress = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IoTCurtainTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier.weight(1f, true),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "GPT Support",
                                fontSize = TextUnit(20f, TextUnitType.Sp)
                            )
                        }
                        Column(
                            modifier = Modifier.weight(3f, true),
                            verticalArrangement = Arrangement.spacedBy(
                                8.dp,
                                Alignment.CenterVertically
                            )
                        ) {
                            Text(
                                text = "Prompt:"
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = request.value,
                                onValueChange = { request.value = it }
                            )
                        }
                        Row(
                            modifier = Modifier.weight(1f, true),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                onClick = {
                                    prompt()
                                },
                                colors = ButtonDefaults.buttonColors()
                            ) {
                                Text(text = "Send")
                            }
//                            TextButton(
//                                onClick = {
//                                    apiCallTask!!.cancel(true)
//                                    isRequestInProgress.value = false
//                                }
//                            ) {
//                                Text(text = "Cancel")
//                            }
                        }
                        Column(
                            modifier = Modifier.weight(4f, true),
                            verticalArrangement = Arrangement.spacedBy(
                                8.dp,
                                Alignment.CenterVertically
                            )
                        ) {
                            Text(
                                text = "Response:"
                            )
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.primaryContainer,
                            ) {
                                if (isRequestInProgress.value)
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        color = Color.Transparent
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                Text(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .verticalScroll(
                                            rememberScrollState()
                                        ),
                                    text = response.value
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun prompt() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://$GPT_URL/gpt-prompt"

        isRequestInProgress.value = true

        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url,
            { response ->
                Log.d(getString(R.string.app_name), response.toString())
                this.response.value = JSONObject(response.toString()).getString("responseText")
                isRequestInProgress.value = false
            },
            {
                Log.d(getString(R.string.app_name), "Error! ${it.message}")
                isRequestInProgress.value = false
            }
        ) {
            override fun getBody(): ByteArray {
                val jsonObj = JSONObject()
                jsonObj.put("prompt", request.value)
                return jsonObj.toString().toByteArray(Charsets.UTF_8)
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8";
            }
        }

        queue.add(stringRequest)
    }
}