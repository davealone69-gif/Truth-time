package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.AuraViewModel

val AccentPurple = Color(0xFF904EDD)
val BgDeep = Color(0xFF0B0B10)
val PanelDark = Color(0xFF12121A)
val BorderDark = Color(0xFF222230)
val TextGray = Color(0xFFA0A0B0)
val TextWhite = Color(0xFFFFFFFF)

@Composable
fun AvatarCreatorScreen(
    viewModel: AuraViewModel,
    modifier: Modifier = Modifier,
) {
    val advSpec by viewModel.advancedAvatarSpec.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generatedImage by viewModel.generatedImage.collectAsState()

    // Using a horizontal scroll state just in case it runs on a narrow screen,
    // to preserve the complex tablet/desktop layout requested.
    Row(
        modifier =
            modifier
                .fillMaxSize()
                .background(BgDeep)
                .horizontalScroll(rememberScrollState()),
    ) {
        // 1. Far Left Nav
        DesignSideNav(modifier = Modifier.width(80.dp).fillMaxHeight())

        VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = 1.dp, color = BorderDark)

        // 2. Rest of the screen
        Column(modifier = Modifier.width(1200.dp).fillMaxHeight()) {
            // Top Bar
            TopDesignBar()
            HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = BorderDark)

            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // 2a. Presets
                PresetsColumn(modifier = Modifier.width(130.dp).fillMaxHeight())
                VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = 1.dp, color = BorderDark)

                // 2b. Center Content
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    // Main Image Area
                    MainPreviewArea(modifier = Modifier.weight(1f).fillMaxWidth(), generatedImage = generatedImage, isGenerating = isGenerating)
                    HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = BorderDark)

                    // Bottom Details
                    BottomDetailArea(modifier = Modifier.height(320.dp).fillMaxWidth())
                }

                VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = 1.dp, color = BorderDark)

                // 2c. Right Appearance Panel
                Column(modifier = Modifier.width(340.dp).fillMaxHeight()) {
                    AppearancePanel(
                        age = advSpec.age.toFloat(),
                        onAgeChange = { viewModel.updateAdvancedAvatarSpec(age = it.toInt()) },
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                    HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = BorderDark)

                    // Bottom Action Bar
                    ActionBar(modifier = Modifier.height(80.dp).fillMaxWidth(), onGenerateClick = { viewModel.generateAvatar() })
                }
            }
        }
    }
}

@Composable
fun DesignSideNav(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(BgDeep).padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Logo
        Icon(Icons.Default.Hexagon, contentDescription = "Logo", tint = AccentPurple, modifier = Modifier.size(36.dp))
        Spacer(modifier = Modifier.height(32.dp))

        // Nav Items
        SideNavItem("APPEARANCE", Icons.Outlined.Face, selected = true)
        SideNavItem("BODY", Icons.Outlined.Accessibility, selected = false)
        SideNavItem("CLOTHING", Icons.Outlined.Checkroom, selected = false)
        SideNavItem("HAIR", Icons.Outlined.FaceRetouchingNatural, selected = false)
        SideNavItem("FACE", Icons.Outlined.SentimentSatisfied, selected = false)
        SideNavItem("EYES", Icons.Outlined.Visibility, selected = false)
        SideNavItem("ACCESSORIES", Icons.Outlined.Watch, selected = false)
        SideNavItem("AUGMENTS", Icons.Outlined.PrecisionManufacturing, selected = false)
        SideNavItem("TATTOOS", Icons.Outlined.Brush, selected = false)
        SideNavItem("ANIMATIONS", Icons.Default.DirectionsRun, selected = false)

        Spacer(modifier = Modifier.weight(1f))

        SideNavItem("", Icons.Outlined.Token, selected = false)
        SideNavItem("", Icons.Outlined.Search, selected = false)
        SideNavItem("", Icons.Outlined.Settings, selected = false)

        Spacer(modifier = Modifier.height(16.dp))
        Icon(Icons.Default.WorkspacePremium, contentDescription = "Premium", tint = Color(0xFFFFD700))
        Text("PREMIUM", color = Color(0xFFFFD700), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Icon(Icons.Default.HelpOutline, contentDescription = "Help", tint = TextGray)
    }
}

