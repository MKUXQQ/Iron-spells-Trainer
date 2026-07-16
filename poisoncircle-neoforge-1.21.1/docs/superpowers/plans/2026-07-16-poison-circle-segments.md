# Poison Circle Segment Wall Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Render the poison boundary as collision-free wall-segment entities fixed around the world-space circle.

**Architecture:** A pure layout helper produces evenly spaced segment positions on the current circle. The server owns one visual entity per segment and updates the position of every segment when the circle moves or shrinks. Each entity renderer only draws one narrow vertical wall, so there is no camera-relative full-cylinder draw.

**Tech Stack:** Java 21, Minecraft 1.21.1, NeoForge 21.1.x, JUnit 5.

## Global Constraints

- Keep the existing poison damage, red flash, map and detector systems unchanged.
- Visual entities must have no collision and must be independently located in world space.
- Do not use `RenderLevelStageEvent` for the poison boundary.

---

### Task 1: Circle segment layout

**Files:**
- Create: `src/main/java/com/poisoncircle/PoisonCircleSegmentLayout.java`
- Modify: `src/test/java/com/poisoncircle/PoisonCircleVisualStateTest.java`

- [ ] **Step 1: Write the failing test**

```java
@Test
void segmentLayoutPlacesEverySegmentOnTheWorldCircle() {
    var positions = PoisonCircleSegmentLayout.positions(100, -50, 80, 16);
    assertEquals(16, positions.size());
    assertEquals(80, Math.hypot(positions.get(0).x() - 100, positions.get(0).z() + 50), 0.001);
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew.bat test --tests com.poisoncircle.PoisonCircleVisualStateTest`

- [ ] **Step 3: Implement the layout**

```java
static List<Position> positions(double centerX, double centerZ, double radius, int count) {
    return IntStream.range(0, count).mapToObj(index -> {
        double angle = Math.PI * 2 * index / count;
        return new Position(centerX + Math.cos(angle) * radius, centerZ + Math.sin(angle) * radius, angle);
    }).toList();
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew.bat test --tests com.poisoncircle.PoisonCircleVisualStateTest`

### Task 2: Server-owned wall segments

**Files:**
- Modify: `src/main/java/com/poisoncircle/PoisonCircleVisualEntity.java`
- Modify: `src/main/java/com/poisoncircle/PoisonCircleVisualRenderer.java`
- Modify: `src/main/java/com/poisoncircle/PoisonCircleMod.java`

- [ ] **Step 1: Replace the one central visual with segment entities**

Create 96 `PoisonCircleVisualEntity` instances per active world. Update each from `PoisonCircleSegmentLayout.positions`; discard all entities when the circle stops or collapses.

- [ ] **Step 2: Render a narrow vertical wall per entity**

Use the segment yaw and a fixed world-space width for one vertical red energy wall. Do not draw the complete circle from one entity.

- [ ] **Step 3: Verify full build**

Run: `./gradlew.bat clean build`

