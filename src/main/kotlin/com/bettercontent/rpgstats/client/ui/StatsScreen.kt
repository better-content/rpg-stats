package com.bettercontent.rpgstats.client.ui

import com.bettercontent.rpgstats.client.cache.ClientCache
import com.bettercontent.rpgstats.client.cache.ClientCurveDef
import com.bettercontent.rpgstats.client.cache.ClientEffectDef
import com.bettercontent.rpgstats.client.cache.ClientStatDef
import com.bettercontent.rpgstats.common.config.json.CurveDef
import com.bettercontent.rpgstats.common.curve.Curves
import com.bettercontent.rpgstats.common.network.Network
import com.bettercontent.rpgstats.common.network.packets.C2SApplyStats
import com.bettercontent.rpgstats.common.salience.AspectIdentity
import com.bettercontent.rpgstats.common.sound.ModSounds
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import kotlin.math.abs

class StatsScreen : Screen(Component.translatable("screen.rpg_stats.title")) {
    private companion object {
        const val OUTER_MARGIN = 8
        const val PANEL_MAX_WIDTH = 600
        const val PANEL_MAX_HEIGHT = 336
        const val PANEL_PADDING = 12
        const val COLUMN_GAP = 16
        const val SUMMARY_TOP = 24
        const val SUMMARY_HEIGHT = 24
        const val HEADER_TOP = 56
        const val CONTENT_TOP = 70
        const val FOOTER_HEIGHT = 32
        const val STAT_ROW_HEIGHT = 32
        const val PROPERTY_ROW_HEIGHT = 28
        const val CONTROL_BUTTON_SIZE = 18
        const val CONTROL_WIDTH = 72

        const val PANEL_BORDER = 0xFF69717C.toInt()
        const val PANEL_BACKGROUND = 0xEC101318.toInt()
        const val SUMMARY_BACKGROUND = 0xE51B2027.toInt()
        const val HEADER_BACKGROUND = 0xE0191D23.toInt()
        const val ROW_BACKGROUND = 0xD9181C22.toInt()
        const val ROW_ALTERNATE = 0xD91D2229.toInt()
        const val ROW_HOVER = 0xEE29313A.toInt()
        const val RULE_COLOR = 0xFF3E4650.toInt()
        const val PRIMARY_TEXT = 0xFFF4F5F7.toInt()
        const val SECONDARY_TEXT = 0xFFB8BEC7.toInt()
        const val PENDING_UP = 0xFF72DB78.toInt()
        const val PENDING_DOWN = 0xFFFF6B6B.toInt()
    }

    private data class Layout(
        val panelX: Int,
        val panelY: Int,
        val panelWidth: Int,
        val panelHeight: Int,
        val leftX: Int,
        val rightX: Int,
        val columnWidth: Int,
        val contentTop: Int,
        val footerTop: Int,
        val viewportHeight: Int
    )

    private data class Row(val def: ClientStatDef, var plus: Button? = null, var minus: Button? = null)

    private data class PropertyProvider(
        val attributeId: String,
        val operation: Int,
        val friendlyName: String,
        val sourceName: String,
        val color: Int,
        val perPoint: Double,
        val primary: Boolean,
        val displayAsPercent: Boolean
    )

    private data class PropertyRow(
        val friendlyName: String,
        val sourceName: String,
        val color: Int,
        val operation: Int,
        val displayAsPercent: Boolean,
        val originalTotal: Double,
        val workingTotal: Double
    )

    private var rows: List<Row> = emptyList()
    private var originalAlloc: Map<String, Int> = emptyMap()
    private var workingAlloc: MutableMap<String, Int> = mutableMapOf()
    private var workingUnspent = 0
    private var lifePeak = 0
    private var applyButton: Button? = null
    private var leftScrollOffset = 0.0
    private var rightScrollOffset = 0.0
    private var leftContentHeight = 0
    private var rightContentHeight = 0
    private val previewPulses = mutableMapOf<String, Int>()

