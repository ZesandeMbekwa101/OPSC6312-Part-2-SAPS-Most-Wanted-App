package com.example.sapsmostwantedapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sapsmostwantedapp.data.model.WantedPerson
import com.example.sapsmostwantedapp.data.network.RetrofitInstance
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WantedViewModel : ViewModel() {

    private val _wanted = MutableLiveData<List<WantedPerson>>(emptyList())
    val wanted: LiveData<List<WantedPerson>> = _wanted

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadWanted() {
        // prevent duplicate loads
        if (_loading.value == true) return

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val response = RetrofitInstance.api.getAllWantedPersons()
                if (response.isSuccessful) {
                    val raw = response.body() ?: ""
                    // parse on Default dispatcher
                    val parsed = withContext(Dispatchers.Default) {
                        val gson = Gson()
                        val temp = mutableListOf<WantedPerson>()
                        raw.lines().forEach { line ->
                            if (line.isNotBlank()) {
                                try {
                                    val item = gson.fromJson(line, WantedPerson::class.java)
                                    temp.add(item)
                                } catch (e: Exception) {
                                    // skip parsing errors but keep going
                                    e.printStackTrace()
                                }
                            }
                        }
                        temp.toList()
                    }
                    _wanted.value = parsed
                } else {
                    _error.value = "HTTP ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }
}