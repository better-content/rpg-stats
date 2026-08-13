package com.bettercontent.rpgstats.client.ui

import com.bettercontent.rpgstats.client.cache.ClientCache
import com.bettercontent.rpgstats.client.cache.ClientStatDef
import com.bettercontent.rpgstats.common.network.Network
import com.bettercontent.rpgstats.common.network.packets.C2SApplyStats
import com.bettercontent.rpgstats.common.curve.Curves
import com.bettercontent.rpgstats.common.config.json.CurveDef
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import kotlin.math.abs

class StatsScreen : Screen(Component.translatable("screen.rpg_stats.title")) {

    private companion object {
        const val ROW_HEIGHT = 36
    }

    private data class Layout(
        val leftX: Int,
        val rightX: Int,
        val topY: Int,
        val columnWidth: Int,
        val viewportHeight: Int
    )

    private data class Row(
        val def: ClientStatDef,
        var y: Int = 0,
        var plus: Button? = null,
        var minus: Button? = null
    )

    private var rows: List<Row> = emptyList()
    private var originalAlloc: Map<String, Int> = emptyMap()
    private var workingAlloc: MutableMap<String, Int> = mutableMapOf()
    private var originalUnspent: Int = 0
    private var workingUnspent: Int = 0
    private var lifePeak: Int = 0
    private var applyButton: Button? = null
    private var resetButton: Button? = null

    private var leftScrollOffset = 0.0
    private var rightScrollOffset = 0.0
    private var leftContentHeight = 0
    private var rightContentHeight = 0

    override fun init() {
        super.init()

        val defs = ClientCache.defs
        val snap = ClientCache.stats

        originalAlloc = snap.allocations
        workingAlloc = snap.allocations.toMutableMap()
        originalUnspent = snap.unspent
        workingUnspent = snap.unspent
        lifePeak = snap.lifePeak

        val layout = layout()
        val leftX = layout.leftX
        val leftY = layout.topY
        val headerOffset = 30

        rows = defs.map { d -> Row(d) }

        rows.forEachIndexed { idx, row ->
            val y = leftY + headerOffset + idx * ROW_HEIGHT
            row.y = y

            val minusBtn = Button.builder(Component.literal("-")) {
                val cur = workingAlloc[row.def.id] ?: 0
                if (cur > 0) {
                    if (cur == 1) {
                        workingAlloc.remove(row.def.id)
                    } else {
                        workingAlloc[row.def.id] = cur - 1
                    }
                    workingUnspent += 1
                    refreshButtons()
                }
            }.pos(leftX + layout.columnWidth - 42, y + 20).size(18, 14).build()

            val plusBtn = Button.builder(Component.literal("+")) {
                val cur = workingAlloc[row.def.id] ?: 0
                val cap = if (row.def.maxPoints >= 0) row.def.maxPoints else Int.MAX_VALUE
                if (workingUnspent > 0 && cur < cap) {
                    workingAlloc[row.def.id] = cur + 1
                    workingUnspent -= 1
                    refreshButtons()
                }
            }.pos(leftX + layout.columnWidth - 20, y + 20).size(18, 14).build()

            row.minus = minusBtn
            row.plus = plusBtn
            addRenderableWidget(minusBtn)
            addRenderableWidget(plusBtn)
        }

        val btnY = leftY + headerOffset + rows.size * ROW_HEIGHT + 12
        applyButton = Button.builder(Component.translatable("screen.rpg_stats.apply")) {
            Network.sendToServer(C2SApplyStats(workingAlloc.toMap()))
            this.onClose()
        }.pos(leftX, btnY).size(80, 18).build()
        addRenderableWidget(applyButton!!)

        resetButton = Button.builder(Component.translatable("screen.rpg_stats.reset")) {
            workingUnspent += workingAlloc.values.sum()
            workingAlloc.clear()
            refreshButtons()
        }.pos(leftX + 90, btnY).size(80, 18).build()
        addRenderableWidget(resetButton!!)

        refreshButtons()
    }

