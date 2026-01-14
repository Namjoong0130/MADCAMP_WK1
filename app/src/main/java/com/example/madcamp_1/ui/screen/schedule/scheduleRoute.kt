// ScheduleRoute.kt
package com.example.madcamp_1.ui.screen.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ScheduleRoute(
    onNavigateToInfo: (String) -> Unit, // MainScreen에서 들어오는 람다
    viewModel: ScheduleViewModel = viewModel()
) {
    val events by viewModel.events.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userSchoolId by viewModel.userSchoolId.collectAsState()
    val selectedEvent by viewModel.selectedEvent.collectAsState()

    ScheduleScreen(
        events = events,
        userName = userName,
        userLogoRes = viewModel.getSchoolLogo(userSchoolId),
        userSchoolDisplayName = viewModel.getSchoolDisplayName(userSchoolId),
        pScore = viewModel.postechScore,
        kScore = viewModel.kaistScore,
        selectedEvent = selectedEvent,
        onEventClick = { viewModel.onEventClick(it) },
        onDismissSheet = { viewModel.clearSelectedEvent() },
        onNavigateToInfo = onNavigateToInfo
    )
}