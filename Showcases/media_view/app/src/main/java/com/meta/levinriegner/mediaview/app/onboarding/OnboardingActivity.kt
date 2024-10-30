// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.onboarding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.onboarding.view.OnboardingSlide
import com.meta.levinriegner.mediaview.app.shared.view.ErrorView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.init()
        buildUi()
    }

    private fun buildUi() {
        setContent {
            // UI
            Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                Box(Modifier.fillMaxSize()) {
                    when (val state = viewModel.state.collectAsState().value) {
                        OnboardingState.Idle -> Box(Modifier)

                        is OnboardingState.OnboardingStarted -> LazyRow {
                            items(state.steps.count()) { index ->
                                OnboardingSlide(
                                    imageUri = state.steps[index].imageUri,
                                    title = state.steps[index].title,
                                    description = state.steps[index].description,
                                )
                            }
                        }

                        is OnboardingState.Error -> {
                            ErrorView(
                                modifier = Modifier.fillMaxSize(),
                                description = state.reason,
                                actionButtonText = stringResource(id = R.string.close),
                                onActionButtonPressed = { },
                            )
                        }
                    }
                }
            }
        }
    }
}