    private fun refreshButtons() {
        var hasDiff = false
        var hasAllocatedPoints = false

        rows.forEach { row ->
            val cur = workingAlloc[row.def.id] ?: 0
            val original = originalAlloc[row.def.id] ?: 0
            val cap = if (row.def.maxPoints >= 0) row.def.maxPoints else Int.MAX_VALUE

            row.plus?.active = workingUnspent > 0 && cur < cap
            row.minus?.active = cur > 0

            if (cur > 0) hasAllocatedPoints = true
            if (cur != original) hasDiff = true
        }

        applyButton?.active = hasDiff
        resetButton?.active = hasAllocatedPoints
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(guiGraphics)
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        val layout = layout()
        val centerX = this.width / 2
        val leftX = layout.leftX
        val leftY = layout.topY
        val rightX = layout.rightX
        val rightY = layout.topY
        val headerOffset = 30
        val viewportHeight = layout.viewportHeight

        // Title at top center
        val titleWidth = this.font.width(this.title)
        guiGraphics.drawString(this.font, this.title, centerX - titleWidth / 2, leftY - 18, 0xFFFFFF)

        // Left column header
        guiGraphics.drawString(
            this.font,
            Component.translatable("screen.rpg_stats.left_header"),
            leftX,
            leftY + headerOffset - 20,
            0xFFFF55
        )

        guiGraphics.drawString(
            this.font,
            Component.translatable("screen.rpg_stats.unspent", workingUnspent.toString()),
            leftX,
            leftY + headerOffset - 10,
            0xFFFFFF
        )

        // Calculate left content height
        leftContentHeight = rows.size * ROW_HEIGHT

        // Enable scissor for left column
        guiGraphics.enableScissor(leftX, leftY + headerOffset, leftX + layout.columnWidth, leftY + headerOffset + viewportHeight)

        // Left column: Attribute editing with scroll offset
        rows.forEach { row ->
            val pts = workingAlloc[row.def.id] ?: 0
            val scrolledY = row.y - leftScrollOffset.toInt()
            val summary = buildRowSummary(row.def, pts)

            // Draw icon
            if (row.def.icon.isNotEmpty()) {
                guiGraphics.drawString(
                    this.font,
                    row.def.icon,
                    leftX,
                    scrolledY + 1,
                    row.def.color or 0xFF000000.toInt(),
                    false
                )
            }

            // Draw name in color
            val nameComponent = Component.translatable(row.def.nameKey).withStyle { it.withColor(row.def.color or 0xFF000000.toInt()) }
            guiGraphics.drawString(
                this.font,
                nameComponent,
                leftX + 12,
                scrolledY,
                0xFFFFFF,
                false
            )

            guiGraphics.drawString(
                this.font,
                summary,
                leftX + 12,
                scrolledY + 11,
                0xCFCFCF,
                false
            )

            // Draw points
            guiGraphics.drawString(
                this.font,
                Component.literal(formatPointCounter(row.def, pts)),
                leftX + layout.columnWidth - 82,
                scrolledY + 22,
                0xE0E0E0,
                false
            )
        }

        guiGraphics.disableScissor()

        // Right column header
        guiGraphics.drawString(
            this.font,
            Component.translatable("screen.rpg_stats.right_header"),
            rightX,
            rightY + headerOffset - 20,
            0x55FF55
        )

        guiGraphics.drawString(
            this.font,
            Component.translatable("screen.rpg_stats.life_peak", lifePeak.toString()),
            rightX,
            rightY + headerOffset - 10,
            0xFFFFFF
        )

        // Enable scissor for right column
        guiGraphics.enableScissor(rightX, rightY + headerOffset, rightX + layout.columnWidth, rightY + headerOffset + viewportHeight)

        // Right column: Stats with diff
        renderStatsColumn(guiGraphics, rightX, rightY + headerOffset, layout.columnWidth)

        guiGraphics.disableScissor()

        // Render tooltips for left column attribute rows
        rows.forEachIndexed { idx, row ->
            val rowY = leftY + headerOffset + idx * ROW_HEIGHT - leftScrollOffset.toInt()
            val rowHeight = ROW_HEIGHT - 4
            val textWidth = layout.columnWidth - 96

            if (mouseX >= leftX && mouseX <= leftX + textWidth &&
                mouseY >= rowY && mouseY <= rowY + rowHeight &&
                mouseY >= leftY + headerOffset && mouseY <= leftY + headerOffset + viewportHeight) {

                val tooltip = buildTooltip(row.def)
                guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, rowY)
            }
        }
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollDelta: Double): Boolean {
        val layout = layout()
        val leftX = layout.leftX
        val leftY = layout.topY
        val rightX = layout.rightX
        val rightY = layout.topY
        val headerOffset = 30
        val viewportHeight = layout.viewportHeight

        // Check if mouse is over left column
        if (mouseX >= leftX && mouseX <= leftX + layout.columnWidth && mouseY >= leftY + headerOffset && mouseY <= leftY + headerOffset + viewportHeight) {
            val maxScroll = maxOf(0, leftContentHeight - viewportHeight)
            leftScrollOffset = Mth.clamp(leftScrollOffset - scrollDelta * 10, 0.0, maxScroll.toDouble())
            return true
        }

        // Check if mouse is over right column
        if (mouseX >= rightX && mouseX <= rightX + layout.columnWidth && mouseY >= rightY + headerOffset && mouseY <= rightY + headerOffset + viewportHeight) {
            val maxScroll = maxOf(0, rightContentHeight - viewportHeight)
            rightScrollOffset = Mth.clamp(rightScrollOffset - scrollDelta * 10, 0.0, maxScroll.toDouble())
            return true
        }

        return super.mouseScrolled(mouseX, mouseY, scrollDelta)
    }

    private fun renderStatsColumn(guiGraphics: GuiGraphics, x: Int, startY: Int, columnWidth: Int) {
        val lineH = 24
        var y = startY - rightScrollOffset.toInt()
        var lineCount = 0

        // Collect all unique attributes and find their primary provider
        data class AttributeInfo(
            val attributeId: String,
            val operation: Int,
            val friendlyName: String,
            val primaryProviderColor: Int,
            val primaryProviderPerPoint: Double
        )

        val allAttributes = mutableMapOf<String, AttributeInfo>()

        rows.forEach { row ->
            row.def.effects.forEach { effect ->
                val existing = allAttributes[effect.attributeId]

                if (existing == null) {
                    val attrId = ResourceLocation.tryParse(effect.attributeId)
                    val friendlyName = if (attrId != null) {
                        val key = "attr.${attrId.namespace}.${attrId.path}"
                        val translated = Component.translatable(key).string
                        if (translated == key) effect.attributeId else translated
                    } else {
                        effect.attributeId
                    }

                    // Use primary color if this is a primary effect, lightened if secondary
                    val effectColor = if (effect.isPrimary) row.def.color else lightenColor(row.def.color)

                    allAttributes[effect.attributeId] = AttributeInfo(
                        effect.attributeId,
                        effect.operation,
                        friendlyName,
                        effectColor,
                        effect.curve.perPoint
                    )
                } else if (effect.isPrimary && existing.primaryProviderPerPoint < effect.curve.perPoint) {
                    // Update to use this primary provider's color if it contributes more
                    allAttributes[effect.attributeId] = existing.copy(
                        primaryProviderColor = row.def.color,
                        primaryProviderPerPoint = effect.curve.perPoint
                    )
                }
            }
        }

        // Calculate current and new values for each attribute
        allAttributes.values.sortedBy { it.friendlyName }.forEach { attrInfo ->
            var originalTotal = 0.0
            var workingTotal = 0.0

            rows.forEach { row ->
                val originalPoints = originalAlloc[row.def.id] ?: 0
                val workingPoints = workingAlloc[row.def.id] ?: 0

                row.def.effects.forEach { effect ->
                    if (effect.attributeId == attrInfo.attributeId) {
                        val curveDef = CurveDef(
                            type = effect.curve.type,
                            cap = effect.curve.cap,
                            k = effect.curve.k,
                            perPoint = effect.curve.perPoint,
                            min = effect.curve.min,
                            max = effect.curve.max
                        )
                        originalTotal += Curves.eval(originalPoints, curveDef)
                        workingTotal += Curves.eval(workingPoints, curveDef)
                    }
                }
            }

            // Only show if there's any value (current or future)
            if (originalTotal != 0.0 || workingTotal != 0.0) {
                val isMultiplier = attrInfo.operation == 1

                val originalStr = formatEffectValue(originalTotal, isMultiplier)
                val workingStr = formatEffectValue(workingTotal, isMultiplier)

                val valueChanged = originalTotal != workingTotal
                val color = attrInfo.primaryProviderColor or 0xFF000000.toInt()

                // Draw attribute name in primary provider's color
                guiGraphics.drawString(this.font, attrInfo.friendlyName, x, y, color, false)

                // Put values on their own line so narrow GUI scales remain readable.
                if (valueChanged) {
                    val comparison = "$originalStr → $workingStr"
                    guiGraphics.drawString(
                        this.font,
                        comparison,
                        x + columnWidth - this.font.width(comparison),
                        y + 11,
                        color,
                        false
                    )
                } else {
                    guiGraphics.drawString(
                        this.font,
                        originalStr,
                        x + columnWidth - this.font.width(originalStr),
                        y + 11,
                        color,
                        false
                    )
                }

                y += lineH
                lineCount++
            }
        }

        // Update right content height for scrolling
        rightContentHeight = lineCount * lineH
    }

    private fun lightenColor(color: Int, factor: Double = 1.5): Int {
        val a = ((color shr 24) and 0xFF)
        val r = ((color shr 16) and 0xFF)
        val g = ((color shr 8) and 0xFF)
        val b = (color and 0xFF)

        val newR = minOf(255, (r * factor).toInt())
        val newG = minOf(255, (g * factor).toInt())
        val newB = minOf(255, (b * factor).toInt())

        return (a shl 24) or (newR shl 16) or (newG shl 8) or newB
    }

    private fun formatPointCounter(def: ClientStatDef, points: Int): String {
        return if (def.maxPoints >= 0) {
            "$points/${def.maxPoints}"
        } else {
            points.toString()
        }
    }

    private fun buildRowSummary(def: ClientStatDef, currentPoints: Int): Component {
        val primary = def.effects.firstOrNull { it.isPrimary } ?: def.effects.firstOrNull()
            ?: return Component.translatable("tooltip.rpg_stats.no_effects")

        val attrName = resolveAttributeName(primary.attributeId)
        val summary = formatMarginalGain(primary, currentPoints)
        val secondaryCount = (def.effects.size - 1).coerceAtLeast(0)
        val suffix = if (secondaryCount > 0) {
            Component.translatable("screen.rpg_stats.more_effects", secondaryCount.toString())
        } else {
            Component.empty()
        }

        return Component.translatable("screen.rpg_stats.main_effect", summary, attrName).append(suffix)
    }

    private fun resolveAttributeName(attributeId: String): Component {
        val attrId = ResourceLocation.tryParse(attributeId)
        if (attrId == null) {
            return Component.literal(attributeId)
        }

        val key = "attr.${attrId.namespace}.${attrId.path}"
        val translated = Component.translatable(key)
        return if (translated.string == key) Component.literal(attributeId) else translated
    }

    private fun formatEffectValue(value: Double, isMultiplier: Boolean): String {
        return if (isMultiplier) {
            formatSignedPercent(value)
        } else {
            formatSignedNumber(value)
        }
    }

    private fun formatMarginalGain(effect: com.bettercontent.rpgstats.client.cache.ClientEffectDef, currentPoints: Int): String {
        val curveDef = CurveDef(
            type = effect.curve.type,
            cap = effect.curve.cap,
            k = effect.curve.k,
            perPoint = effect.curve.perPoint,
            min = effect.curve.min,
            max = effect.curve.max
        )
        val currentValue = Curves.eval(currentPoints, curveDef)
        val nextValue = Curves.eval(currentPoints + 1, curveDef)
        val delta = nextValue - currentValue
        return formatEffectValue(delta, effect.operation != 0)
    }

    private fun formatSignedPercent(value: Double): String {
        val percent = value * 100.0
        return "${if (percent >= 0.0) "+" else ""}${trimNumber(percent)}%"
    }

    private fun formatSignedNumber(value: Double): String {
        return "${if (value >= 0.0) "+" else ""}${trimNumber(value)}"
    }

    private fun trimNumber(value: Double): String {
        val absValue = abs(value)
        return when {
            absValue >= 100.0 -> String.format("%.0f", value)
            absValue >= 10.0 -> String.format("%.1f", value)
            else -> String.format("%.2f", value)
        }
    }

    private fun buildTooltip(def: ClientStatDef): List<Component> {
        val lines = mutableListOf<Component>()

        if (def.effects.isEmpty()) {
            lines.add(Component.translatable("tooltip.rpg_stats.no_effects"))
            return lines
        }

        val currentPoints = workingAlloc[def.id] ?: 0
        lines.add(Component.translatable("tooltip.rpg_stats.points_header", formatPointCounter(def, currentPoints)))
        lines.add(Component.translatable("tooltip.rpg_stats.effects_header"))

        def.effects.forEach { effect ->
            val attrName = resolveAttributeName(effect.attributeId)

            val curveDef = CurveDef(
                type = effect.curve.type,
                cap = effect.curve.cap,
                k = effect.curve.k,
                perPoint = effect.curve.perPoint,
                min = effect.curve.min,
                max = effect.curve.max
            )

            // Calculate marginal value (next point's contribution)
            val currentValue = Curves.eval(currentPoints, curveDef)
            val nextValue = Curves.eval(currentPoints + 1, curveDef)
            val marginalValue = nextValue - currentValue

            // Use primary color if this is a primary effect, lightened if secondary
            val effectColor = if (effect.isPrimary) def.color else lightenColor(def.color)
            val coloredName = Component.literal(attrName.string).withStyle { it.withColor(effectColor) }
            val roleKey = if (effect.isPrimary) "tooltip.rpg_stats.main_effect" else "tooltip.rpg_stats.bonus_effect"
            val nowText = formatEffectValue(currentValue, effect.operation != 0)
            val nextText = formatEffectValue(marginalValue, effect.operation != 0)

            val line = Component.translatable(roleKey)
                .append(Component.literal(": "))
                .append(coloredName)
                .append(Component.literal(" "))
                .append(Component.translatable("tooltip.rpg_stats.now_next", nowText, nextText))

            lines.add(line)
        }

        return lines
    }

    override fun isPauseScreen(): Boolean = false

    private fun layout(): Layout {
        val panelWidth = (width - 20).coerceIn(300, 570)
        val gap = 20
        val columnWidth = (panelWidth - gap) / 2
        val leftX = (width - panelWidth) / 2
        val topY = (height / 2 - 105).coerceAtLeast(30)
        val viewportHeight = (height - topY - 80).coerceAtLeast(120)
        return Layout(leftX, leftX + columnWidth + gap, topY, columnWidth, viewportHeight)
    }
}