    override fun init() {
        super.init()
        val snapshot = ClientCache.stats
        originalAlloc = snapshot.allocations
        workingAlloc = snapshot.allocations.toMutableMap()
        workingUnspent = snapshot.unspent
        lifePeak = snapshot.lifePeak
        rows = ClientCache.defs.map(::Row)

        val layout = layout()
        rows.forEachIndexed { index, row ->
            val buttonY = layout.contentTop + index * STAT_ROW_HEIGHT + 7
            row.minus = Button.builder(Component.literal("−")) { decrement(row) }
                .pos(layout.leftX + layout.columnWidth - CONTROL_WIDTH, buttonY)
                .size(CONTROL_BUTTON_SIZE, CONTROL_BUTTON_SIZE).build().also(::addRenderableWidget)
            row.plus = Button.builder(Component.literal("+")) { increment(row) }
                .pos(layout.leftX + layout.columnWidth - CONTROL_BUTTON_SIZE - 4, buttonY)
                .size(CONTROL_BUTTON_SIZE, CONTROL_BUTTON_SIZE).build().also(::addRenderableWidget)
        }

        val actionWidth = 80
        val actionY = layout.footerTop + 7
        applyButton = Button.builder(Component.translatable("screen.rpg_stats.apply")) {
            Network.sendToServer(C2SApplyStats(workingAlloc.toMap()))
            onClose()
        }.pos(width / 2 - actionWidth / 2, actionY).size(actionWidth, 18).build().also(::addRenderableWidget)

        refreshButtons()
        syncRowButtons(layout)
    }

    private fun increment(row: Row) {
        val current = workingAlloc[row.def.id] ?: 0
        val cap = if (row.def.maxPoints >= 0) row.def.maxPoints else Int.MAX_VALUE
        if (workingUnspent > 0 && current < cap) {
            workingAlloc[row.def.id] = current + 1
            workingUnspent--
            preview(row.def)
            refreshButtons()
        }
    }

    override fun tick() {
        super.tick()
        previewPulses.replaceAll { _, ticks -> ticks - 1 }
        previewPulses.entries.removeIf { it.value <= 0 }
    }

    private fun preview(def: ClientStatDef) {
        val aspect = AspectIdentity.fromStatId(def.id) ?: return
        previewPulses[def.id] = 8
        minecraft?.soundManager?.play(SimpleSoundInstance.forUI(ModSounds.forAspect(aspect), 1.0f, .32f))
    }

    private fun decrement(row: Row) {
        val current = workingAlloc[row.def.id] ?: 0
        val committed = originalAlloc[row.def.id] ?: 0
        if (current > committed) {
            val next = current - 1
            if (next == 0) workingAlloc.remove(row.def.id) else workingAlloc[row.def.id] = next
            workingUnspent++
            refreshButtons()
        }
    }

    private fun refreshButtons() {
        var hasDiff = false
        rows.forEach { row ->
            val current = workingAlloc[row.def.id] ?: 0
            val original = originalAlloc[row.def.id] ?: 0
            val cap = if (row.def.maxPoints >= 0) row.def.maxPoints else Int.MAX_VALUE
            row.plus?.active = workingUnspent > 0 && current < cap
            row.minus?.active = current > original
            if (current != original) hasDiff = true
        }
        applyButton?.active = hasDiff
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderBackground(guiGraphics)
        val layout = layout()
        val properties = buildPropertyRows()
        leftContentHeight = rows.size * STAT_ROW_HEIGHT
        rightContentHeight = properties.size * PROPERTY_ROW_HEIGHT
        clampScrollOffsets(layout)
        syncRowButtons(layout)

        drawPanel(guiGraphics, layout)
        drawSummary(guiGraphics, layout)
        drawColumnHeaders(guiGraphics, layout)
        drawStatRows(guiGraphics, layout, mouseX, mouseY)
        drawPropertyRows(guiGraphics, layout, properties, mouseX, mouseY)
        drawScrollbars(guiGraphics, layout)
        guiGraphics.fill(layout.panelX + 1, layout.footerTop, layout.panelX + layout.panelWidth - 1, layout.footerTop + 1, RULE_COLOR)

        super.render(guiGraphics, mouseX, mouseY, partialTick)
        drawTooltip(guiGraphics, layout, mouseX, mouseY)
    }

