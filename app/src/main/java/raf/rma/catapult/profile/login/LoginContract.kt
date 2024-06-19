package raf.rma.catapult.profile.login

interface LoginContract {
    data class LoginState(
        val name: String = "",
        val nickname: String = "",
        val email: String = "",
        val isNameValid: Boolean = true,
        val isNicknameValid: Boolean = true,
        val isEmailValid: Boolean = true,
        val isProfileCreated: Boolean = false
    )

    sealed class LoginEvent {
        data class OnNameChange(val name: String) : LoginEvent()
        data class OnNicknameChange(val nickname: String) : LoginEvent()
        data class OnEmailChange(val email: String) : LoginEvent()
        data object OnCreateProfile : LoginEvent()
    }
}