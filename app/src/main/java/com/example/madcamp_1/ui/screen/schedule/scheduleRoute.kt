package com.example.madcamp_1.ui.screen.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ScheduleRoute(viewModel: ScheduleViewModel = viewModel()) {
    val events by viewModel.events.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userSchoolId by viewModel.userSchoolId.collectAsState()

    ScheduleScreen(
        events = events,
        userName = userName,
        userLogoRes = viewModel.getSchoolLogo(userSchoolId),
        userSchoolDisplayName = viewModel.getSchoolDisplayName(userSchoolId),
        pScore = viewModel.postechScore,
        kScore = viewModel.kaistScore
    )
}