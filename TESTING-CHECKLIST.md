# AssortedStorage — in-world testing checklist

Manual checks only. Anything automatable lives in `common/src/main/java/.../gametest/`
and runs with `./gradlew :neoforge:runGameTestServer` / `:fabric:runGameTest`.

Run each list on **both** NeoForge and Fabric.

## Bags
- [ ] Bag opens and holds items
- [ ] Bag can be dyed, and the held/inventory model shows the colour
- [ ] Locked bag shows its padlock on the model
- [ ] Ender bag shares one inventory with every bag/chest on the same lock code
- [ ] An uncoded ender bag opens the player's own vanilla ender chest
- [ ] Key ring holds keys and hands the right one to a lock
- [ ] Contents survive a world reload and a death/respawn

## Locks and keys
- [ ] Locksmith workbench crafts a key and a lock with a matching code
- [ ] Locking a container with the lock sets the code
- [ ] Right key (in hand or on a key ring) opens it; wrong key does not
- [ ] Typing the code by hand opens it
- [ ] All 14 locked doors open only with the right key, and none of them draw solid
- [ ] Locked barrel, hopper and crate controller show their padlocked face

## Storage blocks
- [ ] Locker, double locker, wood cabinet, glass cabinet, gold safe, obsidian safe and item tower
      each open and hold items
- [ ] Their item forms render as the block in hand, inventory and on the ground
- [ ] Warehouse crates (every wood) open, and show their contents on the front
- [ ] Locked chest, ender chest and shulker box open and render correctly
- [ ] Breaking any of them drops the contents
- [ ] Contents survive a world reload

## Crates
- [ ] Crates placed next to each other join into one inventory
- [ ] Crate controller opens the whole network
- [ ] Crate bridge links two separate groups
- [ ] Crate compacting block compacts items up the tiers (nugget → ingot → block)
- [ ] Amount, glow, redstone, void, level and blank upgrades all apply and show on the crate
- [ ] Void upgrade destroys overflow; redstone upgrade emits a signal

## Misc
- [ ] Rotator majig rotates a block it is used on
- [ ] Curios slot accepts a bag (NeoForge, with Curios installed)

## Creative
- [ ] The Assorted Storage tab exists and every block/item in it has a model and a name
