package com.ssafy.yeobaeg.presentation.cafe

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import androidx.compose.foundation.border
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown

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
                currentZoomLevel = uiState.currentZoomLevel,
                onCameraPositionChanged = viewModel::onCameraPositionChanged
            )
            
            // 현재 위치로 이동 버튼
            FloatingActionButton(
                onClick = { viewModel.loadCurrentLocation() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .padding(bottom = 200.dp),
                containerColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                ),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "현재 위치",
                    tint = YeobaegDarkBrown
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
            
            // 로딩 인디케이터 (초기 로딩시만 표시)
            if (uiState.isInitialLoading) {
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
        shadowElevation = 2.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // 상단: 제목과 필터 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "카페 찾기",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = YeobaegDarkBrown
                )

                // 필터 버튼 (작가만)
                if (showFilterButton) {
                    Surface(
                        onClick = onFilterClick,
                        modifier = Modifier.size(42.dp),
                        shape = CircleShape,
                        color = if (isFilterActive) YeobaegDarkBrown else Color(0xFFF5F5F5),
                        shadowElevation = if (isFilterActive) 4.dp else 0.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "필터",
                                tint = if (isFilterActive) Color.White else YeobaegDarkBrown,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 검색 입력 필드 - 더 모던한 디자인
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF8F8F8),
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "검색",
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    androidx.compose.foundation.text.BasicTextField(
                        value = searchKeyword,
                        onValueChange = onSearchKeywordChanged,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF212121)
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box {
                                if (searchKeyword.isEmpty()) {
                                    Text(
                                        "카페나 전시를 검색하세요",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color(0xFFBDBDBD)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GoogleMapView(
    cafes: List<com.ssafy.yeobaeg.domain.model.cafe.CafeSummary>,
    currentLocation: LatLng?,
    cameraPosition: LatLng?,
    currentZoomLevel: Float,
    onCameraPositionChanged: (LatLng, Float) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val defaultPosition = remember { LatLng(37.5665, 126.9780) } // 서울 시청
    val initialPosition = cameraPosition ?: currentLocation ?: defaultPosition

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, currentZoomLevel)
    }

    // 마커 아이콘 캐시 (lazy 초기화)
    var singleMarkerIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    val markerIconCache = remember { mutableMapOf<Int, BitmapDescriptor>() }

    // 카메라 위치 또는 줌이 변경될 때마다 콜백 호출
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            onCameraPositionChanged(
                cameraPositionState.position.target,
                cameraPositionState.position.zoom
            )
        }
    }

    // currentLocation이 변경되면 카메라 이동
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it, currentZoomLevel),
                durationMs = 1000
            )
        }
    }

    // 클러스터링을 위한 카페 그룹화
    val clusters = remember(cafes, cameraPositionState.position.zoom) {
        clusterCafes(cafes, cameraPositionState.position.zoom)
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
        // 클러스터 마커들
        clusters.forEach { cluster ->
            if (cluster.cafes.size == 1) {
                // 단일 카페 마커
                val cafe = cluster.cafes.first()
                cafe.latitude?.let { lat ->
                    cafe.longitude?.let { lng ->
                        // 기본 갈색 마커 핀 사용
                        if (singleMarkerIcon == null) {
                            singleMarkerIcon = try {
                                BitmapDescriptorFactory.defaultMarker(15f) // 갈색 (HUE 15도)
                            } catch (e: Exception) {
                                null
                            }
                        }

                        MarkerInfoWindowContent(
                            state = MarkerState(position = LatLng(lat, lng)),
                            icon = singleMarkerIcon
                        ) {
                            CafeInfoWindow(cafe = cafe)
                        }
                    }
                }
            } else {
                // 클러스터 마커 (여러 카페)
                // 캐시에서 아이콘 가져오기 또는 생성
                val clusterIcon = markerIconCache.getOrPut(cluster.cafes.size) {
                    try {
                        createClusterMarkerIcon(context, cluster.cafes.size)
                    } catch (e: Exception) {
                        // 초기화 실패 시 기본 마커 사용
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                    }
                }

                Marker(
                    state = MarkerState(position = cluster.position),
                    title = "${cluster.cafes.size}개 카페",
                    snippet = "탭하여 확대",
                    icon = clusterIcon,
                    onClick = {
                        // 클러스터 내 모든 카페를 포함하는 영역으로 줌
                        val builder = LatLngBounds.Builder()
                        cluster.cafes.forEach { cafe ->
                            cafe.latitude?.let { lat ->
                                cafe.longitude?.let { lng ->
                                    builder.include(LatLng(lat, lng))
                                }
                            }
                        }
                        val bounds = builder.build()
                        val padding = 150 // 패딩 (픽셀)
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngBounds(bounds, padding),
                                durationMs = 500
                            )
                        }
                        true // 이벤트 소비
                    }
                )
            }
        }
    }
}

/**
 * 카페 정보 윈도우 (마커 클릭 시 표시)
 */
