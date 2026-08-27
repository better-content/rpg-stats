# RPG Stats

Pack-owned RPG stats and diminishing-returns system for Forge `1.20.1`.

## Life aspects

Players commit Life points irreversibly during a life to seven broad capabilities: Impact, Tempo, Work, Mobility, Endurance, Robustness, and Control. One aspect may project into several concrete attributes owned by vanilla, Epic Fight, TConstruct, TACZ, Goety, Cold Sweat, or this mod; those integrations do not share a global aspect meter. Twenty points reach half of each configured cap through the `cap × points / (points + 20)` curve, and death clears the allocation ledger.

The stat resources use the same portable glyph and CVD-screened color contract as nutrition and pack-authored TConstruct material profiles. Renewal remains part of the pack vocabulary but is intentionally absent here until a permanent Life capability is justified by the concrete-mechanic inventory.

## Common commands

```bash
./gradlew verifyFast
./gradlew verifyFull
```

For an isolated 1280×720, GUI-scale-2 visual fixture containing only Minecraft, the RPG Stats
runtime, and development harness code, run:

```bash
./gradlew runVisualHarness
```

The client opens the seeded stats screen, writes a screenshot under
`run-visual-harness/screenshots/`, and exits automatically.

`verifyFull` currently matches `verifyFast`; this repo does not yet expose a distinct GameTest or coverage-enforced full lane.

## Release artifact

Deploy the reobfuscated runtime jar from:

- `build/libs/rpg-stats-<version>.jar`

## Community and support

For modpack and mod discussion, playtest feedback, and bug reports, join the [Better Content Discord](https://discord.gg/EkRnZbzqS9).

## Canonical identity

- Repository and release artifact: `rpg-stats`
- Mod ID and resource namespace: `rpg_stats`
- Java package: `com.bettercontent.rpgstats`
- Validation: `./gradlew verifyFull`

This normalization is a clean break. Worlds, configuration files, and integrations created for earlier identities are not migrated or aliased.
