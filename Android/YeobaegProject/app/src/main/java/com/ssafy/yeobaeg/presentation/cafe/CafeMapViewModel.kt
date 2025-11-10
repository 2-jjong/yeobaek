package com.ssafy.yeobaeg.presentation.cafe

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.ssafy.yeobaeg.data.service.LocationService
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.cafe.CafeFilter
import com.ssafy.yeobaeg.domain.model.cafe.CafeSummary
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CafeMapUiState(
    val cafes: List<CafeSummary> = emptyList(),
    val isInitialLoading: Boolean = true,
    val error: String? = null,
    val currentLocation: LatLng? = null,
    val cameraPosition: LatLng? = null,
    val searchKeyword: String = "",
    val filter: CafeFilter = CafeFilter(),
    val isFilterDialogVisible: Boolean = false,
    val isBottomSheetExpanded: Boolean = false,
    val userRole: UserRole? = null,
    val hasLocationPermission: Boolean = false,
    val currentZoomLevel: Float = 15f
)

@HiltViewModel
class CafeMapViewModel @Inject constructor(
    private val cafeRepository: CafeRepository,
    private val locationService: LocationService,
    private val authSessionManager: AuthSessionManager,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(CafeMapUiState())
    val uiState: StateFlow<CafeMapUiState> = _uiState.asStateFlow()

    private var cameraMovementJob: Job? = null
    private val CAMERA_MOVEMENT_DEBOUNCE_MS = 800L

    init {
        observeUserRole()
    }
    private fun observeUserRole() {
        viewModelScope.launch {
            authSessionManager.currentUserRole.collect { role ->
                _uiState.update { it.copy(userRole = role) }
            }
        }
    }
    
    /**
     * 위치 권한 상태 업데이트
     */
    fun updateLocationPermission(hasPermission: Boolean) {
        _uiState.update { it.copy(hasLocationPermission = hasPermission) }
        if (hasPermission) {
            loadCurrentLocation()
        } else {
            // 권한이 없으면 기본 위치 사용
            val defaultLocation = locationService.getDefaultLocation()
            val defaultLatLng = LatLng(defaultLocation.latitude, defaultLocation.longitude)
            _uiState.update {
                it.copy(
                    currentLocation = defaultLatLng,
                    cameraPosition = defaultLatLng
                )
            }
            loadCafesWithCurrentState()
        }
    }
    
    /**
     * 현재 위치 로드
     */
    fun loadCurrentLocation() {
        viewModelScope.launch {
            try {
                val location = locationService.getCurrentLocation()
                    ?: locationService.getDefaultLocation()

                val latLng = LatLng(location.latitude, location.longitude)
                _uiState.update {
                    it.copy(
                        currentLocation = latLng,
                        cameraPosition = latLng
                    )
                }

                // 현재 위치 기준으로 카페 목록 로드
                loadCafesWithCurrentState()
            } catch (e: Exception) {
                Log.e("CafeMapViewModel", "Failed to get current location", e)
                _uiState.update { it.copy(error = "위치를 가져올 수 없습니다") }
            }
        }
    }
    
    /**
     * 지도 카메라 위치가 변경되었을 때 (디바운스 적용)
     */
    fun onCameraPositionChanged(position: LatLng, zoomLevel: Float) {
        _uiState.update {
            it.copy(
                cameraPosition = position,
                currentZoomLevel = zoomLevel
            )
        }

        // 기존 작업 취소
        cameraMovementJob?.cancel()

        // 새로운 작업 예약 (디바운스)
        cameraMovementJob = viewModelScope.launch {
            delay(CAMERA_MOVEMENT_DEBOUNCE_MS)
            loadCafesWithCurrentState(showLoading = false)
        }
    }

    /**
     * 줌 레벨에 따라 반경(km) 계산
     * 줌 레벨이 낮을수록(멀리서 볼수록) 더 넓은 반경
     */
    private fun calculateRadiusFromZoom(zoomLevel: Float): Double {
        return when {
            zoomLevel >= 17f -> 1.0   // 매우 확대: 1km
            zoomLevel >= 15f -> 3.0   // 확대: 3km
            zoomLevel >= 13f -> 5.0   // 중간: 5km
            zoomLevel >= 11f -> 10.0  // 축소: 10km
            zoomLevel >= 9f -> 20.0   // 많이 축소: 20km
            else -> 50.0              // 매우 많이 축소: 50km
        }
    }
    
    /**
     * 현재 상태로 카페 목록 로드
     */
    private fun loadCafesWithCurrentState(showLoading: Boolean = true) {
        val state = _uiState.value
        val cameraPos = state.cameraPosition ?: return
        val myLocation = state.currentLocation ?: return

        val radiusKm = calculateRadiusFromZoom(state.currentZoomLevel)

        loadCafes(
            centerLatitude = cameraPos.latitude,
            centerLongitude = cameraPos.longitude,
            myLatitude = myLocation.latitude,
            myLongitude = myLocation.longitude,
            radiusKm = radiusKm,
            keyword = state.searchKeyword.takeIf { it.isNotEmpty() },
            filter = state.filter.takeIf { it.isActive() },
            showLoading = showLoading
        )
    }

    /**
     * 카페 목록 로드
     */
    private fun loadCafes(
        centerLatitude: Double,
        centerLongitude: Double,
        myLatitude: Double,
        myLongitude: Double,
        radiusKm: Double,
        keyword: String? = null,
        filter: CafeFilter? = null,
        showLoading: Boolean = true
    ) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.update { it.copy(isInitialLoading = true, error = null) }
            }

            cafeRepository.getCafes(
                keyword = keyword,
                centerLatitude = centerLatitude,
                centerLongitude = centerLongitude,
                myLatitude = myLatitude,
                myLongitude = myLongitude,
                radiusKm = radiusKm,
                filter = filter,
                page = 0,
                size = 50
            ).onSuccess { result ->
                _uiState.update {
                    it.copy(
                        cafes = result.cafes,
                        isInitialLoading = false
                    )
                }
            }.onFailure { e ->
                Log.e("CafeMapViewModel", "Failed to load cafes", e)
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        error = e.message ?: "카페 목록을 불러올 수 없습니다"
                    )
                }
            }
        }
    }
    
    /**
     * 검색어 변경
     */
    fun onSearchKeywordChanged(keyword: String) {
        _uiState.update { it.copy(searchKeyword = keyword) }
    }
    
    /**
     * 검색 실행
     */
    fun onSearch() {
        loadCafesWithCurrentState()
    }
    
    /**
     * 필터 다이얼로그 표시/숨김
     */
    fun toggleFilterDialog() {
        _uiState.update { it.copy(isFilterDialogVisible = !it.isFilterDialogVisible) }
    }
    
    /**
     * 필터 적용
     */
    fun applyFilter(filter: CafeFilter) {
        _uiState.update { it.copy(filter = filter, isFilterDialogVisible = false) }
        loadCafesWithCurrentState()
    }
    
    /**
     * 필터 초기화
     */
    fun clearFilter() {
        _uiState.update { it.copy(filter = CafeFilter()) }
        loadCafesWithCurrentState()
    }
    
    /**
     * 바텀 시트 확장/축소
     */
    fun toggleBottomSheet() {
        _uiState.update { it.copy(isBottomSheetExpanded = !it.isBottomSheetExpanded) }
    }
    
    /**
     * 에러 메시지 클리어
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun navigateToCafeDetail(cafeId: Long) = viewModelScope.launch {
        navigator.navigate(FullScreen.CafeDetail(cafeId= cafeId))
    }
}

