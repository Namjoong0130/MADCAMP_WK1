package com.example.madcamp_1.ui.screen.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ScheduleRoute(viewModel: ScheduleViewModel = viewModel()) {
    val events by viewModel.events.collectAsState()
    ScheduleScreen(events = events)
}