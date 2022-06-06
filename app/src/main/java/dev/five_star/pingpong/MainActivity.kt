package dev.five_star.pingpong

import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hbb20.CountryCodePicker
import dev.five_star.pingpong.ui.theme.PingXPongTheme


class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            @Suppress("UNCHECKED_CAST")
            return VerifyViewModel(telephonyManager) as T
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel : VerifyViewModel = viewModel(factory = factory)
            EnterPhoneScreen(viewModel)
        }
    }
}

@Composable
fun EnterPhoneScreen(viewModel: VerifyViewModel) {

    PingXPongTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {

                EnterPhoneDescription(Modifier.padding(bottom = 48.dp))

                EnterPhoneHumber(viewModel)

                VerifyNumber(
                    Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp)
                ) {
                    viewModel.verifyNumber()
                }
            }
        }
    }
}

@Composable
fun EnterPhoneDescription(modifier: Modifier = Modifier) = Column(modifier) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        fontSize = 30.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.verify_headline)
    )

    Text(
        modifier = Modifier.padding(top = 24.dp),
        fontSize = 18.sp,
        text = stringResource(R.string.verify_description)
    )
}

@Composable
fun EnterPhoneHumber(
    viewModel: VerifyViewModel,
    modifier: Modifier = Modifier,
) = Column(modifier) {

    val currentState: State<VerifyUiState> = viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            CountryCodePicker(context).apply {
                layoutParams.apply {
                    LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    gravity = Gravity.CENTER
                    setOnCountryChangeListener {
                        viewModel.setPrefix(selectedCountryCodeWithPlus, selectedCountryNameCode)
                        showDialog = false
                    }
                }
                setCountryForNameCode(viewModel.uiState.value.countryCode)
                viewModel.setPrefix(selectedCountryCodeWithPlus, selectedCountryNameCode)
                if (showDialog) {
                    launchCountrySelectionDialog()
                }
            }
        }
    )

    Divider(Modifier.padding(8.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {

        OutlinedTextField(
            value = currentState.value.prefix,
            modifier = Modifier
                .weight(2f)
                .padding(end = 8.dp)
                .clickable {
                    Log.d(TAG, "clicked")
                    showDialog = true
                },
            textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            enabled = false,
            onValueChange = {
                viewModel.setPrefix(it, "")
            },
            label = {
                Text(text = currentState.value.countryCode)
            }
        )

        OutlinedTextField(
            value = currentState.value.number,
            modifier = Modifier
                .fillMaxWidth()
                .weight(5f),
            textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            onValueChange = {
                viewModel.setNumber(it)
            },
            label = { 
                Text(text = stringResource(R.string.phone_number))
            }
        )
    }

}

@Composable
fun VerifyNumber(modifier: Modifier = Modifier, onVerifyClicked: () -> Unit) {
    OutlinedButton(modifier = modifier, onClick = {
        onVerifyClicked()
    }) {
        Text(text = stringResource(R.string.verify))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EnterPhoneScreen(viewModel = viewModel())
}