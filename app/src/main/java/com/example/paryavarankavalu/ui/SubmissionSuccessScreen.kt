package com.example.paryavarankavalu.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

private val GreenPrimary =
    Color(0xFF2E7D32)

private val BgColor =
    Color(0xFFF5EFE6)

@Composable
fun SubmissionSuccessScreen(
    navController: NavHostController
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor),

        contentAlignment = Alignment.Center
    ) {

        Card(

            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),

            shape = RoundedCornerShape(24.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "✅",
                    fontSize = 72.sp
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(

                    text =
                        "Report Submitted!",

                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(

                    text =
                        "Your environmental report has been successfully submitted to the community dashboard.",

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,

                    color = Color.Gray
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Button(

                    onClick = {

                        navController.navigate(
                            "dashboard"
                        ) {

                            popUpTo("report") {
                                inclusive = true
                            }
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                GreenPrimary
                        ),

                    shape =
                        RoundedCornerShape(50)
                ) {

                    Text(
                        "Go to Dashboard"
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Button(

                    onClick = {

                        navController.navigate(
                            "report"
                        ) {

                            popUpTo("report") {
                                inclusive = true
                            }
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color.LightGray
                        ),

                    shape =
                        RoundedCornerShape(50)
                ) {

                    Text(
                        "Submit Another Report",

                        color = Color.Black
                    )
                }
            }
        }
    }
}