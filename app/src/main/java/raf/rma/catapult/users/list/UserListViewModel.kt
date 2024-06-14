package raf.rma.catapult.users.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import raf.rma.catapult.users.db.UserProfile
import raf.rma.catapult.users.list.UserListContract.UserListState
import raf.rma.catapult.users.list.model.UserUiModel
import raf.rma.catapult.users.repository.UsersRepository
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val repository: UsersRepository
) : ViewModel(){

    private val _state = MutableStateFlow(UserListState())
    val state = _state.asStateFlow()
    private fun setState(reducer: UserListState.() -> UserListState) = _state.update(reducer)

    init {
        fetchAllUsers()
        observeUserProfiles()
    }

    private fun observeUserProfiles(){
        viewModelScope.launch {
            setState { copy(loading = true) }
            repository.observeAllUserProfiles()
                .distinctUntilChanged()
                .collect {
                    setState {
                        copy(
                            loading = false,
                            users = it.map { it.asUserUiModel() }
                        )
                    }
                }
        }
    }

    private fun fetchAllUsers(){
        viewModelScope.launch {
            setState { copy(updating = true) }
            try {
                withContext(Dispatchers.IO) {
                    repository.fetchAllUsers()
                }
            } catch (error: Exception) {
                Log.e("Greska", "Message", error)
            } finally {
                setState { copy(updating = false) }
            }
        }
    }

    private fun UserProfile.asUserUiModel() = UserUiModel(
        id = this.id,
        name = this.name,
        username = this.username,
        albumsCount = this.albumsCount
    )
}