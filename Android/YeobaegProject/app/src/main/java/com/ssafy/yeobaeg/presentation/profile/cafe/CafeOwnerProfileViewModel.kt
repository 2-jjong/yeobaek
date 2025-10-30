package com.ssafy.yeobaeg.presentation.profile.cafe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CafeOwnerProfileViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel(){
    fun navigateToEditProfile() = viewModelScope.launch {
        navigator.navigate(FullScreen.ProfileEdit)
    }
    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }

}