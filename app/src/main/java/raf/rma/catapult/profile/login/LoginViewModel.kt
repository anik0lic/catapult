package raf.rma.catapult.profile.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import raf.rma.catapult.cats.list.CatListContract.CatListState
import raf.rma.catapult.profile.datastore.ProfileData
import raf.rma.catapult.profile.datastore.ProfileDataStore
import raf.rma.catapult.profile.login.LoginContract.LoginEvent
import raf.rma.catapult.profile.login.LoginContract.LoginState
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val profileDataStore: ProfileDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()
    private fun setState(reducer: LoginState.() -> LoginState) = _state.update(reducer)

    private val events = MutableSharedFlow<LoginEvent>()
    fun setEvent(event: LoginEvent) = viewModelScope.launch { events.emit(event) }

    init {
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is LoginEvent.OnNameChange -> {
                        setState { copy(name = event.name, isNameValid = event.name.isNotBlank()) }
                    }
                    is LoginEvent.OnNicknameChange -> {
                        setState { copy(nickname = event.nickname, isNicknameValid = event.nickname.matches(Regex("^[a-zA-Z0-9_]*$"))) }
                    }
                    is LoginEvent.OnEmailChange -> {
                        setState { copy(email = event.email, isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(event.email).matches()) }
                    }
                    LoginEvent.OnCreateProfile -> {
                        if (state.value.isNameValid && state.value.isNicknameValid && state.value.isEmailValid) {
                            val newProfileData = ProfileData(
                                fullName = state.value.name,
                                nickname = state.value.nickname,
                                email = state.value.email
                            )
                            profileDataStore.updateProfileData(newProfileData)
                            setState { copy(isProfileCreated = true) }
                        }
                    }
                }
            }
        }
    }
}