@Composable
fun SideNavItem(
    title: String,
    icon: ImageVector,
    selected: Boolean,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { }
                .background(if (selected) Color(0xFF1A1A2E) else Color.Transparent)
                .padding(vertical = 12.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (selected) AccentPurple else TextGray,
            modifier = Modifier.size(24.dp),
        )
        if (title.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = if (selected) Color.White else TextGray,
                fontSize = 9.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}

@Composable
fun TopDesignBar() {
    Row(
        modifier = Modifier.fillMaxWidth().height(70.dp).background(BgDeep).padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text("AVATAR DESIGN", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text("CREATE YOUR IDENTITY", color = AccentPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }

        Spacer(modifier = Modifier.width(64.dp))

        // Tabs
        Row(
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(PanelDark).padding(4.dp),
        ) {
            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFF222230)).padding(horizontal = 24.dp, vertical = 8.dp)) {
                Text("BUILDER", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Text("PRESETS", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Text("IMPORT", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Icons
        Icon(Icons.Default.Undo, contentDescription = "Undo", tint = TextGray, modifier = Modifier.padding(horizontal = 12.dp))
        Icon(Icons.Default.Redo, contentDescription = "Redo", tint = Color(0xFF333344), modifier = Modifier.padding(horizontal = 12.dp))
        Icon(Icons.Outlined.ViewInAr, contentDescription = "3D", tint = TextGray, modifier = Modifier.padding(horizontal = 12.dp))
        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = TextGray, modifier = Modifier.padding(horizontal = 12.dp))
    }
}

@Composable
fun PresetsColumn(modifier: Modifier = Modifier) {
    Column(modifier = modifier.background(BgDeep).padding(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("PRESETS", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Tune, contentDescription = "Filter", tint = TextGray, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(6) { index ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (index == 0) 2.dp else 1.dp,
                                color = if (index == 0) AccentPurple else BorderDark,
                                shape = RoundedCornerShape(8.dp),
                            )
                            .background(PanelDark),
                ) {
                    // Placeholder for preset images
                    Box(
                        modifier =
                            Modifier.fillMaxSize().background(
                                Brush.verticalGradient(listOf(Color.Transparent, Color(0x88000000))),
                            ),
                    )
                    if (index == 0) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = AccentPurple, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = PanelDark, contentColor = TextWhite),
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
            Spacer(modifier = Modifier.width(4.dp))
            Text("NEW PRESET", fontSize = 10.sp)
        }
    }
}

@Composable
fun MainPreviewArea(
    modifier: Modifier = Modifier,
    generatedImage: Bitmap? = null,
    isGenerating: Boolean = false,
) {
    Box(modifier = modifier.background(BgDeep)) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Brush.radialGradient(listOf(Color(0xFF2A1B4A), BgDeep), radius = 1000f)),
            contentAlignment = Alignment.Center,
        ) {
            if (generatedImage != null) {
                Image(
                    bitmap = generatedImage.asImageBitmap(),
                    contentDescription = "Generated Avatar",
                    modifier = Modifier.fillMaxSize(),
                )
            } else if (isGenerating) {
                CircularProgressIndicator(color = AccentPurple)
            }
        }

        // Right Tools
        Column(
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            ToolIcon("R", "ROTATE")
            ToolIcon("🔍", "ZOOM")
            ToolIcon("☩", "PAN")
            ToolIcon("🎲", "RANDOM")
        }

        // Bottom Tools
        Row(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PanelDark.copy(alpha = 0.8f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Icon(Icons.Outlined.LightMode, contentDescription = "Light", tint = TextGray)
            Icon(Icons.Outlined.Face, contentDescription = "Face", tint = TextGray)
            Icon(Icons.Outlined.Accessibility, contentDescription = "Body", tint = TextGray)
            Icon(Icons.Outlined.Person, contentDescription = "Person", tint = TextGray)
            Icon(Icons.Outlined.ViewInAr, contentDescription = "3D", tint = AccentPurple)
        }
    }
}

@Composable
fun ToolIcon(
    symbol: String,
    label: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).border(1.dp, BorderDark, CircleShape).background(PanelDark),
            contentAlignment = Alignment.Center,
        ) {
            Text(symbol, color = TextWhite, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = TextGray, fontSize = 9.sp)
    }
}

