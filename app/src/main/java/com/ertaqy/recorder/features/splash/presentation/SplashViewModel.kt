package com.ertaqy.recorder.features.splash.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.ertaqy.recorder.features.uploading.UploadingScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(

) : ViewModel() {

    fun navigateToHome(navigator: Navigator? , context : Context) {
        viewModelScope.launch {
            delay(3000)
        }.invokeOnCompletion {
          // navigator?.replace(UploadingScreen(context))
        }
    }

    fun navigateToOnBoarding(navigator: Navigator) {

    }
}