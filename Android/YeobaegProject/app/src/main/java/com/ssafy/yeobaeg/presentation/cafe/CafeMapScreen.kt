package com.ssafy.yeobaeg.presentation.cafe

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.navigation.navigator.Navigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeMapScreen(
    viewModel: CafeMapViewModel = hiltViewModel(),

) {
    val uiState by viewModel.uiState.collectAsState()

    // 바텀 시트 상태
    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState
    )
    
    // 위치 권한 요청
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.updateLocationPermission(granted)
    }
    
    // 초기 권한 요청
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CafeSearchBar(
                searchKeyword = uiState.searchKeyword,
                onSearchKeywordChanged = viewModel::onSearchKeywordChanged,
                onSearch = viewModel::onSearch,
                showFilterButton = uiState.userRole == UserRole.ARTIST,
                onFilterClick = viewModel::toggleFilterDialog,
                isFilterActive = uiState.filter.isActive()
            )
        },
        sheetContent = {
            CafeListBottomSheetContent(
                cafes = uiState.cafes,
                onCafeClick = viewModel::navigateToCafeDetail
            )
        },
        sheetPeekHeight = 200.dp,
        sheetSwipeEnabled = true,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContainerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Google Map
            GoogleMapView(
                cafes = uiState.cafes,
                currentLocation = uiState.currentLocation,
                cameraPosition = uiState.cameraPosition,
                onCameraPositionChanged = viewModel::onCameraPositionChanged
            )
            
            // 현재 위치로 이동 버튼
            FloatingActionButton(
                onClick = { viewModel.loadCurrentLocation() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .padding(bottom = 200.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "현재 위치",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // 필터 다이얼로그
            if (uiState.isFilterDialogVisible) {
                CafeFilterDialog(
                    currentFilter = uiState.filter,
                    onDismiss = viewModel::toggleFilterDialog,
                    onApply = viewModel::applyFilter,
                    onClear = viewModel::clearFilter
                )
            }
            
            // 로딩 인디케이터
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            
            // 에러 스낵바
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .padding(bottom = 200.dp),
                    action = {
                        TextButton(onClick = viewModel::clearError) {
                            Text("확인")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeSearchBar(
    searchKeyword: String,
    onSearchKeywordChanged: (String) -> Unit,
    onSearch: () -> Unit,
    showFilterButton: Boolean,
    onFilterClick: () -> Unit,
    isFilterActive: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 상단: 제목과 필터 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "카페 지도",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // 필터 버튼 (작가만)
                if (showFilterButton) {
                    IconButton(
                        onClick = onFilterClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFilterActive) 
                                    MaterialTheme.colorScheme.primaryContainer 
                                else 
                                    Color.Transparent
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "필터",
                            tint = if (isFilterActive)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 검색 입력 필드
            OutlinedTextField(
                value = searchKeyword,
                onValueChange = onSearchKeywordChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { 
                    Text(
                        "카페나 전시를 검색하세요",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ) 
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "검색",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun GoogleMapView(
    cafes: List<com.ssafy.yeobaeg.domain.model.cafe.CafeSummary>,
    currentLocation: LatLng?,
    cameraPosition: LatLng?,
    onCameraPositionChanged: (LatLng) -> Unit
) {
    val defaultPosition = remember { LatLng(37.5665, 126.9780) } // 서울 시청
    val initialPosition = cameraPosition ?: currentLocation ?: defaultPosition
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 15f)
    }
    
    // 카메라 위치가 변경될 때마다 콜백 호출
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            onCameraPositionChanged(cameraPositionState.position.target)
        }
    }
    
    // currentLocation이 변경되면 카메라 이동
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it, 15f),
                durationMs = 1000
            )
        }
    }
    
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = currentLocation != null
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false
        )
    ) {
        // 카페 마커들
        cafes.forEach { cafe ->
            cafe.latitude?.let { lat ->
                cafe.longitude?.let { lng ->
                    Marker(
                        state = MarkerState(position = LatLng(lat, lng)),
                        title = cafe.cafeName,
                        snippet = "${cafe.availableSlotCount}/${cafe.totalSlotCount} 슬롯 가능 · ${String.format("%.1f", cafe.distance)}km"
                    )
                }
            }
        }
    }
}

