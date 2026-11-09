package com.izm.spokoyno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun TestResultScreen(
    resultId: Int,
    onGoToTests: () -> Unit,
    onGoToHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.screen_result_title),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.screen_result_stub, resultId))
        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onGoToTests) {
                Text(stringResource(R.string.btn_to_tests))
            }
            OutlinedButton(onClick = onGoToHistory) {
                Text(stringResource(R.string.btn_to_history))
            }
        }
    }
}