package com.ssafy.yeobaeg.presentation.home.artist

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite

@Composable
fun ArtworkRegisterScreen(
    viewModel: ArtworkRegisterViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.resetSteps()
    }
    val step = viewModel.currentStep.collectAsState().value
    val context = LocalContext.current

    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "작품 등록",
                onBackClick = {
                    if (step == 1) {
                        // step 1이면 이전 화면으로 이동
                        viewModel.navigateBack()
                    } else {
                        // 그 외에는 이전 단계로 이동
                        viewModel.prevStep()
                    }
                }, actionIcon = null,
                onActionClick = {}
            )
        },
        containerColor = YeobaegWhite
    ) { paddingValues ->
        when (step) {
            1 -> ArtworkImageUploadScreen(
                onNext = { viewModel.nextStep() },
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues)
            )

            2 -> ArtworkBasicInfoScreen(
                onNext = { viewModel.nextStep() },
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues)
            )

            3 -> ArtworkDetailInfoScreen(
                onNext = { viewModel.nextStep() },
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues)
            )

            4 -> ArtworkDescriptionScreen(
                onRegister = {
                    viewModel.registerArtwork(context)
                },
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ArtworkRegisterScreenPreview() {
    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "작품 등록",
                onBackClick = {},
                actionIcon = null,
                onActionClick = {},
                showBackButton = true
            )
        },
        containerColor = YeobaegWhite
    ) { _ ->
        ArtworkRegisterScreen()
    }
}