# RPG Stats

Pack-owned RPG stats and diminishing-returns system for Forge `1.20.1`.

## Common commands

```bash
./gradlew verifyFast
./gradlew verifyFull
```

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
