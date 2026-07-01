# Portable Inscription Table Design

## Goal

Build a Minecraft 1.21.1 NeoForge addon that lets a player press `G` anywhere to open Iron's Spells 'n Spellbooks' inscription table UI and inscribe scrolls into spell books.

## Confirmed Behavior

- Pressing `G` opens the inscription table interface.
- The feature has no world-position, dimension, combat, block, or inventory requirement.
- The mod does not add a block or item.
- The mod does not modify Iron's Spells source code.
- The client and server must both have this addon installed.
- Iron's Spells must be installed because the addon reuses its menu and spell data logic.

## Architecture

The addon registers one client key mapping for `G`. When the key is pressed, the client sends a small custom packet to the server.

The server handles the packet by opening Iron's Spells' `InscriptionTableMenu` with `ContainerLevelAccess.NULL`. Iron's Spells already uses this pattern in its own command path, so no fake block position is needed. The existing menu continues to handle slots, scroll consumption, spell book updates, and the normal inscription button behavior.

## Components

- Main mod class: registers shared setup and network payload handlers.
- Client input class: registers the key mapping and sends the open request when `G` is pressed.
- Network payload class: represents the client-to-server open request.
- Server handler: opens a `SimpleMenuProvider` that creates `InscriptionTableMenu`.
- Mod metadata: declares a required dependency on `irons_spellbooks`.

## Error Handling

The addon depends on Iron's Spells at load time, so the game should refuse to load without it instead of failing at key press time. The server handler does not perform extra gameplay validation because the selected requirement is unrestricted access.

## Testing

Automated tests will cover the small local logic that identifies the key action request path where practical. Build verification will compile the mod against Minecraft 1.21.1 NeoForge and Iron's Spells dependencies.

Manual in-game verification:

1. Start a client with Iron's Spells and this addon.
2. Join a world or local server with both mods installed.
3. Press `G`.
4. Confirm the inscription table UI opens without a nearby table.
5. Put a spell book and scroll in the UI.
6. Click the inscription button and confirm the scroll is consumed and the spell is added to the book.
