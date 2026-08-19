import re

path = "app/src/main/java/com/example/ui/screens/AvatarCreatorScreen.kt"
with open(path, "r") as f:
    content = f.read()

# Add bitmap import
if "import android.graphics.Bitmap" not in content:
    content = content.replace("import androidx.compose.ui.graphics.Color\n", "import androidx.compose.ui.graphics.Color\nimport android.graphics.Bitmap\nimport androidx.compose.ui.graphics.asImageBitmap\nimport androidx.compose.foundation.Image\n")

# Update AvatarCreatorScreen to pass state down
if "val isGenerating by viewModel.isGenerating.collectAsState()" not in content:
    state_reads = """    val isGenerating by viewModel.isGenerating.collectAsState()
    val generatedImage by viewModel.generatedImage.collectAsState()"""
    content = content.replace("val advSpec by viewModel.advancedAvatarSpec.collectAsState()", "val advSpec by viewModel.advancedAvatarSpec.collectAsState()\n" + state_reads)

content = content.replace("MainPreviewArea(modifier = Modifier.weight(1f).fillMaxWidth())", "MainPreviewArea(modifier = Modifier.weight(1f).fillMaxWidth(), generatedImage = generatedImage, isGenerating = isGenerating)")
content = content.replace("ActionBar(modifier = Modifier.height(80.dp).fillMaxWidth())", "ActionBar(modifier = Modifier.height(80.dp).fillMaxWidth(), onGenerateClick = { viewModel.generateAvatar() })")

# Update MainPreviewArea
preview_area = """@Composable
fun MainPreviewArea(
    modifier: Modifier = Modifier,
    generatedImage: Bitmap? = null,
    isGenerating: Boolean = false,
) {
    Box(modifier = modifier.background(BgDeep)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(listOf(Color(0xFF2A1B4A), BgDeep), radius = 1000f)),
            contentAlignment = Alignment.Center
        ) {
            if (generatedImage != null) {
                Image(
                    bitmap = generatedImage.asImageBitmap(),
                    contentDescription = "Generated Avatar",
                    modifier = Modifier.fillMaxSize()
                )
            } else if (isGenerating) {
                CircularProgressIndicator(color = AccentPurple)
            }
        }"""
content = re.sub(r"@Composable\s+fun MainPreviewArea\(modifier: Modifier = Modifier\) \{\s+Box\(modifier = modifier\.background\(BgDeep\)\) \{\s+// Placeholder for the main generated image\s+Box\(\s+modifier =\s+Modifier\s+\.fillMaxSize\(\)\s+\.background\(Brush\.radialGradient\(listOf\(Color\(0xFF2A1B4A\), BgDeep\), radius = 1000f\)\),\s+\)", preview_area, content)

# Update ActionBar
action_bar_sig = """@Composable
fun ActionBar(modifier: Modifier = Modifier, onGenerateClick: () -> Unit = {}) {"""
content = content.replace("@Composable\nfun ActionBar(modifier: Modifier = Modifier) {", action_bar_sig)

action_bar_button = """Row(
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
        }"""
content = re.sub(r"Row\(\s+modifier =\s+Modifier\s+\.clip\(RoundedCornerShape\(8\.dp\)\)\s+\.background\(PanelDark\)\s+\.border\(1\.dp, BorderDark, RoundedCornerShape\(8\.dp\)\)\s+\.padding\(horizontal = 16\.dp, vertical = 12\.dp\)\s+\.clickable \{ \},\s+verticalAlignment = Alignment\.CenterVertically,\s+\) \{\s+Icon\(Icons\.Outlined\.Checkroom, contentDescription = \"Outfit\", tint = TextGray, modifier = Modifier\.size\(16\.dp\)\)\s+Spacer\(modifier = Modifier\.width\(8\.dp\)\)\s+Text\(\"LOAD OUTFIT\", color = TextGray, fontSize = 10\.sp, fontWeight = FontWeight\.Bold\)\s+\}", action_bar_button, content)


with open(path, "w") as f:
    f.write(content)
