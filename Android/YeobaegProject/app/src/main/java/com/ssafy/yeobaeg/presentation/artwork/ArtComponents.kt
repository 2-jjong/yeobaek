// ArtComponents.kt
package com.ssafy.yeobaeg.presentation.artwork

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite

@Composable
fun ArtworkGrid(
    items: List<String>,
    modifier: Modifier = Modifier,
    columns: Int = 3,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = contentPadding,
        userScrollEnabled = false,
        modifier = modifier
    ) {
        items(items) { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun CategoryChips(
    categories: List<String>,
    selected: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories.size) { i ->
            val c = categories[i]
            val isSelected = c == selected
            FilterChip(
                selected = isSelected,
                onClick = { onClick(c) },
                label = { Text(c) },
                shape = MaterialTheme.shapes.large,
                border = null,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFFF3F4F6),
                    selectedContainerColor = YeobaegBrown,
                    selectedLabelColor = YeobaegWhite,
                    labelColor = YeobaegDarkGray
                )
            )
        }
    }
}
