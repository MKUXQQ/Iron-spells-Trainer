# Poison Circle Entity Implementation Plan

**Goal:** Render the poison boundary as a non-colliding server entity rather than a client camera-stage overlay.

**Architecture:** A `PoisonCircleVisualEntity` carries center/radius state from the server. The poison-circle controller creates, updates, and discards it. A dedicated client renderer draws the existing animated cylindrical wall at the entity's actual world position.

### Tasks

1. Register a non-colliding visual entity and verify its state model with a unit test.
2. Create/update/discard one visual entity per active dimension.
3. Register an entity renderer and remove the old `RenderLevelStageEvent` boundary drawing.
4. Build the mod and replace the installed Jar.