@Composable
fun BottomDetailArea(modifier: Modifier = Modifier) {
    Column(modifier = modifier.background(BgDeep).padding(16.dp)) {
        // Tabs
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("HAIR STYLE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 24.dp, bottom = 8.dp))
            Text("HAIR COLOR", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 24.dp, bottom = 8.dp))
            Text("FACIAL HAIR", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 24.dp, bottom = 8.dp))
            Text("EYEBROWS", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 24.dp, bottom = 8.dp))
        }
        HorizontalDivider(color = AccentPurple, modifier = Modifier.width(70.dp).height(2.dp))
        HorizontalDivider(color = BorderDark, modifier = Modifier.fillMaxWidth().height(1.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
            // Hair Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(8) { index ->
                    Box(
                        modifier =
                            Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (index == 0) AccentPurple else BorderDark,
                                    RoundedCornerShape(8.dp),
                                )
                                .background(PanelDark),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.Face, contentDescription = null, tint = if (index == 0) AccentPurple else TextGray)
                    }
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Color Wheel Section
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                // Color Wheel Placeholder
                val rainbowColors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red)
                Box(
                    modifier = Modifier.size(120.dp).clip(CircleShape).background(Brush.sweepGradient(rainbowColors)),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Box(modifier = Modifier.padding(4.dp).size(16.dp).clip(CircleShape).background(Color.White).border(2.dp, AccentPurple, CircleShape))
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Sliders
                Column(modifier = Modifier.width(16.dp).height(120.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(8.dp)).background(Brush.verticalGradient(listOf(Color.White, AccentPurple))))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.width(16.dp).height(120.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(8.dp)).background(Brush.verticalGradient(listOf(Color.White, Color.Black))))
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Hex & Recent
                Column {
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(PanelDark).border(1.dp, BorderDark, RoundedCornerShape(8.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("#", color = TextGray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("904EDD", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("RECENT", color = TextGray, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(AccentPurple, Color(0xFF4E58DD), Color(0xFF4EDD90), Color(0xFF222230), Color(0xFFDDDDDD)).forEach { col ->
                            Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp)).background(col))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add-ons & Preview
        Row(modifier = Modifier.fillMaxWidth().height(100.dp)) {
            // Details & Add-ons
            Column(modifier = Modifier.weight(1.5f)) {
                Text("DETAILS & ADD-ONS", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AddonBox("PIERCINGS", "12 / 32", Icons.Outlined.Face)
                    AddonBox("SCARS", "05 / 18", Icons.Outlined.Sick)
                    AddonBox("MAKEUP", "08 / 28", Icons.Outlined.Brush)
                    AddonBox("CYBERWARE", "15 / 40", Icons.Outlined.Memory)
                    AddonBox("FACE PAINT", "07 / 22", Icons.Outlined.ColorLens)
                }
            }

            // Preview Angles
            Column(modifier = Modifier.weight(1f)) {
                Text("PREVIEW", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f).clip(CircleShape).background(PanelDark).border(1.dp, BorderDark, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Face, tint = TextGray, contentDescription = null) }
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f).clip(CircleShape).background(PanelDark).border(1.dp, AccentPurple, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Face, tint = AccentPurple, contentDescription = null) }
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f).clip(CircleShape).background(PanelDark).border(1.dp, BorderDark, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Face, tint = TextGray, contentDescription = null) }
                }
            }
        }
    }
}

