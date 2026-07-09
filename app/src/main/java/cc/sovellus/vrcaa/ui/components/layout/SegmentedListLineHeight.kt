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

package cc.sovellus.vrcaa.ui.components.layout

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** M3 segmented list outer corner radius (single item: all four corners). */
val SegmentedContainerCornerRadius = 16.dp

/** M3 segmented list inner corner radius (middle items / group joints). */
val SegmentedInnerCornerRadius = 4.dp

/**
 * Minimum row height by visible text line count (M3 list item sizing).
 * Applied via [heightIn] only — rows grow beyond these values when font scale or content requires it.
 */
enum class SegmentedListLineLayout {
    OneLine,
    TwoLine,
    ThreeLine,
}

val SegmentedListLineLayout.minHeight: Dp
    get() = when (this) {
        SegmentedListLineLayout.OneLine -> 56.dp
        SegmentedListLineLayout.TwoLine -> 72.dp
        SegmentedListLineLayout.ThreeLine -> 88.dp
    }

fun Modifier.segmentedListMinHeight(layout: SegmentedListLineLayout): Modifier =
    heightIn(min = layout.minHeight)

/**
 * M3 segmented list shapes with explicit single-item handling.
 *
 * When [count] is 1, all four corners use [SegmentedContainerCornerRadius] (16dp).
 * Official [ListItemDefaults.segmentedShapes] does not always apply this in practice.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun segmentedListItemShapes(index: Int, count: Int): ListItemShapes {
    val baseShape = segmentedListItemShape(index = index, count = count)
    return ListItemDefaults.shapes(
        shape = baseShape,
        selectedShape = baseShape,
        pressedShape = baseShape,
        focusedShape = baseShape,
        hoveredShape = baseShape,
        draggedShape = baseShape,
    )
}

fun segmentedListItemShape(index: Int, count: Int): Shape = when {
    count <= 1 -> RoundedCornerShape(SegmentedContainerCornerRadius)
    index == 0 -> RoundedCornerShape(
        topStart = SegmentedContainerCornerRadius,
        topEnd = SegmentedContainerCornerRadius,
        bottomStart = SegmentedInnerCornerRadius,
        bottomEnd = SegmentedInnerCornerRadius,
    )
    index == count - 1 -> RoundedCornerShape(
        topStart = SegmentedInnerCornerRadius,
        topEnd = SegmentedInnerCornerRadius,
        bottomStart = SegmentedContainerCornerRadius,
        bottomEnd = SegmentedContainerCornerRadius,
    )
    else -> RoundedCornerShape(SegmentedInnerCornerRadius)
}
