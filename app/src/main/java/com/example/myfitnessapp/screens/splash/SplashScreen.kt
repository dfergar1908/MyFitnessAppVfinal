package com.example.myfitnessapp.screens.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitnessapp.R
import com.example.myfitnessapp.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.8f,
            animationSpec = tween(
                durationMillis = 2000,
                easing = {
                    OvershootInterpolator(8f).getInterpolation(it)
                }
            )
        )

        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        )

        delay(2000)

        if (FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()) {
            navController.navigate(Screens.LoginScreen.name)
        } else {
            navController.navigate(Screens.HomeScreen.name) {
                popUpTo(Screens.SplashScreen.name) {
                    inclusive = true
                }
            }
        }
    }

    val color = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(
            colors = listOf(color.copy(alpha = 0.1f), backgroundColor),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )
    )) {
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .size(250.dp)
                .scale(scale.value),
            shape = CircleShape,
            border = BorderStroke(3.dp, color)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            }
        }


        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "FITNESS APP",
                style = MaterialTheme.typography.titleLarge,
                color = color.copy(alpha = 0.7f),
                modifier = Modifier.alpha(alpha.value)
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.titleSmall,
                color = color.copy(alpha = 0.8f),
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}