@Composable
fun AddonBox(
    title: String,
    count: String,
    icon: ImageVector,
) {
    Column(
        modifier =
            Modifier
                .width(70.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PanelDark)
                .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = title, tint = TextGray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, color = TextGray, fontSize = 8.sp)
        Text(count, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AppearancePanel(
    age: Float,
    onAgeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.background(BgDeep).padding(24.dp).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("APPEARANCE", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Collapse", tint = TextGray)
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Gender
        Text("GENDER", color = TextGray, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GenderBox(Icons.Default.Male, selected = true, modifier = Modifier.weight(1f))
            GenderBox(Icons.Default.Female, selected = false, modifier = Modifier.weight(1f))
            GenderBox(Icons.Default.Transgender, selected = false, modifier = Modifier.weight(1f))
            GenderBox(Icons.Default.Female, selected = false, modifier = Modifier.weight(1f)) // Placeholder for 4th icon
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Skin Tone
        Text("SKIN TONE", color = TextGray, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val skinColors = listOf(Color(0xFFFFDAB9), Color(0xFFDEB887), Color(0xFFD2B48C), Color(0xFFBC8F8F), Color(0xFFA0522D), Color(0xFF8B4513), Color(0xFF5C4033))
            skinColors.forEachIndexed { index, color ->
                Box(
                    modifier =
                        Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color)
                            .border(
                                2.dp,
                                if (index == 2) AccentPurple else Color.Transparent,
                                RoundedCornerShape(8.dp),
                            ),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Head Shape
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("HEAD SHAPE", color = TextGray, fontSize = 10.sp)
            Text("04 / 12", color = TextGray, fontSize = 10.sp)
        }
        Slider(
            value = 0.3f,
            onValueChange = {},
            colors =
                SliderDefaults.colors(
                    thumbColor = AccentPurple,
                    activeTrackColor = AccentPurple,
                    inactiveTrackColor = PanelDark,
                ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Age
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("AGE", color = TextGray, fontSize = 10.sp)
            Text("${age.toInt()}", color = TextGray, fontSize = 10.sp)
        }
        Slider(
            value = age,
            onValueChange = onAgeChange,
            valueRange = 18f..80f,
            colors =
                SliderDefaults.colors(
                    thumbColor = AccentPurple,
                    activeTrackColor = AccentPurple,
                    inactiveTrackColor = PanelDark,
                ),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Skin Details
        Text("SKIN DETAILS", color = TextGray, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(4) { index ->
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PanelDark)
                            .border(1.dp, if (index == 0) AccentPurple else BorderDark, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (index == 3) {
                        Icon(Icons.Default.MoreHoriz, tint = TextGray, contentDescription = "More")
                    } else {
                        Icon(Icons.Outlined.Face, tint = if (index == 0) AccentPurple else TextGray, contentDescription = null)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Collapsed Categories
        AccordionItem("HAIR")
        AccordionItem("EYES")
        AccordionItem("FACE")
        AccordionItem("BODY")
        AccordionItem("TATTOOS")
        AccordionItem("AUGMENTS")

        Spacer(modifier = Modifier.height(24.dp))

        // Color Accent Button
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(PanelDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("COLOR ACCENT", color = TextGray, fontSize = 12.sp)
            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(AccentPurple))
        }
    }
}

@Composable
fun GenderBox(
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (selected) Color(0xFF1A1A2E) else PanelDark)
                .border(1.dp, if (selected) AccentPurple else BorderDark, RoundedCornerShape(8.dp))
                .clickable { },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = if (selected) AccentPurple else TextGray)
    }
}

@Composable
fun AccordionItem(title: String) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clickable { },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Expand", tint = TextGray)
    }
    HorizontalDivider(color = BorderDark, modifier = Modifier.fillMaxWidth().height(1.dp))
}

@Composable
fun ActionBar(
    modifier: Modifier = Modifier,
    onGenerateClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.background(BgDeep).padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text("AVATAR ID", color = TextGray, fontSize = 8.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("MATRIX_07_8X9A", color = TextGray, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = TextGray, modifier = Modifier.size(12.dp))
            }
        }

        Row(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentPurple)
                    .clickable { onGenerateClick() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = "Generate", tint = TextWhite, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("GENERATE", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }

        Text("CANCEL", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { })

        Row(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentPurple)
                    .clickable { },
        ) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text("SAVE AVATAR", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.background(Color(0x33000000)).padding(12.dp)) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "More", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}
