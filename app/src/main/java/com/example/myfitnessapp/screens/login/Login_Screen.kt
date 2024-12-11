package com.example.myfitnessapp.screens.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myfitnessapp.R
import com.example.myfitnessapp.navigation.Screens
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider




@Composable
fun Login_Screen(
    navController: NavController,
    viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val showLoginForm = rememberSaveable {
        mutableStateOf(true)
    }



    // Google
    // este token se consigue en Firebase->Proveedores de Acceso->Google->Conf del SKD->Id de cliente web
    val token = "102189252440-i2b5v33u25544iiispcsq8mn9nm93fm1.apps.googleusercontent.com"
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts
            .StartActivityForResult() // esto abrirá un activity para hacer el login de Google
    ) {
        val task =
            GoogleSignIn.getSignedInAccountFromIntent(it.data) // esto lo facilita la librería añadida
        // el intent será enviado cuando se lance el launcher
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            viewModel.signInWithGoogleCredential(credential) {
                navController.navigate(Screens.HomeScreen.name)
            }
        } catch (ex: Exception) {
            Log.d("My Login", "GoogleSignIn falló")
        }
    }




    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()

        ) {
            if (showLoginForm.value) { //si es true crea cuenta sino inicia sesion
                Text(text = "Inicia sesion")
                UserForm(isCreateAccount = false) { email, password ->
                    Log.d("My Login", "Logueando con $email y $password")
                    viewModel.signInWithEmailAndPassword(
                        email,
                        password
                    ) {//pasamos email, password, y la funcion que navega hacia home
                        navController.navigate(Screens.HomeScreen.name)
                    }

                }
            } else {
                Text(text = "Crear una cuenta")
                UserForm(isCreateAccount = true) { email, password ->
                    Log.d("My Login", "Creando cuenta con $email y $password")
                    viewModel.createUserWithEmailAndPassword(
                        email,
                        password
                    ) {//pasamos email, password, y la funcion que navega hacia home
                        navController.navigate(Screens.HomeScreen.name)
                    }

                }
            }
            // alternar entre Crear cuenta e iniciar sesion
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val text1 = if (showLoginForm.value) "¿No tienes cuenta?"
                else "¿Ya tienes cuenta?"
                val text2 = if (showLoginForm.value) "Registrate"
                else "Inicia sesión"
                Text(text = text1)
                Text(text = text2,
                    modifier = Modifier
                        .clickable {
                            showLoginForm.value = !showLoginForm.value
                        }
                        .padding(start = 5.dp),
                    color = MaterialTheme.colorScheme.secondary)
            }


            // GOOGLE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { // Se crea un buider de opciones, una de ellas incluye un token

                        val opciones = GoogleSignInOptions
                            .Builder(
                                GoogleSignInOptions.DEFAULT_SIGN_IN
                            )
                            .requestIdToken(token) //requiere el token
                            .requestEmail() //y tb requiere el email
                            .build()
                        //creamos un cliente de logueo con estas opciones
                        val googleSingInCliente = GoogleSignIn.getClient(context, opciones)
                        launcher.launch(googleSingInCliente.signInIntent)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Login con GOOGLE",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(40.dp)
                )

                Text(
                    text = "Login con Google",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }



        }
    }
}

@Composable
fun UserForm(
    isCreateAccount: Boolean,
    onDone: (String, String) -> Unit = { email, pwd -> }
) {

    val email = rememberSaveable {
        mutableStateOf("")
    }

    val password = rememberSaveable {
        mutableStateOf("")
    }

    val passwordVisible = rememberSaveable {
        mutableStateOf(false)
    }

    val valido = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }

    //controla que al hacer clic en el boton submite, el teclado se oculte
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailInput(
            emailState = email
        )

        PasswordInput(
            passwordState = password,
            passwordVisible = passwordVisible
        )

        SubmitButton(
            textId = if (isCreateAccount) "Crear cuenta" else "Login",
            inputValido = valido
        ) {
            onDone(email.value.trim(), password.value.trim())
            //se oculta el teclado, el ? es que se llama la función en modo seguro
            keyboardController?.hide()
        }
    }
}

@Composable
fun SubmitButton(
    textId: String,
    inputValido: Boolean,
    onClic: () -> Unit
) {
    Button(
        onClick = onClic,
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        shape = CircleShape,
        enabled = inputValido
    ) {
        Text(
            text = textId,
            modifier = Modifier.padding(5.dp)
        )
    }
}


@Composable
fun EmailInput(
    emailState: MutableState<String>,
    labelId: String = "Email"
) {
    InputField(
        valuestate = emailState,
        labelId = labelId,
        keyboardType = KeyboardType.Email
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    valuestate: MutableState<String>,
    labelId: String,
    keyboardType: KeyboardType,
    isSingleLine: Boolean = true,
) {
    OutlinedTextField(
        value = valuestate.value,
        onValueChange = { valuestate.value = it },
        label = { Text(text = labelId) },
        singleLine = isSingleLine,
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)

    )
}

@Composable
fun PasswordInput(
    passwordState: MutableState<String>,
    labelId: String = "Password",
    passwordVisible: MutableState<Boolean>
) {
    val visualTransformation = if (passwordVisible.value)
        VisualTransformation.None
    else PasswordVisualTransformation()

    OutlinedTextField(
        value = passwordState.value,
        onValueChange = { passwordState.value = it },
        label = { Text(text = labelId) },
        singleLine = true,
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = visualTransformation,
        trailingIcon = {
            if (passwordState.value.isNotBlank()) {
                PasswordVisibleIcon(passwordVisible)
            } else null
        }
    )
}

@Composable
fun PasswordVisibleIcon(
    passwordVisible: MutableState<Boolean>
) {
    val image = if (passwordVisible.value)
        Icons.Default.VisibilityOff
    else
        Icons.Default.Visibility

    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
        Icon(
            imageVector = image,
            contentDescription = ""
        )
    }
}




