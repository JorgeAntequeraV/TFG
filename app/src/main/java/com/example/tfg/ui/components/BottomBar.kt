package com.example.tfg.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfg.ui.theme.GreenAccent

enum class BottomTab { LISTAS, FAVORITOS, PERFIL }

@Composable
fun BottomBar(
    selected: BottomTab,
    onSelect: (BottomTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .padding(vertical = 8.dp)
    ) {
        TabItem(
            label = "Listas",
            icon = { Icon(Icons.Default.FormatListBulleted, null, tint = MaterialTheme.colorScheme.onBackground) },
            selected = selected == BottomTab.LISTAS,
            onClick = { onSelect(BottomTab.LISTAS) }
        )
        TabItem(
            label = "Favoritos",
            icon = { Icon(Icons.Default.Favorite, null, tint = MaterialTheme.colorScheme.onBackground) },
            selected = selected == BottomTab.FAVORITOS,
            onClick = { onSelect(BottomTab.FAVORITOS) }
        )
        TabItem(
            label = "Perfil",
            icon = { Icon(Icons.Default.AccountCircle, null, tint = MaterialTheme.colorScheme.onBackground) },
            selected = selected == BottomTab.PERFIL,
            onClick = { onSelect(BottomTab.PERFIL) }
        )
    }
}

@Composable
private fun RowScope.TabItem(
    label: String,
    icon: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Spacer(Modifier.height(2.dp))
        Text(label, color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier
                .height(3.dp)
                .fillMaxWidth(0.5f)
                .background(if (selected) GreenAccent else Color.Transparent)
        )
    }
}
