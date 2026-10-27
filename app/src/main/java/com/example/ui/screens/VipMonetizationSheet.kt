package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.privilege.AppVersionMode
import com.example.ui.components.GlassCard
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald

enum class VipPlan(val period: String, val priceInr: Int, val tag: String?, val discountLabel: String) {
    MONTHLY("1 Month", 25, null, "Standard"),
    QUARTERLY("3 Months", 50, "POPULAR", "Save 33%"),
    ANNUAL("1 Year", 179, "BEST VALUE", "Save 40%")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipMonetizationSheet(
    activeAppMode: AppVersionMode,
    isVip: Boolean,
    onPurchaseSuccess: (VipPlan) -> Unit,
    onDismiss: () -> Unit
) {
    val glassColors = LocalGlassColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedPlan by remember { mutableStateOf(VipPlan.QUARTERLY) }
    var selectedUpiApp by remember { mutableStateOf("Google Pay") }
    var customUpiId by remember { mutableStateOf("") }
    var showSuccessBanner by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = glassColors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("vip_monetization_sheet")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonAmber.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "VIP",
                            tint = NeonAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "VoltCare VIP Upgrade",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.textPrimary
                        )
                        Text(
                            text = if (activeAppMode == AppVersionMode.LITE)
                                "Play Store Billing • Ad-Free"
                            else
                                "Razorpay & Direct UPI • Pro Unlocked",
                            fontSize = 11.sp,
                            color = glassColors.textSecondary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = glassColors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // VIP Feature Highlights
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VipPerkItem("Zero Ads", "AdMob removed")
                VipPerkItem("Safe Limits", "42°C auto-alert")
                VipPerkItem("Pro Engine", "Root / Shizuku")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pricing Plans
            Text(
                text = "Choose Subscription Plan",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = glassColors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            VipPlan.values().forEach { plan ->
                val isSelected = plan == selectedPlan
                PlanRow(
                    plan = plan,
                    isSelected = isSelected,
                    onClick = { selectedPlan = plan }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Payment Gateway Section: Lite vs Pro
            if (activeAppMode == AppVersionMode.LITE) {
                // Google Play Billing
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 12.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = "Play Billing",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Google Play In-App Billing",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = glassColors.textPrimary
                            )
                            Text(
                                text = "Supports Google Play Balance, Netbanking, Cards & UPI",
                                fontSize = 10.sp,
                                color = glassColors.textSecondary
                            )
                        }
                    }
                }
            } else {
                // Razorpay / UPI for Pro
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 12.dp
                ) {
                    Column {
                        Text(
                            text = "Direct UPI & Razorpay Payment",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonAmber
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("Google Pay", "PhonePe", "Paytm", "BHIM").forEach { upiName ->
                                val isChosen = selectedUpiApp == upiName
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isChosen) NeonAmber.copy(alpha = 0.25f)
                                            else if (glassColors.isDark) Color(0x331E293B) else Color(0x1AE2E8F0)
                                        )
                                        .clickable { selectedUpiApp = upiName }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = upiName,
                                        fontSize = 11.sp,
                                        fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isChosen) NeonAmber else glassColors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subscribe Button
            Button(
                onClick = {
                    showSuccessBanner = true
                    onPurchaseSuccess(selectedPlan)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("subscribe_vip_button")
            ) {
                Text(
                    text = if (activeAppMode == AppVersionMode.LITE)
                        "Subscribe with Play Billing • ₹${selectedPlan.priceInr}"
                    else
                        "Pay with $selectedUpiApp • ₹${selectedPlan.priceInr}",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PlanRow(
    plan: VipPlan,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val glassColors = LocalGlassColors.current

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("vip_plan_${plan.period.replace(" ", "_").lowercase()}"),
        cornerRadius = 12.dp,
        borderWidth = if (isSelected) 2.dp else 1.dp,
        customBorderColor = if (isSelected) NeonAmber else null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, if (isSelected) NeonAmber else glassColors.textSecondary, CircleShape)
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(NeonAmber)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = plan.period,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.textPrimary
                        )
                        if (plan.tag != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonAmber)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = plan.tag,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                    Text(
                        text = plan.discountLabel,
                        fontSize = 11.sp,
                        color = NeonEmerald
                    )
                }
            }

            Text(
                text = "₹${plan.priceInr}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) NeonAmber else glassColors.textPrimary
            )
        }
    }
}

@Composable
private fun VipPerkItem(title: String, subtitle: String) {
    val glassColors = LocalGlassColors.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(NeonEmerald.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = NeonEmerald,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = glassColors.textPrimary)
        Text(text = subtitle, fontSize = 9.sp, color = glassColors.textSecondary)
    }
}
