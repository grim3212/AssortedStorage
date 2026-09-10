# AssortedStorage — in-world testing checklist

Manual checks only. Anything automatable lives in `common/src/main/java/.../gametest/`
and runs with `./gradlew :neoforge:runGameTestServer` / `:fabric:runGameTest`.

Run each list on **both** NeoForge and Fabric.

## Bags
- [ ] Bag opens
- [ ] A dyed bag shows its colour in hand and in the inventory
- [ ] Locked bag shows its padlock on the model
- [ ] Ender bag shares one inventory with every bag/chest on the same lock code
- [ ] Contents survive a death/respawn

## Locks and keys
- [ ] None of the 14 locked doors draw solid
- [ ] Locked barrel, hopper and crate controller show their padlocked face

## Storage blocks
- [ ] Locker, double locker, wood cabinet, glass cabinet, gold safe, obsidian safe and item tower
      each open
- [ ] Their item forms render as the block in hand, inventory and on the ground
- [ ] Warehouse crates (every wood) open, and show their contents on the front
- [ ] Locked chest, ender chest and shulker box open and render correctly

## Crates
- [ ] Crates placed next to each other join into one inventory
- [ ] Crate controller opens the whole network
- [ ] Crate bridge links two separate groups
- [ ] Crate compacting block compacts items up the tiers (nugget → ingot → block)
- [ ] Amount, glow, redstone, void, level and blank upgrades all show on the crate

## Misc
- [ ] Curios slot accepts a bag (NeoForge, with Curios installed)
