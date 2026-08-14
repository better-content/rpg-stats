# RPG Stats visual theory

The stats screen is a **character ledger**: the left side records the points the player chooses, and the right side shows the properties those choices produce. It should feel measured and dependable rather than decorative. A player should be able to answer three questions at a glance: where are my points, what does one more point do, and what will change if I apply this draft?

## Composition

- Build one centered panel with two equal columns and a visible central gutter. The title, summary strip, content area, and footer all belong to that same frame.
- Mirror the columns around the gutter. Their headers share a baseline, their content begins on the same baseline, and their outer padding is identical.
- Use an 8-pixel spacing unit, with 4-pixel half-steps only for compact controls and rules. Repeated rows use one fixed height.
- Keep actions in a full-width footer. Controls must not appear to belong to whichever list happens to be shorter.
- At narrow sizes, preserve equal columns and reduce incidental detail before breaking alignment.

## Hierarchy

1. The screen title names the system.
2. A quiet summary strip pairs **Points available** with **Life peak**.
3. Column headers state the relationship: **Allocate points** on the left and **Resulting properties** on the right.
4. Rows use a stable pattern: name first, explanatory property second, numeric state aligned at the trailing edge.
5. Apply and Reset are visually separated from the data and remain in predictable positions.

Color identifies a stat family; it does not carry meaning alone. Primary text stays high-contrast, secondary explanations use neutral gray, pending increases use green, and pending decreases use red. Panel surfaces, rules, alignment, labels, and signs must still communicate the screen in grayscale.

## Property language

Properties are outcomes, not implementation identifiers. Every visible property must include:

- a player-facing name (for example, **Attack Damage** rather than a registry ID);
- a meaningful unit or sign (`+1.25`, `+8%`);
- an explicit current value and, only when edited, a pending value;
- a source stat when the relationship is not already obvious.

The left row states the next-point effect in plain language: `Next point: +0.25 Attack Damage`. The right row labels values rather than presenting an unexplained arrow: `Current +1.00` and `Pending +1.25`.

## Interaction and states

- Minus, point count, and plus form one right-aligned control group with symmetric gaps and equal button sizes.
- Disabled controls remain legible but quiet. Hovered rows get a restrained surface highlight, not a layout shift.
- Scroll regions show a scrollbar when content overflows, and buttons move and clip with their rows.
- Pending edits are the only animated or bright state. Applying commits the draft; Reset restores the current allocation draft to zero as before.
- Tooltips provide full effect breakdowns, but the base screen must explain the primary effect without requiring hover.

## Review checklist

- Left and right outer edges, headers, row starts, and footer edges are symmetrical.
- All repeated gaps are 4, 8, 12, or 16 pixels; no one-off spacing is used to patch alignment.
- Long translated names clip or elide before colliding with controls or values.
- At the default harness size, the player can distinguish points, next-point effects, current properties, and pending properties without a tooltip.
- The layout remains readable at common GUI scales and at the minimum supported window size.
