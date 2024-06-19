package com.ertaqy.recorder.features.Files

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.ertaqy.recorder.base.playback.AndroidAudioPlayer
import com.ertaqy.recorder.RecordingViewModel

class FilesScreen(val player: AndroidAudioPlayer) : Screen {
    @Composable
    override fun Content() {
        val viewModel : RecordingViewModel = getViewModel()
        FilesUI(viewModel , player)
    }
    @Composable
    fun FilesUI(viewModel : RecordingViewModel, player : AndroidAudioPlayer){
        LazyColumn(Modifier.padding(25.dp)) {
            items(viewModel.files) { file ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(25.dp)
                        .background(Color.Green)
                        .clickable {
                            player.playFile(file)
                        }
                ) {
                    Text(
                        text = file.name.toString(),
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))

            }
        }
    }
}