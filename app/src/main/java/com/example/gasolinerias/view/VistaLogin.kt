package com.example.gasolinerias.view

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gasolinerias.viewModel.AuthViewModel
import com.example.gasolinerias.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaLogin(navController: NavController, authViewModel: AuthViewModel, activity: ComponentActivity) {

    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                authViewModel.firebaseAuthWithGoogle(account, {
                    navController.navigate("maps")
                }, { error ->
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
                })
            } catch (e: ApiException) {
//                Log.e("GoogleSignIn", "Google sign in failed", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.login_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text(stringResource(id = R.string.email_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        TextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text(stringResource(id = R.string.password_hint)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                authViewModel.login(emailState.value, passwordState.value, onSuccess = {
                    navController.navigate("maps")
//                    Log.e("", "Sesion iniciada correctamente")
                }, {error ->
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
                })
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7E46)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = stringResource(id = R.string.login_button), color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate("register") }
        ) {
            Text(text = stringResource(id = R.string.register_prompt), color = Color.Blue)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(activity.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                val googleSignInClient = GoogleSignIn.getClient(activity, gso)
                launcher.launch(googleSignInClient.signInIntent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDB4437)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = stringResource(id = R.string.google_login_button), color = Color.White)
        }

        Button(
            onClick = {  authViewModel.anonymousLogin(onSuccess = {
                navController.navigate("maps")
//                Log.e("", "Sesion iniciada correctamente")
            }, onError = {
                Log.e("", it)
            } )},
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF707070)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = stringResource(id = R.string.anonymous_login_button), color = Color.White)
        }
    }
}
