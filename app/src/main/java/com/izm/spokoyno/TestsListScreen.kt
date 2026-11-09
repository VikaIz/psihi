package com.izm.spokoyno

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun TestsListScreen(onTestClick: (Int) -> Unit) {
    // Временный список тестов для наглядности
    val testIds = (1..5).toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.screen_tests_title),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(testIds) { testId ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = { onTestClick(testId) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = stringResource(R.string.stub_test_item, testId))
                    }
                }
            }
        }
    }
}