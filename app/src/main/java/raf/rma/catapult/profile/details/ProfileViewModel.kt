package raf.rma.catapult.profile.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import raf.rma.catapult.profile.datastore.ProfileDataStore
import raf.rma.catapult.profile.details.ProfileContract.ProfileEvent
import raf.rma.catapult.profile.details.ProfileContract.ProfileState
import raf.rma.catapult.quiz.repository.QuizRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileDataStore: ProfileDataStore,
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()
    private fun setState(reducer: ProfileState.() -> ProfileState) = _state.update(reducer)

    private val events = MutableSharedFlow<ProfileEvent>()
    fun setEvent(event: ProfileEvent) = viewModelScope.launch { events.emit(event) }

    init {
        observeEvents()
        loadProfileData()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is ProfileEvent.EditProfile -> {
                        if (event.name != state.value.name){
                            profileDataStore.updateFullName(event.name)
                            setState { copy(name = event.name) }
                        }
                        if (event.email != state.value.email) {
                            profileDataStore.updateEmail(event.email)
                            setState { copy(email = event.email) }
                        }
                        if (event.nickname != state.value.nickname){
                            profileDataStore.updateNickname(event.nickname)
                            setState { copy(nickname = event.nickname) }
                        }
                    }
                }
            }
        }
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            val profileData = profileDataStore.data.first()
            val quizResultsFlow = quizRepository.getAllQuizResults(profileData.nickname)
            val bestScore = quizRepository.getBestScore(profileData.nickname) ?: 0f
            val bestPosition = quizRepository.getBestPosition(profileData.nickname)

            quizResultsFlow.collect { quizResults ->
                setState {
                    copy(
                        name = profileData.fullName,
                        nickname = profileData.nickname,
                        email = profileData.email,
                        quizResults = quizResults,
                        bestScore = bestScore,
                        bestPosition = bestPosition
                    )
                }
            }
        }
    }
}