package dev.five_star.pingpong

import android.telephony.TelephonyManager
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

const val TAG = "VerifyViewModel"

data class VerifyUiState(
    var prefix: String = "",
    var number: String = "",
    var countryCode: String = "de"
) {
    fun getPhoneNumber() : String {
        val correctNumber = number.toInt().toString()
        return "$prefix$correctNumber"
    }
}

class VerifyViewModel(private val telephonyManager: TelephonyManager) : ViewModel() {

    private val _uiState = MutableStateFlow(VerifyUiState())
    val uiState: StateFlow<VerifyUiState> get() = _uiState

    init {
        initCountryCode()
    }

    private fun initCountryCode() {
        val countryCode: String = try {
            telephonyManager.simCountryIso
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            "de"
        }

        _uiState.value = uiState.value.copy(
            countryCode = countryCode
        )
    }

//    private val _prefix = mutableStateOf("")
//    val prefix = _prefix

//    private val _number = MutableStateFlow("")
//    val number: StateFlow<String> = _number

    fun setPrefix(prefix: String, countryCode: String) {
        _uiState.value = uiState.value.copy(
            prefix = prefix,
            countryCode = countryCode
        )
    }

    fun setNumber(number: String) {
        _uiState.value = uiState.value.copy(
            number = number
        )
    }

    fun verifyNumber() {
        val correctNumber = uiState.value.number.toInt().toString()
        _uiState.value = uiState.value.copy(
            number = correctNumber
        )
        Log.d(TAG, uiState.value.getPhoneNumber())
    }

}