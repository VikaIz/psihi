package com.izm.spokoyno

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun TestPassingScreen(testId: Int, onTestFinished: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.screen_passing_title),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.screen_passing_stub, testId))
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { onTestFinished(testId) }) {
            Text(stringResource(R.string.btn_finish_test))
        }
    }
}