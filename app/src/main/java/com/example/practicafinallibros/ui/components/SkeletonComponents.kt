package com.example.practicafinallibros.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SkeletonBookList() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(5) {
            SkeletonBookItem()
        }
    }
}

@Composable
fun SkeletonBookItem() {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Box(Modifier.fillMaxWidth(0.7f).height(20.dp).background(Color.LightGray.copy(alpha = 0.5f)))
                Spacer(Modifier.height(8.dp))
                Box(Modifier.fillMaxWidth(0.4f).height(16.dp).background(Color.LightGray.copy(alpha = 0.5f)))
            }
        }
    }
}
