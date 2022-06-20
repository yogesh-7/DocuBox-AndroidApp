package com.docubox.ui.screens.main.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.docubox.data.modes.local.StorageItem
import com.docubox.data.repo.StorageRepo
import com.docubox.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val storageRepo: StorageRepo) : ViewModel() {

    private val _uiState = MutableStateFlow(SharedScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SharedScreenEvents>()
    val events = _events.asSharedFlow()

    private val isSharedByMeState = MutableStateFlow(false)

    init {
        collectIsSharedByMeState()
    }
    private fun collectIsSharedByMeState() = viewModelScope.launch {
        isSharedByMeState.collectLatest {
            _uiState.update { state -> state.copy(isSharedByMeState = it) }
            getSharedFiles(it)
        }
    }

    private suspend fun getSharedFiles(isSharedByMe: Boolean) {
        val reqFlow =
            if (isSharedByMe) storageRepo.getFilesSharedByMe() else storageRepo.getFilesSharedToMe()
        reqFlow.collectLatest {
            _uiState.emit(uiState.value.copy(isLoading = it is Resource.Loading))
            when (it) {
                is Resource.Error -> _events.emit(SharedScreenEvents.ShowToast(it.message))
                is Resource.Loading -> Unit
                is Resource.Success -> it.data?.let(this::handleSharedFilesSuccess)
            }
        }
    }

    private fun handleSharedFilesSuccess(files: List<StorageItem.File>) {
        _uiState.update { it.copy(storageItems = files) }
    }

    fun onSharedByMeButtonPress() = viewModelScope.launch {
        isSharedByMeState.emit(true)
    }
    fun onSharedToMeButtonPress() = viewModelScope.launch {
        isSharedByMeState.emit(false)
    }
}