    private fun drawPanel(guiGraphics: GuiGraphics, layout: Layout) {
        borderedFill(guiGraphics, layout.panelX, layout.panelY, layout.panelX + layout.panelWidth,
            layout.panelY + layout.panelHeight, PANEL_BORDER, PANEL_BACKGROUND)
        val titleText = title.string
        guiGraphics.drawString(font, titleText, width / 2 - font.width(titleText) / 2,
            layout.panelY + 8, PRIMARY_TEXT, false)
    }

    private fun drawSummary(guiGraphics: GuiGraphics, layout: Layout) {
        val x = layout.panelX + 8
        val y = layout.panelY + SUMMARY_TOP
        val right = layout.panelX + layout.panelWidth - 8
        guiGraphics.fill(x, y, right, y + SUMMARY_HEIGHT, SUMMARY_BACKGROUND)
        guiGraphics.fill(width / 2, y + 4, width / 2 + 1, y + SUMMARY_HEIGHT - 4, RULE_COLOR)
        val points = Component.translatable("screen.rpg_stats.unspent", workingUnspent.toString()).string
        val peak = Component.translatable("screen.rpg_stats.life_peak", lifePeak.toString()).string
        guiGraphics.drawString(font, points, layout.leftX, y + 8, PRIMARY_TEXT, false)
        guiGraphics.drawString(font, peak, layout.rightX + layout.columnWidth - font.width(peak), y + 8, PRIMARY_TEXT, false)
    }

    private fun drawColumnHeaders(guiGraphics: GuiGraphics, layout: Layout) {
        val y = layout.panelY + HEADER_TOP
        guiGraphics.fill(layout.leftX, y, layout.leftX + layout.columnWidth, y + 12, HEADER_BACKGROUND)
        guiGraphics.fill(layout.rightX, y, layout.rightX + layout.columnWidth, y + 12, HEADER_BACKGROUND)
        guiGraphics.drawString(font, Component.translatable("screen.rpg_stats.left_header"), layout.leftX + 4, y + 2, PRIMARY_TEXT, false)
        guiGraphics.drawString(font, Component.translatable("screen.rpg_stats.right_header"), layout.rightX + 4, y + 2, PRIMARY_TEXT, false)
    }

    private fun drawStatRows(guiGraphics: GuiGraphics, layout: Layout, mouseX: Int, mouseY: Int) {
        guiGraphics.enableScissor(layout.leftX, layout.contentTop, layout.leftX + layout.columnWidth,
            layout.contentTop + layout.viewportHeight)
        rows.forEachIndexed { index, row ->
            val rowY = layout.contentTop + index * STAT_ROW_HEIGHT - leftScrollOffset.toInt()
            if (rowY + STAT_ROW_HEIGHT < layout.contentTop || rowY > layout.contentTop + layout.viewportHeight) return@forEachIndexed
            val hovered = mouseX in layout.leftX until (layout.leftX + layout.columnWidth) &&
                mouseY in rowY until (rowY + STAT_ROW_HEIGHT) &&
                mouseY in layout.contentTop until (layout.contentTop + layout.viewportHeight)
            val pulse = previewPulses[row.def.id] ?: 0
            guiGraphics.fill(layout.leftX, rowY, layout.leftX + layout.columnWidth, rowY + STAT_ROW_HEIGHT - 1,
                if (pulse > 0) ((0x50 + pulse * 8) shl 24) or (row.def.color and 0xFFFFFF)
                else if (hovered) ROW_HOVER else if (index % 2 == 0) ROW_BACKGROUND else ROW_ALTERNATE)
            guiGraphics.fill(layout.leftX, rowY, layout.leftX + 3, rowY + STAT_ROW_HEIGHT - 1, opaque(row.def.color))
            val aspect = AspectIdentity.fromStatId(row.def.id)
            if (aspect != null) guiGraphics.blit(AspectIdentity.BADGES, layout.leftX + 5, rowY + 7,
                aspect.index * 18f, 0f, 18, 18, 144, 18)
            val textX = layout.leftX + 27
            val maxTextWidth = layout.columnWidth - CONTROL_WIDTH - 33
            val naturalName = Component.translatable(row.def.nameKey).string
            val namedIdentity = if (aspect == null) naturalName else "$naturalName — ${aspect.label}"
            guiGraphics.drawString(font, ellipsize(namedIdentity, maxTextWidth),
                textX, rowY + 4, opaque(row.def.color), false)
            val points = workingAlloc[row.def.id] ?: 0
            guiGraphics.drawString(font, ellipsize(buildRowSummary(row.def, points).string, maxTextWidth),
                textX, rowY + 17, SECONDARY_TEXT, false)
            val counter = formatPointCounter(row.def, points)
            val counterCenter = layout.leftX + layout.columnWidth - 37
            guiGraphics.drawString(font, counter, counterCenter - font.width(counter) / 2, rowY + 12, PRIMARY_TEXT, false)
        }
        guiGraphics.disableScissor()
    }

