package com.ssafy.yeobaeg.presentation.chat.matching

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.yeobaeg.domain.model.UserRole

@Composable
fun MatchingNavHost(
    myId: Long,
    partnerId: Long,
    myRole: UserRole,
    navController: NavHostController = rememberNavController(),
    onFinish: () -> Unit
) {
    val viewModel: MatchingViewModel = hiltViewModel()
    LaunchedEffect(Unit) {
        viewModel.setChatContext(
            myRole = myRole,
            partnerId = partnerId
        )
        viewModel.setMyId(myId)
    }

    NavHost(navController = navController, startDestination = "date") {
        composable("date") {
            MatchingDateScreen(
                onNext = { navController.navigate("space") },
                viewModel = viewModel
            )
        }
        composable("space") {
            MatchingSpaceScreen(
                onNext = { navController.navigate("artwork") },
                viewModel = viewModel
            )
        }

        composable("artwork") {
            MatchingArtworkScreen(
                onNext = { navController.navigate("fee") },
                viewModel = viewModel
            )
        }
        composable("fee") {
            MatchingFeeScreen(
                onNext = { navController.navigate("note") },
                viewModel = viewModel
            )
        }
        composable("note") {
            MatchingNoteScreen(
                onSubmit = {
                    onFinish()
                },
                viewModel = viewModel
            )
        }
    }
}
