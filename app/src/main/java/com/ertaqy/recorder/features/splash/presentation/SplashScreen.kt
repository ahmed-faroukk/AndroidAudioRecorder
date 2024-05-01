package com.ertaqy.recorder.features.splash.presentation

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.ertaqy.recorder.R
import com.ertaqy.recorder.base.animations.AFAnimateAsFloat
import com.ertaqy.recorder.features.splash.presentation.components.ShowLogo

class SplashScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel : SplashViewModel = getViewModel()
        SplashUI(viewModel)
    }

    @Composable
    fun SplashUI(viewModel : SplashViewModel) {
        val navigator = LocalNavigator.current
        val context = LocalContext.current
        viewModel.navigateToHome(navigator , context)
        AFAnimateAsFloat(
            delay = 500,
            initValue = 400f,
            targetValue = 300f,
            animationSpec = tween(100),
            content = { modifier, isVisible ->
                ShowLogo(modifier = modifier, R.drawable.splash, 300f)
                if (isVisible.value)
                    ShowLogo(
                        modifier = modifier.padding(bottom = 80.dp),
                        R.drawable.splash,
                        400f
                    )
            } )

    }
}
