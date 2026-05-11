package com.example.paryavarankavalu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.paryavarankavalu.data.ReportStore
import com.example.paryavarankavalu.model.Report

private val GreenDark = Color(0xFF14532D)
private val GreenPrimary = Color(0xFF2E7D32)
private val GreenLight = Color(0xFFB9F6A5)

private val CreamBg = Color(0xFFF8F5F0)
private val CardBg = Color.White

private val RedSoft = Color(0xFFFFD6D6)
private val YellowSoft = Color(0xFFFFEDB5)

@Composable
fun DashboardScreen() {

    val reports = ReportStore.reports

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .background(CreamBg)
            .padding(horizontal = 16.dp),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        item {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            DashboardHeader()
        }

        items(reports) { report ->

            VolunteerReportCard(report)
        }

        item {

            WeeklyProgressCard()

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}

@Composable
fun DashboardHeader() {

    Row(

        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween,

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(

                text = "Volunteer Dashboard",

                fontSize = 28.sp,

                fontWeight = FontWeight.Bold,

                color = GreenDark
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(

                text =
                    "Welcome back, Guardian. Here are the active reports in your vicinity.",

                color = Color.Gray,

                fontSize = 13.sp,

                lineHeight = 18.sp
            )
        }

        Surface(

            shape = CircleShape,

            color = Color.White,

            tonalElevation = 2.dp
        ) {

            Box(

                modifier = Modifier.size(44.dp),

                contentAlignment = Alignment.Center
            ) {

                Icon(

                    imageVector = Icons.Default.Person,

                    contentDescription = null,

                    tint = GreenDark
                )
            }
        }
    }
}

@Composable
fun VolunteerReportCard(
    report: Report
) {

    val severityColor =

        when (report.severity.uppercase()) {

            "HIGH" -> RedSoft

            "MEDIUM" -> YellowSoft

            else -> GreenLight
        }

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(
            containerColor = CardBg
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(

            modifier = Modifier.padding(14.dp)
        ) {

            // ─────────────────────────────
            // TOP CONTENT
            // ─────────────────────────────
            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                AsyncImage(

                    model = report.imageUri,

                    contentDescription = null,

                    contentScale = ContentScale.Crop,

                    modifier = Modifier
                        .size(92.dp)
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                )

                Column(

                    modifier = Modifier.weight(1f)
                ) {

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.SpaceBetween,

                        verticalAlignment =
                            Alignment.Top
                    ) {

                        Text(

                            text = report.category,

                            fontWeight =
                                FontWeight.Bold,

                            fontSize = 20.sp,

                            color = GreenDark,

                            modifier = Modifier.weight(1f)
                        )

                        Surface(

                            shape =
                                RoundedCornerShape(50),

                            color = severityColor
                        ) {

                            Text(

                                text =
                                    "${report.severity} Severity",

                                modifier = Modifier.padding(

                                    horizontal = 10.dp,

                                    vertical = 5.dp
                                ),

                                fontSize = 11.sp,

                                fontWeight =
                                    FontWeight.SemiBold,

                                color = GreenDark
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(

                        text =
                            "📍 Mangaluru, Karnataka",

                        color = Color.Gray,

                        fontSize = 13.sp
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(

                        text =
                            "Reported 2 hours ago",

                        color = Color.Gray,

                        fontSize = 12.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // ─────────────────────────────
            // BUTTON
            // ─────────────────────────────
            Button(

                onClick = {

                    report.status = "Cleaned"
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),

                shape = RoundedCornerShape(50),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            GreenPrimary
                    )
            ) {

                Icon(

                    imageVector =
                        Icons.Default.CheckCircle,

                    contentDescription = null,

                    tint = Color.White
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(

                    text = "Mark as Cleaned",

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun WeeklyProgressCard() {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(
            containerColor = GreenLight
        )
    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column {

                Text(

                    text = "Weekly Progress",

                    fontWeight =
                        FontWeight.Bold,

                    fontSize = 18.sp,

                    color = GreenDark
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(

                    text =
                        "12 cleanup actions this week",

                    color =
                        GreenDark.copy(alpha = 0.8f),

                    fontSize = 13.sp
                )
            }

            Box(
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(

                    progress = { 0.75f },

                    color = GreenDark,

                    trackColor =
                        Color.White.copy(alpha = 0.5f),

                    strokeWidth = 7.dp,

                    modifier = Modifier.size(62.dp)
                )

                Text(

                    text = "75%",

                    fontWeight =
                        FontWeight.Bold,

                    color = GreenDark,

                    fontSize = 14.sp
                )
            }
        }
    }
}