package com.example.madcamp_1.ui.screen.battle

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BattleRoute() {
    val viewModel: BattleViewModel = viewModel()
    BattleScreen(viewModel = viewModel)
}