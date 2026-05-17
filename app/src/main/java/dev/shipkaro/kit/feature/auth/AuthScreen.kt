package dev.shipkaro.kit.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    vm: AuthViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state.signedIn) {
        if (state.signedIn) onAuthenticated()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.auth_title))
        if (state.loading) {
            CircularProgressIndicator()
        } else {
            Button(onClick = vm::signIn) { Text(stringResource(R.string.auth_continue)) }
        }
    }
}
