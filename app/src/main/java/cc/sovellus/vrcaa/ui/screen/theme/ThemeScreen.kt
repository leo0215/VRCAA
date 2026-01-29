/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.ui.screen.theme

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.ViewColumn
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.columnCountOption
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.extension.fixedColumnSize
import cc.sovellus.vrcaa.extension.fontFamily
import cc.sovellus.vrcaa.extension.minimalistMode
import cc.sovellus.vrcaa.manager.ThemeManager
import cc.sovellus.vrcaa.ui.components.settings.SectionHeader
import cc.sovellus.vrcaa.ui.components.settings.SettingsGroup
import cc.sovellus.vrcaa.ui.components.settings.SettingsItem
import cc.sovellus.vrcaa.ui.components.settings.rememberThumbContent
import cc.sovellus.vrcaa.ui.components.dialog.FontSelectionDialog
import cc.sovellus.vrcaa.ui.components.settings.ColorPicker
import cc.sovellus.vrcaa.ui.components.settings.ColorPickerContent
import cc.sovellus.vrcaa.ui.components.settings.SettingsCard
import cc.sovellus.vrcaa.ui.screen.debug.ColorDebugScreen
import kotlin.math.roundToInt

class ThemeScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val model = navigator.rememberNavigatorScreenModel { ThemeScreenModel() }
        
        val fontSelectionState = remember { mutableStateOf(false) }


        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },

                    title = { Text(text = stringResource(R.string.theme_page_title)) }
                )
            },
            content = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = it.calculateBottomPadding(),
                            top = it.calculateTopPadding()
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        SectionHeader(stringResource(R.string.theme_page_section_theme_title))
                    }

                    item {
                        val options = stringArrayResource(R.array.theme_page_selection_options)
                        val unCheckedIcons =
                            listOf(Icons.Outlined.LightMode, Icons.Outlined.DarkMode, Icons.Outlined.Settings)
                        val checkedIcons = listOf(Icons.Filled.LightMode, Icons.Filled.DarkMode, Icons.Filled.Settings)
                        var selectedIndex by remember { mutableIntStateOf(model.currentIndex.intValue) }


                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceBright
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                            ) {
                                val modifiers = listOf(Modifier.weight(1f), Modifier.weight(1f), Modifier.weight(1f))

                                options.forEachIndexed { index, label ->
                                    ToggleButton(
                                        checked = selectedIndex == index,
                                        onCheckedChange = {
                                            selectedIndex = index
                                            model.currentIndex.intValue = index
                                            model.preferences.currentThemeOption = index
                                            ThemeManager.setTheme(index)
                                        },
                                        modifier = modifiers[index].semantics { role = Role.RadioButton },
                                        shapes =
                                            when (index) {
                                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                                options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                            },
                                    ) {
                                        Icon(
                                            if (selectedIndex == index) checkedIcons[index] else unCheckedIcons[index],
                                            contentDescription = "Localized description",
                                        )
                                        Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                                        Text(label)
                                    }
                                }
                            }
                        }

                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(stringResource(R.string.theme_page_section_display_title))
                    }

                    item {
                        val options = stringArrayResource(R.array.column_count_options)
                        val unCheckedIcons = listOf(
                            Icons.Outlined.ViewColumn,
                            Icons.Outlined.ViewColumn
                        )
                        val checkedIcons = listOf(
                            Icons.Filled.ViewColumn,
                            Icons.Filled.ViewColumn
                        )
                        var selectedColumnIndex by remember { mutableIntStateOf(model.currentColumnIndex.intValue) }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceBright
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.theme_page_section_column_title),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                                ) {
                                    val modifiers = List(options.size) { Modifier.weight(1f) }

                                    options.forEachIndexed { index, label ->
                                        ToggleButton(
                                            checked = selectedColumnIndex == index,
                                            onCheckedChange = {
                                                selectedColumnIndex = index
                                                model.currentColumnIndex.intValue = index
                                                model.preferences.columnCountOption = index
                                            },
                                            modifier = modifiers[index].semantics { role = Role.RadioButton },
                                            shapes =
                                                when (index) {
                                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                                },
                                        ) {
                                            Icon(
                                                if (selectedColumnIndex == index) checkedIcons[index] else unCheckedIcons[index],
                                                contentDescription = "Localized description",
                                            )
                                            Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                                            Text(label)
                                        }
                                    }
                                }

                                if (model.currentColumnIndex.intValue == 1) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Slider(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        value = model.currentColumnAmount.floatValue,
                                        onValueChange = { size ->
                                            model.currentColumnAmount.floatValue = size.roundToInt().toFloat()
                                            model.preferences.fixedColumnSize = model.currentColumnAmount.floatValue.roundToInt()
                                        },
                                        valueRange = 1f..6f
                                    )
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        text = stringResource(R.string.theme_page_fixed_column_size, model.currentColumnAmount.floatValue.roundToInt()),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(stringResource(R.string.theme_page_section_font_title))
                    }

                    item {
                        SettingsGroup(
                            items = listOf(
                                SettingsItem(
                                    title = stringResource(R.string.settings_item_font),
                                    description = stringResource(R.string.settings_item_font_description),
                                    icon = Icons.Outlined.TextFields,
                                    onClick = {
                                        fontSelectionState.value = true
                                    }
                                )
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(stringResource(R.string.theme_page_section_color_title))
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceBright
                            )
                        ) {
                            Column {
                                SettingsCard(
                                    title = stringResource(R.string.theme_page_follow_system_color_theme),
                                    description = stringResource(R.string.theme_page_follow_system_color_theme_description),
                                    icon = Icons.Outlined.Palette,
                                    onClick = {
                                        model.setUseSystemColorTheme(!model.useSystemColorTheme.value)
                                    },
                                    trailingContent = {
                                        Switch(
                                            checked = model.useSystemColorTheme.value,
                                            onCheckedChange = {
                                                model.setUseSystemColorTheme(it)
                                            },
                                            thumbContent = rememberThumbContent(isChecked = model.useSystemColorTheme.value)
                                        )
                                    }
                                )
                                
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    thickness = 1.dp
                                )
                                
                                SettingsCard(
                                    title = stringResource(R.string.theme_page_use_legacy_material_theme),
                                    icon = Icons.Outlined.Settings,
                                    onClick = {
                                        model.setUseLegacyMaterialTheme(!model.useLegacyMaterialTheme.value)
                                    },
                                    trailingContent = {
                                        Switch(
                                            checked = model.useLegacyMaterialTheme.value,
                                            onCheckedChange = {
                                                model.setUseLegacyMaterialTheme(it)
                                            },
                                            thumbContent = rememberThumbContent(isChecked = model.useLegacyMaterialTheme.value)
                                        )
                                    }
                                )
                                
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    thickness = 1.dp
                                )
                                
                                ColorPickerContent(
                                    selectedColor = if (model.useSystemColorTheme.value) null else model.primaryColor,
                                    selectedSchemeIndex = model.colorSchemeIndex,
                                    onColorSelected = { color, schemeIndex ->
                                        // 如果「跟隨系統顏色主題」啟用，自動禁用並應用選擇的顏色
                                        if (model.useSystemColorTheme.value) {
                                            model.setUseSystemColorTheme(false)
                                        }
                                        model.setPrimaryColor(color, schemeIndex)
                                        // Theme will update automatically via SharedPreferences listener
                                    }
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        SettingsGroup(
                            items = listOf(
                                SettingsItem(
                                    title = "Material Colors Debug",
                                    description = "View all Material color values",
                                    icon = Icons.Outlined.BugReport,
                                    onClick = {
                                        navigator.push(ColorDebugScreen())
                                    }
                                )
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        SettingsGroup(
                            items = listOf(
                                SettingsItem(
                                    title = stringResource(R.string.theme_page_minimalist_mode_text),
                                    description = stringResource(R.string.theme_page_minimalist_mode_text_description),
                                    icon = Icons.Outlined.Info,
                                    onClick = {
                                        model.minimalistMode.value = !model.minimalistMode.value
                                        model.preferences.minimalistMode = !model.preferences.minimalistMode
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.developer_mode_toggle_toast),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    trailingContent = {
                                        Switch(
                                            checked = model.minimalistMode.value,
                                            onCheckedChange = {
                                                model.minimalistMode.value = !model.minimalistMode.value
                                                model.preferences.minimalistMode = !model.preferences.minimalistMode
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.developer_mode_toggle_toast),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            thumbContent = rememberThumbContent(isChecked = model.minimalistMode.value)
                                        )
                                    }
                                )
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            },
        )

        if (fontSelectionState.value) {
            FontSelectionDialog(
                onDismiss = { fontSelectionState.value = false },
                onFontSelected = { fontIndex ->
                    model.currentFontFamily.intValue = fontIndex
                    model.preferences.fontFamily = fontIndex
                },
                currentFontIndex = model.currentFontFamily.intValue
            )
        }
    }
}
