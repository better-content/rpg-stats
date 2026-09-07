# RPG Stats visual theory

The stats screen is a **character ledger**: the upper grid records the points the player chooses, and the lower pane shows the properties those choices produce. It should feel measured and dependable rather than decorative. A player should be able to answer three questions at a glance: where are my points, what does one more point do, and what will change if I apply this draft?

## Composition

- Build one centered panel with a two-column, four-row capability grid above one full-width resulting-properties pane. The title, summary strip, content sections, and footer all belong to that same frame.
- Mirror capability tiles around a narrow gutter. Every tile uses the same height and preserves its name, point controls, and next-point line.
- Use 2-pixel section separators, a 6-pixel grid gutter, and 4/8-pixel spacing elsewhere; these compact intervals preserve normal text size instead of shrinking the font.
- Keep all eight capabilities visible without scrolling, including at 427×240 logical pixels. Only resulting properties may scroll.
- Keep actions in a full-width footer. At narrow sizes, the properties viewport contracts while the capability grid remains complete.

## Hierarchy

1. The screen title names the system.
2. A quiet summary strip pairs **Points available** with **Life peak**.
3. Section headers state the relationship: **Develop capabilities** above and **Resulting properties** below.
4. Capability tiles use a stable pattern: badge and name first, next-point effect second, numeric controls aligned at the trailing edge.
5. Apply is visually separated from the data and remains centered in a predictable position.

Color identifies a stat family; it does not carry meaning alone. Primary text stays high-contrast, secondary explanations use neutral gray, pending increases use green, and pending decreases use red. Panel surfaces, rules, alignment, labels, and signs must still communicate the screen in grayscale.

## Property language

Properties are outcomes, not implementation identifiers. Every visible property must include:

- a player-facing name (for example, **Attack Damage** rather than a registry ID);
- a meaningful unit or sign (`+1.25`, `+8%`);
- an explicit current value and, only when edited, a pending value;
- a source stat when the relationship is not already obvious.

The left row states the next-point effect in plain language: `Next point: +0.25 Attack Damage`. The right row labels values rather than presenting an unexplained arrow: `Current +1.00` and `Pending +1.25`.

## Interaction and states

- Minus, point count, and plus form one right-aligned control group with symmetric gaps and equal button sizes. Minus only undoes points added in the current draft; committed points remain locked until death.
- Disabled controls remain legible but quiet. Hovered rows get a restrained surface highlight, not a layout shift.
- The property pane shows a scrollbar when its visible, nonzero rows overflow. Capability tiles and their buttons never scroll.
- Hide a property only when both its committed and draft totals are effectively zero. Retain committed values and every transition to or from zero; show the empty-state instruction when no properties remain.
- Pending edits are the only animated or bright state. Applying commits the draft; closing the screen discards it. Death is the only event that resets committed allocations.
- Tooltips provide full effect breakdowns, but the base screen must explain the primary effect without requiring hover.

## Review checklist

- Capability columns, row starts, section headers, and footer edges remain aligned.
- All repeated gaps are 4, 8, 12, or 16 pixels; no one-off spacing is used to patch alignment.
- Long translated names clip or elide before colliding with controls or values.
- At both harness scales, all eight capabilities are simultaneously visible and the player can distinguish points, next-point effects, current properties, and pending properties without a tooltip.
- At GUI scale 3 on a 1280×720 window, the 427×240 logical layout retains normal font size, complete controls, and at least one complete property row.
