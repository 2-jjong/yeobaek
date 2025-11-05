package com.ssafy.yeobaeg.presentation.profile

import android.util.Log
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
) : ViewModel() {

    fun updateProfile(newNickname: String, newImage: String?, artistDes: String?, isArtist:Boolean) =
        viewModelScope.launch {
            viewModelScope.launch {
                repository.updateProfile(
                    nickname = newNickname,
                    profileImageUrl = null,
                    introduction = artistDes,
                    isArtist = isArtist
                ).onSuccess {
                    navigator.navigateBack() // 성공 시 이동
                }.onFailure { e ->
                    Log.e("ProfileUpdate", "프로필 수정 실패", e)
                }
            }
        }

    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }
}
