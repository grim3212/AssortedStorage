# AssortedStorage — in-world testing checklist

Manual checks only. Anything automatable lives in `common/src/gametest/java/.../gametest/`
and runs with `./gradlew :neoforge:runGameTestServer` / `:fabric:runGameTest`.

Run each list on **both** NeoForge and Fabric.

## Bags
- [ ] Bag opens
- [ ] A dyed bag shows its colour in hand and in the inventory
- [ ] Locked bag shows its padlock on the model
- [ ] Ender bag shares one inventory with every bag/chest on the same lock code
- [ ] Contents survive a death/respawn
- [ ] Bag, ender bag and key ring screens show their slots (NeoForge in particular)
- [ ] A bag works in a container slot

## Locks and keys
- [ ] None of the 14 locked doors draw solid
- [ ] Locked barrel, hopper and crate controller show their padlocked face
- [ ] A locked barrel item shows its padlock, and an unlocked one does not
- [ ] The key ring opens a locked door
- [ ] A locked hopper respects its lock
- [ ] Breaking a locked chest, crate, locker and door drops the contents once and the padlock once
- [ ] A hopper or pipe pulls from a locked chest, crate and locker
- [ ] On a dedicated server, a barrel, hopper or crate controller locked by one player shows its
      padlock to a second player

## Storage blocks
- [ ] Locker, double locker, wood cabinet, glass cabinet, gold safe, obsidian safe and item tower
      each open
- [ ] Their item forms render as the block in hand, inventory and on the ground
- [ ] Warehouse crates (every wood) open, and show their contents on the front
- [ ] Locked chest, ender chest and shulker box open and render correctly
- [ ] Item tower shelf offsets and part-picking models look right
- [ ] A locked shulker box item shows its colour

## Crates
- [ ] Crates placed next to each other join into one inventory
- [ ] Crate controller opens the whole network
- [ ] Crate bridge links two separate groups
- [ ] Crate compacting block compacts items up the tiers (nugget → ingot → block)
- [ ] Amount, glow, redstone, void, level and blank upgrades all show on the crate
- [ ] The crate screen's block preview is posed right

## Misc
- [ ] Curios slot accepts a bag (NeoForge, with Curios installed)
- [ ] Curios key slot works (NeoForge, with Curios installed; it compiles but has never been run)

## Rendering
- [ ] Nothing a renderer draws is subtly off. Sprite UVs now take 0-1 and ARGB colours no longer
      force alpha, so a mistake shows as the wrong texture region or a missing tint, not a crash
