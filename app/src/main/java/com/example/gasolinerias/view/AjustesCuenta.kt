package com.example.gasolinerias.view

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.navigation.NavHostController
import com.example.gasolinerias.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesCuenta(
    navController: NavController,
    innerNavController: NavHostController,
    context: Context
) {
    val newPasswordState = remember { mutableStateOf("") }
    val oldPasswordState = remember { mutableStateOf("") }
    val newUsernameState = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }

    var user = FirebaseAuth.getInstance().currentUser
    var provider: String?
    if (user?.providerData?.size!! > 1) {
        provider = user.providerData[1]?.providerId
    } else {
        provider = user.providerData[0]?.providerId
    }


    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(id = R.string.delete_account)) },
            text = { Text(stringResource(id = R.string.delete_confirmation_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteAccount()
                        showDialog.value = false
                        navController.navigate("login")
                    }
                ) {
                    Text(text = stringResource(id = R.string.delete_account_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog.value = false }
                ) {
                    Text(text = stringResource(id = R.string.delete_account_dialog_cancel))
                }
            }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // Align items to the top
    ) {
        // Add Back Button Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(onClick = { innerNavController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.back),
                    tint = Color.Black
                )
                Text(text = stringResource(id = R.string.back), color = Color.Black)
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.account_settings_title),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))



            if (provider != null && provider == "password") {
                TextField(
                    value = oldPasswordState.value,
                    onValueChange = { oldPasswordState.value = it },
                    label = { Text(text = stringResource(id = R.string.old_password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                TextField(
                    value = newPasswordState.value,
                    onValueChange = { newPasswordState.value = it },
                    label = { Text(text = stringResource(id = R.string.new_password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )


                Button(
                    onClick = {
                        if(!oldPasswordState.value.isEmpty() || !newPasswordState.value.isEmpty()){
                            val credential = EmailAuthProvider.getCredential(
                                user.email.toString(),
                                oldPasswordState.value
                            )
                            user.reauthenticate(credential)?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    user.updatePassword(newPasswordState.value)
                                        .addOnCompleteListener {
                                            Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
                                                .show()
                                        }

                                } else {
                                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT)
                                        .show()
//                                Log.e(TAG, "Error reauthenticating user", it.exception)
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please enter both old and new passwords", Toast.LENGTH_SHORT).show()
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(vertical = 8.dp),
                    colors =
                    ButtonDefaults.buttonColors(containerColor = Color(0xFF004080)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = stringResource(id = R.string.modify_password), color = Color.White)
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showDialog.value = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(id = R.string.delete_account), color = Color.White)
            }

        }
    }
}

fun deleteAccount() {
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user!!.uid
    user?.delete()
        ?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // La cuenta se ha eliminado exitosamente
                Log.d(TAG, "Cuenta eliminada correctamente")
                deleteGasolinerasUsuario(uid)
                // Aquí puedes realizar cualquier acción adicional después de eliminar la cuenta
            } else {
                // Error al intentar eliminar la cuenta
                Log.w(TAG, "Error al eliminar la cuenta", task.exception)
                // Aquí puedes manejar el error según tu aplicación
            }
        }
}


fun deleteGasolinerasUsuario(uid: String) {
    val db = Firebase.firestore
    val gasolinerasRef = db.collection("gasolineras")
    gasolinerasRef.whereEqualTo("uid", uid)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                document.reference.delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "Gasolinera eliminada correctamente")
                        // Aquí puedes realizar cualquier acción adicional después de eliminar la gasolinera
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error al eliminar la gasolinera", e)
                        // Manejar el error si falla la eliminación de la gasolinera
                    }
            }
        }
        .addOnFailureListener { exception ->
            // Maneja los errores en la recuperación de datos
            Log.w(TAG, "Error al recuperar las gasolineras por UID", exception)
            // Puedes manejar el error según tu aplicación
        }
}

