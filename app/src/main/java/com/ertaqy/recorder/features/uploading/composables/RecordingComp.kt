package com.ertaqy.recorder.features.uploading.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ertaqy.recorder.ui.theme.matteBlue

@Composable
fun StopRecordBtn(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(Color.Transparent, shape = CircleShape)
            .border(BorderStroke(3.dp, Color.Red), shape = CircleShape)
            .padding(7.dp)
    ) {
        Box(
            Modifier
                .background(Color.Red, CircleShape)
                .size(50.dp).clickable {
                    onClick()

                })

    }
}

@Composable
fun StartRecordBtn(
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .background(Color.Red, CircleShape)
            .size(50.dp).clickable {
                onClick()

            })

}

@Composable
fun AudioPlayerBtn(
    isPlaying: MutableState<Boolean>, playOnClick: () -> Unit, stopOnClick: () -> Unit,
) {
        if (isPlaying.value) {
            StopRecordBtn {
                stopOnClick()
                isPlaying.value = false
            }
        } else {
            StartRecordBtn {
                playOnClick()
                isPlaying.value = true
            }
        }


}

@Composable
@Preview
fun UiTest() {
    val isPlay = remember {
        mutableStateOf(false)
    }
    AudioPlayerBtn(isPlay ,{} ,{})

}