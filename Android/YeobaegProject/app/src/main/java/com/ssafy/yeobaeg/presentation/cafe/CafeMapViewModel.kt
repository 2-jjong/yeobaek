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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CafeMapUiState(
    val cafes: List<CafeSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentLocation: LatLng? = null,
    val cameraPosition: LatLng? = null,
    val searchKeyword: String = "",
    val filter: CafeFilter = CafeFilter(),
    val isFilterDialogVisible: Boolean = false,
    val isBottomSheetExpanded: Boolean = false,
    val userRole: UserRole? = null,
    val hasLocationPermission: Boolean = false
)

@HiltViewModel
class CafeMapViewModel @Inject constructor(
    private val cafeRepository: CafeRepository,
    private val locationService: LocationService,
    private val authSessionManager: AuthSessionManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CafeMapUiState())
    val uiState: StateFlow<CafeMapUiState> = _uiState.asStateFlow()
    
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
            loadCafes(defaultLocation.latitude, defaultLocation.longitude)
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
                loadCafes(location.latitude, location.longitude)
            } catch (e: Exception) {
                Log.e("CafeMapViewModel", "Failed to get current location", e)
                _uiState.update { it.copy(error = "위치를 가져올 수 없습니다") }
            }
        }
    }
    
    /**
     * 지도 카메라 위치가 변경되었을 때
     */
    fun onCameraPositionChanged(position: LatLng) {
        _uiState.update { it.copy(cameraPosition = position) }
        // 카메라 위치 기준으로 카페 목록 다시 로드
        loadCafes(position.latitude, position.longitude)
    }
    
    /**
     * 카페 목록 로드
     */
    fun loadCafes(
        latitude: Double,
        longitude: Double,
        keyword: String? = null,
        filter: CafeFilter? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            cafeRepository.getCafes(
                keyword = keyword?.takeIf { it.isNotEmpty() },
                latitude = latitude,
                longitude = longitude,
                filter = filter?.takeIf { it.isActive() },
                page = 0,
                size = 50
            ).onSuccess { result ->
                _uiState.update { 
                    it.copy(
                        cafes = result.cafes,
                        isLoading = false
                    )
                }
            }.onFailure { e ->
                Log.e("CafeMapViewModel", "Failed to load cafes", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
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
        val state = _uiState.value
        val position = state.cameraPosition ?: state.currentLocation ?: return
        
        loadCafes(
            latitude = position.latitude,
            longitude = position.longitude,
            keyword = state.searchKeyword,
            filter = state.filter
        )
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
        
        val position = _uiState.value.cameraPosition ?: _uiState.value.currentLocation ?: return
        loadCafes(
            latitude = position.latitude,
            longitude = position.longitude,
            keyword = _uiState.value.searchKeyword,
            filter = filter
        )
    }
    
    /**
     * 필터 초기화
     */
    fun clearFilter() {
        _uiState.update { it.copy(filter = CafeFilter()) }
        
        val position = _uiState.value.cameraPosition ?: _uiState.value.currentLocation ?: return
        loadCafes(
            latitude = position.latitude,
            longitude = position.longitude,
            keyword = _uiState.value.searchKeyword,
            filter = null
        )
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
}

