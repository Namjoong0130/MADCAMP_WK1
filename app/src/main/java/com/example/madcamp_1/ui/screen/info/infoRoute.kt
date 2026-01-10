package com.example.madcamp_1.ui.screen.info

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


@Composable
fun InfoRoute(
    category: String,
    navController: NavController,
    viewModel: InfoViewModel = viewModel()
) {
    LaunchedEffect(category) {
        viewModel.loadDataByCategory(category)
    }

    val videoId by viewModel.videoId.collectAsState()
    val location by viewModel.stadiumLocation.collectAsState()
    val stadiumName by viewModel.stadiumName.collectAsState()
    val records by viewModel.historyRecords.collectAsState() // [변경]

    InfoScreen(
        category = category,
        videoId = videoId,
        location = location,
        stadiumName = stadiumName,
        records = records, // [변경]
        onBackClick = { navController.popBackStack() }
    )
}