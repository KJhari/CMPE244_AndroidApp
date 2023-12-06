package com.hari_priyam.iot_curtain

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.media.MediaPlayer
import android.media.session.PlaybackState
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.hari_priyam.iot_curtain.ui.theme.IoTCurtainTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

@UnstableApi @ExperimentalUnitApi
class MainActivity : ComponentActivity() {

    val JETSON_URL = "192.168.46.23:8000"
    val MOTOR_FREQUENCY = 50
    val DUTY_CYCLE = 100

    lateinit var videoPlayer: ExoPlayer

    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IoTCurtainTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier.weight(3f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            DisposableEffect(
                                AndroidView(factory = {
                                    PlayerView(applicationContext).apply {
                                        player = videoPlayer
                                        useController = false
                                        layoutParams = FrameLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT
                                        )
                                    }
                                })
                            ) {
                                onDispose {
                                    videoPlayer.release()
                                }
                            }

                        }

                        Column(
                            modifier = Modifier.weight(3f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    16.dp,
                                    Alignment.CenterHorizontally
                                ),
                            ) {
                                Button(onClick = {
                                    val currentPosition = (videoPlayer.currentPosition / 1000).toInt()

                                    if (videoPlayer.isPlaying) {
                                        Toast.makeText(
                                            applicationContext,
                                            "System busy!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        return@Button
                                    }

                                    // already open
                                    if (currentPosition == 4) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Already open!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        return@Button
                                    }

                                    when (currentPosition) {
                                        0 -> {
//                                            Toast.makeText(applicationContext, "From start.", Toast.LENGTH_SHORT).show()
                                            runMotor(0, 180)
                                        }
                                        2 -> {
//                                            Toast.makeText(applicationContext, "From middle.", Toast.LENGTH_SHORT).show()
                                            runMotor(0, 90)
                                        }
                                        10 -> {
                                            videoPlayer.seekTo(1)
//                                            Toast.makeText(applicationContext, "From start.", Toast.LENGTH_SHORT).show()
                                            runMotor(0, 180)
                                        }
                                        8 -> {
                                            videoPlayer.seekTo(2000)
//                                            Toast.makeText(applicationContext, "From middle.", Toast.LENGTH_SHORT).show()
                                            runMotor(0, 90)
                                        }
                                    }

                                    videoPlayer.play()
                                }) {
                                    Text("Open")
                                }

                                Button(onClick = {
                                    val currentPosition = (videoPlayer.currentPosition / 1000).toInt()

                                    if (videoPlayer.isPlaying) {
                                        Toast.makeText(
                                            applicationContext,
                                            "System busy!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        return@Button
                                    }

                                    if (currentPosition == 0 || currentPosition == 10) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Already closed!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        return@Button
                                    }

                                    when (currentPosition) {
                                        2 -> {
                                            videoPlayer.seekTo(8000)
//                                            Toast.makeText(applicationContext, "From middle.", Toast.LENGTH_SHORT).show()
                                            runMotor(1, 90)
                                        }
                                        4 -> {
                                            videoPlayer.seekTo(6000)
//                                            Toast.makeText(applicationContext, "From end.", Toast.LENGTH_SHORT).show()
                                            runMotor(1, 180)
                                        }
                                        8 -> {
//                                            Toast.makeText(applicationContext, "From middle.", Toast.LENGTH_SHORT).show()
                                            runMotor(1, 90)
                                        }
                                        10 -> {
//                                            Toast.makeText(applicationContext, "From end.", Toast.LENGTH_SHORT).show()
                                            runMotor(1, 180)
                                        }
                                    }

                                    videoPlayer.play()
                                }) {
                                    Text("Close")
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(onClick = {
                                    val currentPosition = (videoPlayer.currentPosition / 1000).toInt()

                                    if (videoPlayer.isPlaying) {
                                        Toast.makeText(
                                            applicationContext,
                                            "System busy!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        return@Button
                                    }

                                    if (currentPosition == 2 || currentPosition == 8) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Already half-open!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        return@Button
                                    }

                                    videoPlayer
                                        .createMessage { messageType, payload ->
                                            GlobalScope.launch(Dispatchers.Main) {
                                                videoPlayer.pause()
                                            }
                                        }
                                        .setPosition(
                                            when (currentPosition) {
                                                0 -> 2000
                                                10 -> 2000
                                                4 -> 8000
                                                else -> 2000
                                            }
                                        )
                                        .setDeleteAfterDelivery(true)
                                        .send()

                                    when (currentPosition) {
                                        0 -> {
//                                            Toast.makeText(applicationContext, "From start.", Toast.LENGTH_SHORT).show()
                                            runMotor(0, 90)
                                        }
                                        4 -> {
                                            videoPlayer.seekTo(6000)
//                                            Toast.makeText(applicationContext, "From end.", Toast.LENGTH_SHORT).show()
                                            runMotor(1, 90)
                                        }
                                        10 -> {
                                            videoPlayer.seekTo(1)
//                                            Toast.makeText(applicationContext, "From start.", Toast.LENGTH_SHORT).show()
                                            runMotor(0, 90)
                                        }
                                    }

                                    videoPlayer.play()
                                }) {
                                    Text("Half Open")
                                }
                            }

                            Column(
                                modifier = Modifier.weight(2f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                FilledTonalButton(
                                    onClick = {
                                        val intent = Intent(
                                            applicationContext,
                                            ChatGptSupportActivity::class.java
                                        )

                                        startActivity(intent)
                                    },
                                ) {
                                    Text("Support")
                                }
                            }
                        }
                    }
                }
            }
        }

        videoPlayer = ExoPlayer.Builder(applicationContext).build()
        videoPlayer.setMediaItem(MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.curtain_loop)))
        videoPlayer.prepare()
        videoPlayer.seekTo(1)

        videoPlayer
            .createMessage { messageType, payload ->
                GlobalScope.launch(Dispatchers.Main) {
                    videoPlayer.pause()
                }
            }
            .setPosition(4000)
            .setDeleteAfterDelivery(false)
            .send()
    }

    fun runMotor(direction: Int, degree: Int) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://$JETSON_URL/run-motor"

        val stringRequest =object: StringRequest(
            Request.Method.POST,
            url,
            { response ->
                Log.d(getString(R.string.app_name), "Motor moved!")
            },
            {
                Log.d(getString(R.string.app_name), "Motor did not moved.")
            }
        ) {
            override fun getBody(): ByteArray {
                val jsonObj = JSONObject()
                jsonObj.put("Frequency", MOTOR_FREQUENCY)
                jsonObj.put("Duty_Cycle", DUTY_CYCLE)
                jsonObj.put("Direction", direction)
                jsonObj.put("Degree", degree)
                return jsonObj.toString().toByteArray(Charsets.UTF_8)
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8";
            }
        }

        queue.add(stringRequest)
    }
}
