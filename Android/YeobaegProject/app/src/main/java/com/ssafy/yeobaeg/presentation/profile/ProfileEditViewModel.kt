package com.ssafy.yeobaeg.presentation.profile

import android.media.Image
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.repository.ProfileRepository
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val navigator: Navigator,
    )  : ProfileViewModel(navigator){

    fun updateProfileNickname(newNickname: String, newImage: String? ,artistDes:String?) = viewModelScope.launch {
        viewModelScope.launch {
            repository.updateProfile(
                nickname = newNickname,
                profileImageUrl = null,
                introduction = null
            ).onSuccess {
                navigator.navigateBack() // 성공 시 이동
            }.onFailure { e ->
                Log.e("ProfileUpdate", "프로필 수정 실패", e)
            }
        }
    }
}