    private fun drawPropertyRows(guiGraphics: GuiGraphics, layout: Layout, properties: List<PropertyRow>, mouseX: Int, mouseY: Int) {
        guiGraphics.enableScissor(layout.rightX, layout.contentTop, layout.rightX + layout.columnWidth,
            layout.contentTop + layout.viewportHeight)
        properties.forEachIndexed { index, property ->
            val rowY = layout.contentTop + index * PROPERTY_ROW_HEIGHT - rightScrollOffset.toInt()
            if (rowY + PROPERTY_ROW_HEIGHT < layout.contentTop || rowY > layout.contentTop + layout.viewportHeight) return@forEachIndexed
            val hovered = mouseX in layout.rightX until (layout.rightX + layout.columnWidth) &&
                mouseY in rowY until (rowY + PROPERTY_ROW_HEIGHT) &&
                mouseY in layout.contentTop until (layout.contentTop + layout.viewportHeight)
            guiGraphics.fill(layout.rightX, rowY, layout.rightX + layout.columnWidth, rowY + PROPERTY_ROW_HEIGHT - 1,
                if (hovered) ROW_HOVER else if (index % 2 == 0) ROW_BACKGROUND else ROW_ALTERNATE)
            guiGraphics.fill(layout.rightX, rowY, layout.rightX + 3, rowY + PROPERTY_ROW_HEIGHT - 1, opaque(property.color))
            val textX = layout.rightX + 8
            val textWidth = layout.columnWidth - 16
            val sourceSuffix = if (property.sourceName != property.friendlyName) {
                " ← ${compactSourceName(property.friendlyName, property.sourceName)}"
            } else ""
            guiGraphics.drawString(font, ellipsize(property.friendlyName + sourceSuffix, textWidth),
                textX, rowY + 4, opaque(property.color), false)
            val current = formatEffectValue(property.originalTotal, property.displayAsPercent)
            val changed = property.originalTotal != property.workingTotal
            val values = if (changed) {
                val pending = formatEffectValue(property.workingTotal, property.displayAsPercent)
                Component.translatable("screen.rpg_stats.current_pending", current, pending).string
            } else Component.translatable("screen.rpg_stats.current", current).string
            val valueColor = when {
                property.workingTotal > property.originalTotal -> PENDING_UP
                property.workingTotal < property.originalTotal -> PENDING_DOWN
                else -> SECONDARY_TEXT
            }
            guiGraphics.drawString(font, ellipsize(values, textWidth), textX, rowY + 16, valueColor, false)
        }
        guiGraphics.disableScissor()
    }

    private fun drawScrollbars(guiGraphics: GuiGraphics, layout: Layout) {
        drawScrollbar(guiGraphics, layout.leftX + layout.columnWidth - 2, layout.contentTop,
            layout.viewportHeight, leftContentHeight, leftScrollOffset)
        drawScrollbar(guiGraphics, layout.rightX + layout.columnWidth - 2, layout.contentTop,
            layout.viewportHeight, rightContentHeight, rightScrollOffset)
    }

