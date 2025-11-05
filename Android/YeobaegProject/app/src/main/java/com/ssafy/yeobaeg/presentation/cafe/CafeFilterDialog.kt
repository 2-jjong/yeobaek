package com.ssafy.yeobaeg.presentation.cafe

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ssafy.yeobaeg.domain.model.cafe.ArtworkSize
import com.ssafy.yeobaeg.domain.model.cafe.CafeFilter
import com.ssafy.yeobaeg.domain.model.CommissionRate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeFilterDialog(
    currentFilter: CafeFilter,
    onDismiss: () -> Unit,
    onApply: (CafeFilter) -> Unit,
    onClear: () -> Unit
) {
    var selectedSize by remember { mutableStateOf<ArtworkSize?>(null) }
    var selectedCommission by remember { mutableStateOf<CommissionRate?>(null) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // 헤더
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "필터",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기"
                    )
                }
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 작품 크기 섹션
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "작품 크기",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ArtworkSize.values().forEach { size ->
                            FilterChip(
                                selected = selectedSize == size,
                                onClick = { 
                                    selectedSize = if (selectedSize == size) null else size 
                                },
                                label = { Text(size.label) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }
                
                // 수수료 섹션
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "수수료",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CommissionRate.values().take(2).forEach { rate ->
                            FilterChip(
                                selected = selectedCommission == rate,
                                onClick = { 
                                    selectedCommission = if (selectedCommission == rate) null else rate 
                                },
                                label = { Text(rate.label) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CommissionRate.values().drop(2).forEach { rate ->
                            FilterChip(
                                selected = selectedCommission == rate,
                                onClick = { 
                                    selectedCommission = if (selectedCommission == rate) null else rate 
                                },
                                label = { Text(rate.label) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }
            }
            
            // 하단 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        selectedSize = null
                        selectedCommission = null
                        onClear()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("초기화")
                }
                
                Button(
                    onClick = {
                        val filter = CafeFilter(
                            minWidth = selectedSize?.minWidth,
                            maxWidth = selectedSize?.maxWidth,
                            minHeight = selectedSize?.minHeight,
                            maxHeight = selectedSize?.maxHeight,
                            minCommission = selectedCommission?.minCommission,
                            maxCommission = selectedCommission?.maxCommission
                        )
                        onApply(filter)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8D6E63)
                    )
                ) {
                    Text("적용하기")
                }
            }
        }
    }
}