@Composable
fun CafeInfoWindow(cafe: com.ssafy.yeobaeg.domain.model.cafe.CafeSummary) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // 썸네일 이미지
            cafe.primaryImageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = cafe.cafeName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 카페명
            Text(
                text = cafe.cafeName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = YeobaegDarkBrown,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 주소
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF9E9E9E),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = cafe.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF757575),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 슬롯 정보와 거리
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 슬롯 정보
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = YeobaegDarkBrown.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "슬롯",
                            style = MaterialTheme.typography.labelSmall,
                            color = YeobaegDarkBrown,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${cafe.availableSlotCount}/${cafe.totalSlotCount}",
                            style = MaterialTheme.typography.labelMedium,
                            color = YeobaegDarkBrown,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 거리
                cafe.distance?.let { distance ->
                    Text(
                        text = "${String.format("%.1f", distance)}km",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF616161),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 카테고리
            if (cafe.categories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    cafe.categories.take(3).forEach { category ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF5F5F5),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF757575),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 카페 클러스터 데이터 클래스
 */
data class CafeCluster(
    val position: LatLng,
    val cafes: List<com.ssafy.yeobaeg.domain.model.cafe.CafeSummary>
)

/**
 * 줌 레벨에 따라 카페를 클러스터링
 */
fun clusterCafes(
    cafes: List<com.ssafy.yeobaeg.domain.model.cafe.CafeSummary>,
    zoomLevel: Float
): List<CafeCluster> {
    // 줌 레벨이 높으면 (확대) 클러스터링 하지 않음
    if (zoomLevel >= 15f) {
        return cafes.mapNotNull { cafe ->
            cafe.latitude?.let { lat ->
                cafe.longitude?.let { lng ->
                    CafeCluster(
                        position = LatLng(lat, lng),
                        cafes = listOf(cafe)
                    )
                }
            }
        }
    }

    // 클러스터링 거리 (줌 레벨에 따라 조정)
    val clusterDistance = when {
        zoomLevel >= 13f -> 0.01  // 약 1km
        zoomLevel >= 11f -> 0.05  // 약 5km
        zoomLevel >= 9f -> 0.1    // 약 10km
        else -> 0.2               // 약 20km
    }

    val clusters = mutableListOf<CafeCluster>()
    val processed = mutableSetOf<com.ssafy.yeobaeg.domain.model.cafe.CafeSummary>()

    cafes.forEach { cafe ->
        if (cafe in processed) return@forEach

        cafe.latitude?.let { lat ->
            cafe.longitude?.let { lng ->
                val nearby = mutableListOf(cafe)
                processed.add(cafe)

                // 근처 카페 찾기
                cafes.forEach { other ->
                    if (other !in processed && other.latitude != null && other.longitude != null) {
                        val distance = kotlin.math.sqrt(
                            (lat - other.latitude!!).let { it * it } +
                                    (lng - other.longitude!!).let { it * it }
                        )
                        if (distance < clusterDistance) {
                            nearby.add(other)
                            processed.add(other)
                        }
                    }
                }

                // 클러스터 중심 계산
                val centerLat = nearby.mapNotNull { it.latitude }.average()
                val centerLng = nearby.mapNotNull { it.longitude }.average()

                clusters.add(
                    CafeCluster(
                        position = LatLng(centerLat, centerLng),
                        cafes = nearby
                    )
                )
            }
        }
    }

    return clusters
}

/**
 * 클러스터 마커 아이콘 생성 (간단한 갈색 원)
 */
fun createClusterMarkerIcon(context: Context, count: Int): BitmapDescriptor {
    // 클러스터 크기는 카페 개수에 따라 동적으로 조정 (마커 핀보다 크게)
    val baseSize = 180
    val size = when {
        count >= 50 -> (baseSize * 1.5f).toInt()
        count >= 20 -> (baseSize * 1.3f).toInt()
        count >= 10 -> (baseSize * 1.1f).toInt()
        else -> baseSize
    }

    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val centerX = size / 2f
    val centerY = size / 2f

    // YeobaegDarkBrown 색상
    val brownColor = android.graphics.Color.rgb(101, 67, 33)
    val whiteColor = android.graphics.Color.WHITE

    // 그림자
    val shadowPaint = Paint().apply {
        color = android.graphics.Color.argb(70, 0, 0, 0)
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    canvas.drawCircle(centerX, centerY + 3f, size / 2.4f, shadowPaint)

    // 갈색 원
    val circlePaint = Paint().apply {
        color = brownColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    canvas.drawCircle(centerX, centerY, size / 2.5f, circlePaint)

    // 숫자 텍스트
    val text = if (count > 999) "999+" else count.toString()

    // 텍스트 크기 동적 조정
    val fontSize = when {
        count >= 100 -> size / 3.5f
        count >= 10 -> size / 3.0f
        else -> size / 2.5f
    }

    val textPaint = Paint().apply {
        color = whiteColor
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = fontSize
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val yPos = centerY - ((textPaint.descent() + textPaint.ascent()) / 2)
    canvas.drawText(text, centerX, yPos, textPaint)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