    private fun drawScrollbar(guiGraphics: GuiGraphics, x: Int, y: Int, viewportHeight: Int,
                              contentHeight: Int, offset: Double) {
        if (contentHeight <= viewportHeight) return
        guiGraphics.fill(x, y, x + 2, y + viewportHeight, 0xAA303741.toInt())
        val thumbHeight = maxOf(16, viewportHeight * viewportHeight / contentHeight)
        val maxScroll = contentHeight - viewportHeight
        val thumbY = y + (offset / maxScroll * (viewportHeight - thumbHeight)).toInt()
        guiGraphics.fill(x, thumbY, x + 2, thumbY + thumbHeight, 0xFFD4D8DE.toInt())
    }

    private fun drawTooltip(guiGraphics: GuiGraphics, layout: Layout, mouseX: Int, mouseY: Int) {
        if (mouseX !in layout.leftX until (layout.leftX + layout.columnWidth) ||
            mouseY !in layout.contentTop until (layout.contentTop + layout.viewportHeight)) return
        val index = (mouseY - layout.contentTop + leftScrollOffset.toInt()) / STAT_ROW_HEIGHT
        val row = rows.getOrNull(index) ?: return
        guiGraphics.renderComponentTooltip(font, buildTooltip(row.def), mouseX, mouseY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollDelta: Double): Boolean {
        val layout = layout()
        if (mouseY !in layout.contentTop.toDouble()..(layout.contentTop + layout.viewportHeight).toDouble())
            return super.mouseScrolled(mouseX, mouseY, scrollDelta)
        when {
            mouseX in layout.leftX.toDouble()..(layout.leftX + layout.columnWidth).toDouble() -> {
                leftScrollOffset = scroll(leftScrollOffset, leftContentHeight, layout.viewportHeight, scrollDelta)
                syncRowButtons(layout)
                return true
            }
            mouseX in layout.rightX.toDouble()..(layout.rightX + layout.columnWidth).toDouble() -> {
                rightScrollOffset = scroll(rightScrollOffset, rightContentHeight, layout.viewportHeight, scrollDelta)
                return true
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollDelta)
    }

    private fun scroll(offset: Double, contentHeight: Int, viewportHeight: Int, delta: Double): Double =
        Mth.clamp(offset - delta * 12, 0.0, maxOf(0, contentHeight - viewportHeight).toDouble())

    private fun clampScrollOffsets(layout: Layout) {
        leftScrollOffset = Mth.clamp(leftScrollOffset, 0.0, maxOf(0, leftContentHeight - layout.viewportHeight).toDouble())
        rightScrollOffset = Mth.clamp(rightScrollOffset, 0.0, maxOf(0, rightContentHeight - layout.viewportHeight).toDouble())
    }

    private fun syncRowButtons(layout: Layout) {
        rows.forEachIndexed { index, row ->
            val y = layout.contentTop + index * STAT_ROW_HEIGHT + 7 - leftScrollOffset.toInt()
            val visible = y >= layout.contentTop && y + CONTROL_BUTTON_SIZE <= layout.contentTop + layout.viewportHeight
            row.minus?.y = y
            row.plus?.y = y
            row.minus?.visible = visible
            row.plus?.visible = visible
        }
    }

    private fun buildPropertyRows(): List<PropertyRow> {
        val providers = linkedMapOf<String, PropertyProvider>()
        rows.forEach { row ->
            val sourceName = Component.translatable(row.def.nameKey).string
            row.def.effects.forEach { effect ->
                val current = providers[effect.attributeId]
                val candidate = PropertyProvider(effect.attributeId, effect.operation,
                    resolveAttributeName(effect.attributeId).string, sourceName,
                    if (effect.isPrimary) row.def.color else lightenColor(row.def.color),
                    effect.curve.perPoint, effect.isPrimary, effect.displayAsPercent)
                if (current == null || (candidate.primary && !current.primary) ||
                    (candidate.primary == current.primary && candidate.perPoint > current.perPoint)) {
                    providers[effect.attributeId] = candidate
                }
            }
        }
        return providers.values.map { provider ->
            var originalTotal = 0.0
            var workingTotal = 0.0
            rows.forEach { row ->
                val originalPoints = originalAlloc[row.def.id] ?: 0
                val workingPoints = workingAlloc[row.def.id] ?: 0
                row.def.effects.filter { it.attributeId == provider.attributeId }.forEach { effect ->
                    originalTotal += Curves.eval(originalPoints, effect.curve.toCommon())
                    workingTotal += Curves.eval(workingPoints, effect.curve.toCommon())
                }
            }
            PropertyRow(provider.friendlyName, provider.sourceName, provider.color,
                provider.operation, provider.displayAsPercent, originalTotal, workingTotal)
        }
    }

    private fun buildRowSummary(def: ClientStatDef, currentPoints: Int): Component {
        val primary = def.effects.firstOrNull { it.isPrimary } ?: def.effects.firstOrNull()
            ?: return Component.translatable("tooltip.rpg_stats.no_effects")
        val attributeName = resolveAttributeName(primary.attributeId)
        val statName = Component.translatable(def.nameKey).string
        val result = if (attributeName.string.equals(statName, ignoreCase = true)) {
            Component.translatable("screen.rpg_stats.next_point_same", formatMarginalGain(primary, currentPoints))
        } else {
            Component.translatable("screen.rpg_stats.next_point", formatMarginalGain(primary, currentPoints), attributeName)
        }
        val secondaryCount = (def.effects.size - 1).coerceAtLeast(0)
        if (secondaryCount > 0) result.append(Component.translatable("screen.rpg_stats.more_effects", secondaryCount.toString()))
        return result
    }

    private fun buildTooltip(def: ClientStatDef): List<Component> {
        if (def.effects.isEmpty()) return listOf(Component.translatable("tooltip.rpg_stats.no_effects"))
        val currentPoints = workingAlloc[def.id] ?: 0
        val aspect = AspectIdentity.fromStatId(def.id)
        val identityLine = if (aspect == null) {
            Component.translatable("tooltip.rpg_stats.aspect", Component.translatable("aspect.rpg_stats.${def.id.substringAfter(':')}"))
                .withStyle { it.withColor(def.color) }
        } else {
            Component.literal(aspect.badge + " ").withStyle { it.withFont(AspectIdentity.FONT) }
                .append(Component.translatable("tooltip.rpg_stats.aspect", Component.literal(aspect.label))
                    .withStyle { it.withFont(ResourceLocation("minecraft", "default")).withColor(def.color) })
        }
        val lines = mutableListOf(
            identityLine,
            Component.translatable("tooltip.rpg_stats.points_header", formatPointCounter(def, currentPoints)),
            Component.translatable("tooltip.rpg_stats.effects_header")
        )
        def.effects.forEach { effect ->
            val current = Curves.eval(currentPoints, effect.curve.toCommon())
            val next = Curves.eval(currentPoints + 1, effect.curve.toCommon()) - current
            val color = if (effect.isPrimary) def.color else lightenColor(def.color)
            val role = if (effect.isPrimary) "tooltip.rpg_stats.main_effect" else "tooltip.rpg_stats.bonus_effect"
            lines += Component.translatable(role).append(Component.literal(": "))
                .append(resolveAttributeName(effect.attributeId).copy().withStyle { it.withColor(color) })
                .append(Component.literal(" ")).append(Component.translatable("tooltip.rpg_stats.now_next",
                    formatEffectValue(current, effect.displayAsPercent), formatEffectValue(next, effect.displayAsPercent)))
        }
        return lines
    }

    private fun resolveAttributeName(attributeId: String): Component {
        val id = ResourceLocation.tryParse(attributeId) ?: return Component.literal(attributeId)
        val key = "attr.${id.namespace}.${id.path}"
        val translated = Component.translatable(key)
        return if (translated.string == key) Component.literal(attributeId) else translated
    }

    private fun compactSourceName(propertyName: String, sourceName: String): String {
        val propertyWords = propertyName.split(' ')
        val sourceWords = sourceName.split(' ').toMutableList()
        while (propertyWords.isNotEmpty() && sourceWords.size > 1 &&
            propertyWords.last().equals(sourceWords.last(), ignoreCase = true)) {
            sourceWords.removeLast()
        }
        return sourceWords.joinToString(" ")
    }

    private fun formatMarginalGain(effect: ClientEffectDef, currentPoints: Int): String {
        val current = Curves.eval(currentPoints, effect.curve.toCommon())
        val next = Curves.eval(currentPoints + 1, effect.curve.toCommon())
        return formatEffectValue(next - current, effect.displayAsPercent)
    }

    private fun formatPointCounter(def: ClientStatDef, points: Int): String =
        if (def.maxPoints >= 0) "$points/${def.maxPoints}" else points.toString()

    private fun formatEffectValue(value: Double, multiplier: Boolean): String =
        if (multiplier) formatSignedPercent(value) else formatSignedNumber(value)

    private fun formatSignedPercent(value: Double): String {
        val percent = value * 100.0
        return "${if (percent >= 0.0) "+" else ""}${trimNumber(percent)}%"
    }

    private fun formatSignedNumber(value: Double): String =
        "${if (value >= 0.0) "+" else ""}${trimNumber(value)}"

    private fun trimNumber(value: Double): String = when {
        abs(value) >= 100.0 -> String.format("%.0f", value)
        abs(value) >= 10.0 -> String.format("%.1f", value)
        abs(value) >= 1.0 -> String.format("%.2f", value)
        abs(value) >= 0.01 -> String.format("%.3f", value)
        else -> String.format("%.4f", value)
    }

    private fun ClientCurveDef.toCommon() = CurveDef(type, cap, k, perPoint, min, max)

    private fun lightenColor(color: Int, factor: Double = 1.35): Int {
        val red = minOf(255, (((color shr 16) and 0xFF) * factor).toInt())
        val green = minOf(255, (((color shr 8) and 0xFF) * factor).toInt())
        val blue = minOf(255, ((color and 0xFF) * factor).toInt())
        return (red shl 16) or (green shl 8) or blue
    }

    private fun opaque(color: Int): Int = color or 0xFF000000.toInt()

    private fun ellipsize(text: String, maxWidth: Int): String {
        if (font.width(text) <= maxWidth) return text
        val ellipsis = "…"
        return font.plainSubstrByWidth(text, maxOf(0, maxWidth - font.width(ellipsis))) + ellipsis
    }

    private fun borderedFill(guiGraphics: GuiGraphics, left: Int, top: Int, right: Int, bottom: Int,
                             border: Int, fill: Int) {
        guiGraphics.fill(left, top, right, bottom, border)
        guiGraphics.fill(left + 1, top + 1, right - 1, bottom - 1, fill)
    }

    private fun layout(): Layout {
        val panelWidth = minOf(PANEL_MAX_WIDTH, width - OUTER_MARGIN * 2).coerceAtLeast(300)
        val panelHeight = minOf(PANEL_MAX_HEIGHT, height - OUTER_MARGIN * 2).coerceAtLeast(240)
        val panelX = (width - panelWidth) / 2
        val panelY = (height - panelHeight) / 2
        val columnWidth = (panelWidth - PANEL_PADDING * 2 - COLUMN_GAP) / 2
        val leftX = panelX + PANEL_PADDING
        val rightX = leftX + columnWidth + COLUMN_GAP
        val contentTop = panelY + CONTENT_TOP
        val footerTop = panelY + panelHeight - FOOTER_HEIGHT
        return Layout(panelX, panelY, panelWidth, panelHeight, leftX, rightX, columnWidth,
            contentTop, footerTop, footerTop - contentTop - 4)
    }

    override fun isPauseScreen(): Boolean = false
}
