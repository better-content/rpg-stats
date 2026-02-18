#!/bin/bash

# Script to generate all 100 affix JSON files
OUTPUT_DIR="src/main/resources/data/tconaffixes/affixes"
mkdir -p "$OUTPUT_DIR"

# Clear existing files
rm -f "$OUTPUT_DIR"/*.json

echo "Generating 100 affix JSON files..."

# HEADS - Combat (15)
cat > "$OUTPUT_DIR/sharp.json" << 'EOF'
{
  "weight": 15,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 1.0, "max": 2.0 }
  ],
  "name_type": "prefix",
  "name_text": "Sharp"
}
EOF

cat > "$OUTPUT_DIR/keen.json" << 'EOF'
{
  "weight": 15,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 0.5, "max": 1.5 }
  ],
  "name_type": "prefix",
  "name_text": "Keen"
}
EOF

cat > "$OUTPUT_DIR/brutal.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 2.0, "max": 4.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": -0.1, "max": 0.0 }
  ],
  "name_type": "prefix",
  "name_text": "Brutal"
}
EOF

cat > "$OUTPUT_DIR/savage.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 1.5, "max": 3.5 }
  ],
  "name_type": "prefix",
  "name_text": "Savage"
}
EOF

cat > "$OUTPUT_DIR/deadly.json" << 'EOF'
{
  "weight": 6,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 3.0, "max": 5.0 }
  ],
  "name_type": "prefix",
  "name_text": "Deadly"
}
EOF

cat > "$OUTPUT_DIR/vicious.json" << 'EOF'
{
  "weight": 8,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 1.0, "max": 2.5 },
    { "type": "attr_add", "attribute": "minecraft:generic.knockback_resistance", "min": 0.05, "max": 0.15 }
  ],
  "name_type": "prefix",
  "name_text": "Vicious"
}
EOF

cat > "$OUTPUT_DIR/piercing.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 1.5, "max": 3.0 }
  ],
  "name_type": "prefix",
  "name_text": "Piercing"
}
EOF

cat > "$OUTPUT_DIR/rending.json" << 'EOF'
{
  "weight": 7,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 2.5, "max": 4.5 }
  ],
  "name_type": "prefix",
  "name_text": "Rending"
}
EOF

cat > "$OUTPUT_DIR/crushing.json" << 'EOF'
{
  "weight": 9,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 2.0, "max": 3.0 },
    { "type": "mining_speed_mul", "min": 0.95, "max": 1.0 }
  ],
  "name_type": "prefix",
  "name_text": "Crushing"
}
EOF

cat > "$OUTPUT_DIR/lethal.json" << 'EOF'
{
  "weight": 5,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 3.5, "max": 6.0 }
  ],
  "name_type": "prefix",
  "name_text": "Lethal"
}
EOF

cat > "$OUTPUT_DIR/berserkers.json" << 'EOF'
{
  "weight": 6,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 2.0, "max": 4.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.1, "max": 0.2 }
  ],
  "name_type": "prefix",
  "name_text": "Berserker's"
}
EOF

cat > "$OUTPUT_DIR/executioners.json" << 'EOF'
{
  "weight": 3,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 4.0, "max": 7.0 }
  ],
  "name_type": "prefix",
  "name_text": "Executioner's"
}
EOF

cat > "$OUTPUT_DIR/vampiric.json" << 'EOF'
{
  "weight": 4,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 1.0, "max": 2.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.max_health", "min": 1.0, "max": 3.0 }
  ],
  "name_type": "prefix",
  "name_text": "Vampiric"
}
EOF

cat > "$OUTPUT_DIR/titans.json" << 'EOF'
{
  "weight": 2,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 5.0, "max": 8.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": -0.2, "max": -0.1 }
  ],
  "name_type": "prefix",
  "name_text": "Titan's"
}
EOF

cat > "$OUTPUT_DIR/dragons_head.json" << 'EOF'
{
  "weight": 2,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 4.0, "max": 6.0 },
    { "type": "mining_speed_mul", "min": 1.1, "max": 1.3 }
  ],
  "name_type": "prefix",
  "name_text": "Dragon's"
}
EOF

# HEADS - Mining (15)
cat > "$OUTPUT_DIR/efficient.json" << 'EOF'
{
  "weight": 15,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.1, "max": 1.2 }
  ],
  "name_type": "prefix",
  "name_text": "Efficient"
}
EOF

cat > "$OUTPUT_DIR/miners.json" << 'EOF'
{
  "weight": 14,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.05, "max": 1.15 }
  ],
  "name_type": "prefix",
  "name_text": "Miner's"
}
EOF

cat > "$OUTPUT_DIR/excavators.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.15, "max": 1.3 }
  ],
  "name_type": "prefix",
  "name_text": "Excavator's"
}
EOF

cat > "$OUTPUT_DIR/quarrymans.json" << 'EOF'
{
  "weight": 9,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.2, "max": 1.35 }
  ],
  "name_type": "prefix",
  "name_text": "Quarryman's"
}
EOF

cat > "$OUTPUT_DIR/tunnelers.json" << 'EOF'
{
  "weight": 7,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.3, "max": 1.5 }
  ],
  "name_type": "prefix",
  "name_text": "Tunneler's"
}
EOF

cat > "$OUTPUT_DIR/earth_breaker.json" << 'EOF'
{
  "weight": 6,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.35, "max": 1.55 }
  ],
  "name_type": "prefix",
  "name_text": "Earth Breaker"
}
EOF

cat > "$OUTPUT_DIR/stone_crusher.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.1, "max": 1.25 }
  ],
  "name_type": "prefix",
  "name_text": "Stone Crusher"
}
EOF

cat > "$OUTPUT_DIR/ore_seeker.json" << 'EOF'
{
  "weight": 8,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.15, "max": 1.3 },
    { "type": "attr_add", "attribute": "minecraft:generic.luck", "min": 0.5, "max": 1.5 }
  ],
  "name_type": "prefix",
  "name_text": "Ore Seeker"
}
EOF

cat > "$OUTPUT_DIR/drill.json" << 'EOF'
{
  "weight": 11,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.2, "max": 1.4 }
  ],
  "name_type": "prefix",
  "name_text": "Drill"
}
EOF

cat > "$OUTPUT_DIR/jackhammer.json" << 'EOF'
{
  "weight": 7,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.4, "max": 1.6 }
  ],
  "name_type": "prefix",
  "name_text": "Jackhammer"
}
EOF

cat > "$OUTPUT_DIR/bedrock_bane.json" << 'EOF'
{
  "weight": 3,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.6, "max": 2.0 }
  ],
  "name_type": "prefix",
  "name_text": "Bedrock Bane"
}
EOF

cat > "$OUTPUT_DIR/prospectors.json" << 'EOF'
{
  "weight": 9,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.0, "max": 1.15 },
    { "type": "attr_add", "attribute": "minecraft:generic.luck", "min": 1.0, "max": 2.0 }
  ],
  "name_type": "prefix",
  "name_text": "Prospector's"
}
EOF

cat > "$OUTPUT_DIR/fortunes.json" << 'EOF'
{
  "weight": 5,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.luck", "min": 2.0, "max": 4.0 }
  ],
  "name_type": "prefix",
  "name_text": "Fortune's"
}
EOF

cat > "$OUTPUT_DIR/vein_miner.json" << 'EOF'
{
  "weight": 3,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.3, "max": 1.6 },
    { "type": "attr_add", "attribute": "minecraft:generic.luck", "min": 1.5, "max": 3.0 }
  ],
  "name_type": "prefix",
  "name_text": "Vein Miner"
}
EOF

cat > "$OUTPUT_DIR/dwarven.json" << 'EOF'
{
  "weight": 6,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.25, "max": 1.45 },
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 0.5, "max": 1.5 }
  ],
  "name_type": "prefix",
  "name_text": "Dwarven"
}
EOF

# HEADS - Balanced (10)
cat > "$OUTPUT_DIR/warriors.json" << 'EOF'
{
  "weight": 12,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 0.5, "max": 1.5 },
    { "type": "mining_speed_mul", "min": 1.05, "max": 1.15 }
  ],
  "name_type": "prefix",
  "name_text": "Warrior's"
}
EOF

cat > "$OUTPUT_DIR/adventurers.json" << 'EOF'
{
  "weight": 13,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 0.5, "max": 1.0 },
    { "type": "mining_speed_mul", "min": 1.05, "max": 1.1 }
  ],
  "name_type": "prefix",
  "name_text": "Adventurer's"
}
EOF

cat > "$OUTPUT_DIR/heros.json" << 'EOF'
{
  "weight": 9,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 1.0, "max": 2.0 },
    { "type": "mining_speed_mul", "min": 1.1, "max": 1.2 }
  ],
  "name_type": "prefix",
  "name_text": "Hero's"
}
EOF

cat > "$OUTPUT_DIR/champions.json" << 'EOF'
{
  "weight": 6,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 2.0, "max": 3.0 },
    { "type": "mining_speed_mul", "min": 1.15, "max": 1.3 }
  ],
  "name_type": "prefix",
  "name_text": "Champion's"
}
EOF

cat > "$OUTPUT_DIR/legendary.json" << 'EOF'
{
  "weight": 3,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 3.0, "max": 5.0 },
    { "type": "mining_speed_mul", "min": 1.3, "max": 1.5 }
  ],
  "name_type": "prefix",
  "name_text": "Legendary"
}
EOF

cat > "$OUTPUT_DIR/masterwork.json" << 'EOF'
{
  "weight": 7,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 1.5, "max": 2.5 },
    { "type": "mining_speed_mul", "min": 1.2, "max": 1.3 }
  ],
  "name_type": "prefix",
  "name_text": "Masterwork"
}
EOF

cat > "$OUTPUT_DIR/artisans.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 0.5, "max": 1.5 },
    { "type": "mining_speed_mul", "min": 1.1, "max": 1.25 }
  ],
  "name_type": "prefix",
  "name_text": "Artisan's"
}
EOF

cat > "$OUTPUT_DIR/forgemasters.json" << 'EOF'
{
  "weight": 5,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 2.0, "max": 3.5 },
    { "type": "mining_speed_mul", "min": 1.2, "max": 1.4 }
  ],
  "name_type": "prefix",
  "name_text": "Forgemaster's"
}
EOF

cat > "$OUTPUT_DIR/ancient.json" << 'EOF'
{
  "weight": 6,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 1.5, "max": 3.0 },
    { "type": "mining_speed_mul", "min": 1.15, "max": 1.35 },
    { "type": "attr_add", "attribute": "minecraft:generic.luck", "min": 0.5, "max": 1.5 }
  ],
  "name_type": "prefix",
  "name_text": "Ancient"
}
EOF

cat > "$OUTPUT_DIR/godly.json" << 'EOF'
{
  "weight": 1,
  "applies_to": { "part_tags": ["tconstruct:heads"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 5.0, "max": 8.0 },
    { "type": "mining_speed_mul", "min": 1.5, "max": 2.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.luck", "min": 2.0, "max": 4.0 }
  ],
  "name_type": "prefix",
  "name_text": "Godly"
}
EOF

# HANDLES - Speed (15)
cat > "$OUTPUT_DIR/swift.json" << 'EOF'
{
  "weight": 15,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.1, "max": 0.2 }
  ],
  "name_type": "suffix",
  "name_text": "Swiftness"
}
EOF

cat > "$OUTPUT_DIR/quick.json" << 'EOF'
{
  "weight": 14,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.05, "max": 0.15 }
  ],
  "name_type": "suffix",
  "name_text": "Quickness"
}
EOF

cat > "$OUTPUT_DIR/rapid.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.15, "max": 0.3 }
  ],
  "name_type": "suffix",
  "name_text": "Rapidity"
}
EOF

cat > "$OUTPUT_DIR/lightning.json" << 'EOF'
{
  "weight": 6,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.3, "max": 0.5 }
  ],
  "name_type": "suffix",
  "name_text": "Lightning"
}
EOF

cat > "$OUTPUT_DIR/haste.json" << 'EOF'
{
  "weight": 13,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.1, "max": 1.2 },
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.05, "max": 0.1 }
  ],
  "name_type": "suffix",
  "name_text": "Haste"
}
EOF

cat > "$OUTPUT_DIR/speed.json" << 'EOF'
{
  "weight": 14,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.05, "max": 1.15 }
  ],
  "name_type": "suffix",
  "name_text": "Speed"
}
EOF

cat > "$OUTPUT_DIR/velocity.json" << 'EOF'
{
  "weight": 9,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.15, "max": 1.3 },
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.1, "max": 0.2 }
  ],
  "name_type": "suffix",
  "name_text": "Velocity"
}
EOF

cat > "$OUTPUT_DIR/acceleration.json" << 'EOF'
{
  "weight": 7,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.25, "max": 1.4 },
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.15, "max": 0.25 }
  ],
  "name_type": "suffix",
  "name_text": "Acceleration"
}
EOF

cat > "$OUTPUT_DIR/the_wind.json" << 'EOF'
{
  "weight": 6,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.2, "max": 0.35 },
    { "type": "attr_add", "attribute": "minecraft:generic.movement_speed", "min": 0.01, "max": 0.03 }
  ],
  "name_type": "suffix",
  "name_text": "the Wind"
}
EOF

cat > "$OUTPUT_DIR/agility.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.1, "max": 0.2 },
    { "type": "attr_add", "attribute": "minecraft:generic.movement_speed", "min": 0.01, "max": 0.02 }
  ],
  "name_type": "suffix",
  "name_text": "Agility"
}
EOF

cat > "$OUTPUT_DIR/nimbleness.json" << 'EOF'
{
  "weight": 9,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.15, "max": 0.25 }
  ],
  "name_type": "suffix",
  "name_text": "Nimbleness"
}
EOF

cat > "$OUTPUT_DIR/dexterity.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.1, "max": 0.2 }
  ],
  "name_type": "suffix",
  "name_text": "Dexterity"
}
EOF

cat > "$OUTPUT_DIR/the_falcon.json" << 'EOF'
{
  "weight": 5,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.25, "max": 0.4 }
  ],
  "name_type": "suffix",
  "name_text": "the Falcon"
}
EOF

cat > "$OUTPUT_DIR/the_cheetah.json" << 'EOF'
{
  "weight": 3,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.3, "max": 0.5 },
    { "type": "attr_add", "attribute": "minecraft:generic.movement_speed", "min": 0.02, "max": 0.04 }
  ],
  "name_type": "suffix",
  "name_text": "the Cheetah"
}
EOF

cat > "$OUTPUT_DIR/fury.json" << 'EOF'
{
  "weight": 6,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.2, "max": 0.35 },
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 0.5, "max": 1.5 }
  ],
  "name_type": "suffix",
  "name_text": "Fury"
}
EOF

# HANDLES - Control (10)
cat > "$OUTPUT_DIR/precision.json" << 'EOF'
{
  "weight": 12,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 0.5, "max": 1.0 }
  ],
  "name_type": "suffix",
  "name_text": "Precision"
}
EOF

cat > "$OUTPUT_DIR/accuracy.json" << 'EOF'
{
  "weight": 11,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 0.3, "max": 0.8 }
  ],
  "name_type": "suffix",
  "name_text": "Accuracy"
}
EOF

cat > "$OUTPUT_DIR/focus.json" << 'EOF'
{
  "weight": 9,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 0.8, "max": 1.5 }
  ],
  "name_type": "suffix",
  "name_text": "Focus"
}
EOF

cat > "$OUTPUT_DIR/mastery.json" << 'EOF'
{
  "weight": 6,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 1.0, "max": 2.0 },
    { "type": "mining_speed_mul", "min": 1.1, "max": 1.2 }
  ],
  "name_type": "suffix",
  "name_text": "Mastery"
}
EOF

cat > "$OUTPUT_DIR/control.json" << 'EOF'
{
  "weight": 12,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.05, "max": 0.1 },
    { "type": "mining_speed_mul", "min": 1.05, "max": 1.1 }
  ],
  "name_type": "suffix",
  "name_text": "Control"
}
EOF

cat > "$OUTPUT_DIR/balance.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.1, "max": 0.15 },
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 0.5, "max": 1.0 }
  ],
  "name_type": "suffix",
  "name_text": "Balance"
}
EOF

cat > "$OUTPUT_DIR/the_monk.json" << 'EOF'
{
  "weight": 5,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.15, "max": 0.25 },
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 1.0, "max": 2.0 }
  ],
  "name_type": "suffix",
  "name_text": "the Monk"
}
EOF

cat > "$OUTPUT_DIR/the_master.json" << 'EOF'
{
  "weight": 3,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.2, "max": 0.3 },
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 1.5, "max": 3.0 },
    { "type": "mining_speed_mul", "min": 1.1, "max": 1.2 }
  ],
  "name_type": "suffix",
  "name_text": "the Master"
}
EOF

cat > "$OUTPUT_DIR/expertise.json" << 'EOF'
{
  "weight": 9,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "mining_speed_mul", "min": 1.1, "max": 1.25 },
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 0.5, "max": 1.0 }
  ],
  "name_type": "suffix",
  "name_text": "Expertise"
}
EOF

cat > "$OUTPUT_DIR/perfection.json" << 'EOF'
{
  "weight": 2,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_speed", "min": 0.2, "max": 0.4 },
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 2.0, "max": 3.0 },
    { "type": "mining_speed_mul", "min": 1.2, "max": 1.4 }
  ],
  "name_type": "suffix",
  "name_text": "Perfection"
}
EOF

# HANDLES - Movement (5)
cat > "$OUTPUT_DIR/the_traveler.json" << 'EOF'
{
  "weight": 12,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.movement_speed", "min": 0.01, "max": 0.02 }
  ],
  "name_type": "suffix",
  "name_text": "the Traveler"
}
EOF

cat > "$OUTPUT_DIR/mobility.json" << 'EOF'
{
  "weight": 9,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.movement_speed", "min": 0.02, "max": 0.03 }
  ],
  "name_type": "suffix",
  "name_text": "Mobility"
}
EOF

cat > "$OUTPUT_DIR/the_explorer.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.movement_speed", "min": 0.015, "max": 0.025 },
    { "type": "mining_speed_mul", "min": 1.05, "max": 1.1 }
  ],
  "name_type": "suffix",
  "name_text": "the Explorer"
}
EOF

cat > "$OUTPUT_DIR/the_nomad.json" << 'EOF'
{
  "weight": 6,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.movement_speed", "min": 0.025, "max": 0.04 },
    { "type": "attr_add", "attribute": "minecraft:generic.max_health", "min": 1.0, "max": 2.0 }
  ],
  "name_type": "suffix",
  "name_text": "the Nomad"
}
EOF

cat > "$OUTPUT_DIR/the_wanderer.json" << 'EOF'
{
  "weight": 8,
  "applies_to": { "part_tags": ["tconstruct:handles"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.movement_speed", "min": 0.02, "max": 0.03 },
    { "type": "attr_add", "attribute": "minecraft:generic.luck", "min": 0.5, "max": 1.0 }
  ],
  "name_type": "suffix",
  "name_text": "the Wanderer"
}
EOF

# BINDINGS - Defense (15)
cat > "$OUTPUT_DIR/sturdy.json" << 'EOF'
{
  "weight": 15,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 0.5, "max": 1.0 }
  ],
  "name_type": "prefix",
  "name_text": "Sturdy"
}
EOF

cat > "$OUTPUT_DIR/reinforced.json" << 'EOF'
{
  "weight": 14,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 0.5, "max": 1.5 }
  ],
  "name_type": "prefix",
  "name_text": "Reinforced"
}
EOF

cat > "$OUTPUT_DIR/fortified.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 1.0, "max": 2.0 }
  ],
  "name_type": "prefix",
  "name_text": "Fortified"
}
EOF

cat > "$OUTPUT_DIR/hardened.json" << 'EOF'
{
  "weight": 11,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 0.5, "max": 1.5 },
    { "type": "attr_add", "attribute": "minecraft:generic.armor_toughness", "min": 0.5, "max": 1.0 }
  ],
  "name_type": "prefix",
  "name_text": "Hardened"
}
EOF

cat > "$OUTPUT_DIR/tempered.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 1.0, "max": 1.5 }
  ],
  "name_type": "prefix",
  "name_text": "Tempered"
}
EOF

cat > "$OUTPUT_DIR/ironclad.json" << 'EOF'
{
  "weight": 7,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 1.5, "max": 2.5 },
    { "type": "attr_add", "attribute": "minecraft:generic.armor_toughness", "min": 1.0, "max": 1.5 }
  ],
  "name_type": "prefix",
  "name_text": "Ironclad"
}
EOF

cat > "$OUTPUT_DIR/adamant.json" << 'EOF'
{
  "weight": 6,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 2.0, "max": 3.0 }
  ],
  "name_type": "prefix",
  "name_text": "Adamant"
}
EOF

cat > "$OUTPUT_DIR/unbreakable.json" << 'EOF'
{
  "weight": 3,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 2.5, "max": 4.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.armor_toughness", "min": 1.5, "max": 2.5 }
  ],
  "name_type": "prefix",
  "name_text": "Unbreakable"
}
EOF

cat > "$OUTPUT_DIR/guardian.json" << 'EOF'
{
  "weight": 9,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 1.0, "max": 1.5 },
    { "type": "attr_add", "attribute": "minecraft:generic.knockback_resistance", "min": 0.05, "max": 0.1 }
  ],
  "name_type": "prefix",
  "name_text": "Guardian"
}
EOF

cat > "$OUTPUT_DIR/defenders.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 0.5, "max": 1.5 }
  ],
  "name_type": "prefix",
  "name_text": "Defender's"
}
EOF

cat > "$OUTPUT_DIR/sentinels.json" << 'EOF'
{
  "weight": 7,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 1.5, "max": 2.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.knockback_resistance", "min": 0.1, "max": 0.15 }
  ],
  "name_type": "prefix",
  "name_text": "Sentinel's"
}
EOF

cat > "$OUTPUT_DIR/wardens.json" << 'EOF'
{
  "weight": 6,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 1.0, "max": 2.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.armor_toughness", "min": 1.0, "max": 2.0 }
  ],
  "name_type": "prefix",
  "name_text": "Warden's"
}
EOF

cat > "$OUTPUT_DIR/bulwark.json" << 'EOF'
{
  "weight": 5,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 2.0, "max": 3.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.knockback_resistance", "min": 0.1, "max": 0.2 }
  ],
  "name_type": "prefix",
  "name_text": "Bulwark"
}
EOF

cat > "$OUTPUT_DIR/juggernauts.json" << 'EOF'
{
  "weight": 3,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 3.0, "max": 4.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.armor_toughness", "min": 2.0, "max": 3.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.knockback_resistance", "min": 0.15, "max": 0.25 }
  ],
  "name_type": "prefix",
  "name_text": "Juggernaut's"
}
EOF

cat > "$OUTPUT_DIR/titan_forged.json" << 'EOF'
{
  "weight": 1,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 4.0, "max": 6.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.armor_toughness", "min": 3.0, "max": 4.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.max_health", "min": 2.0, "max": 4.0 }
  ],
  "name_type": "prefix",
  "name_text": "Titan Forged"
}
EOF

# BINDINGS - Utility (15)
cat > "$OUTPUT_DIR/lucky.json" << 'EOF'
{
  "weight": 13,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.luck", "min": 0.5, "max": 1.5 }
  ],
  "name_type": "suffix",
  "name_text": "Luck"
}
EOF

cat > "$OUTPUT_DIR/fortunate.json" << 'EOF'
{
  "weight": 12,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.luck", "min": 1.0, "max": 2.0 }
  ],
  "name_type": "suffix",
  "name_text": "Fortune"
}
EOF

cat > "$OUTPUT_DIR/serendipity.json" << 'EOF'
{
  "weight": 9,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.luck", "min": 1.5, "max": 3.0 }
  ],
  "name_type": "suffix",
  "name_text": "Serendipity"
}
EOF

cat > "$OUTPUT_DIR/the_gambler.json" << 'EOF'
{
  "weight": 6,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.luck", "min": 2.0, "max": 4.0 }
  ],
  "name_type": "suffix",
  "name_text": "the Gambler"
}
EOF

cat > "$OUTPUT_DIR/destiny.json" << 'EOF'
{
  "weight": 3,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.luck", "min": 3.0, "max": 5.0 }
  ],
  "name_type": "suffix",
  "name_text": "Destiny"
}
EOF

cat > "$OUTPUT_DIR/health.json" << 'EOF'
{
  "weight": 12,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.max_health", "min": 1.0, "max": 2.0 }
  ],
  "name_type": "suffix",
  "name_text": "Health"
}
EOF

cat > "$OUTPUT_DIR/vitality.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.max_health", "min": 2.0, "max": 4.0 }
  ],
  "name_type": "suffix",
  "name_text": "Vitality"
}
EOF

cat > "$OUTPUT_DIR/the_giant.json" << 'EOF'
{
  "weight": 6,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.max_health", "min": 3.0, "max": 6.0 }
  ],
  "name_type": "suffix",
  "name_text": "the Giant"
}
EOF

cat > "$OUTPUT_DIR/endurance.json" << 'EOF'
{
  "weight": 9,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.max_health", "min": 1.5, "max": 3.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 0.5, "max": 1.0 }
  ],
  "name_type": "suffix",
  "name_text": "Endurance"
}
EOF

cat > "$OUTPUT_DIR/resilience.json" << 'EOF'
{
  "weight": 10,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.max_health", "min": 1.0, "max": 2.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.knockback_resistance", "min": 0.05, "max": 0.1 }
  ],
  "name_type": "suffix",
  "name_text": "Resilience"
}
EOF

cat > "$OUTPUT_DIR/regeneration.json" << 'EOF'
{
  "weight": 5,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.max_health", "min": 2.0, "max": 4.0 }
  ],
  "name_type": "suffix",
  "name_text": "Regeneration"
}
EOF

cat > "$OUTPUT_DIR/the_phoenix.json" << 'EOF'
{
  "weight": 3,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.max_health", "min": 4.0, "max": 8.0 }
  ],
  "name_type": "suffix",
  "name_text": "the Phoenix"
}
EOF

cat > "$OUTPUT_DIR/the_dragon.json" << 'EOF'
{
  "weight": 2,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.max_health", "min": 3.0, "max": 6.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 1.0, "max": 2.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.luck", "min": 1.0, "max": 2.0 }
  ],
  "name_type": "suffix",
  "name_text": "the Dragon"
}
EOF

cat > "$OUTPUT_DIR/power.json" << 'EOF'
{
  "weight": 12,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.attack_damage", "min": 0.5, "max": 1.0 }
  ],
  "name_type": "suffix",
  "name_text": "Power"
}
EOF

cat > "$OUTPUT_DIR/the_colossus.json" << 'EOF'
{
  "weight": 1,
  "applies_to": { "part_tags": ["tconstruct:bindings"] },
  "effects": [
    { "type": "attr_add", "attribute": "minecraft:generic.max_health", "min": 6.0, "max": 10.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.armor", "min": 2.0, "max": 3.0 },
    { "type": "attr_add", "attribute": "minecraft:generic.knockback_resistance", "min": 0.2, "max": 0.3 }
  ],
  "name_type": "suffix",
  "name_text": "the Colossus"
}
EOF

echo "✅ Generated 100 affix JSON files!"
ls -1 "$OUTPUT_DIR" | wc -l
