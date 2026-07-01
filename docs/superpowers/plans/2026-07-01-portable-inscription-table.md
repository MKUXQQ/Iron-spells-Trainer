# Portable Inscription Table Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a NeoForge 1.21.1 addon that opens Iron's Spells' inscription table UI with the `G` key from anywhere.

**Architecture:** The client registers a `G` key mapping and sends a custom payload to the server. The server opens Iron's Spells' existing `InscriptionTableMenu` with `ContainerLevelAccess.NULL`, preserving the original inscription behavior while removing the table-position requirement.

**Tech Stack:** Java 21, Minecraft 1.21.1, NeoForge 21.1.200, Gradle, Iron's Spells 'n Spellbooks 1.21.1 APIs/classes.

---

## File Structure

- `settings.gradle`: Gradle project name.
- `build.gradle`: NeoForge build, repositories, dependencies, and resource expansion.
- `gradle.properties`: Minecraft, NeoForge, Iron's Spells, and mod metadata versions.
- `src/main/resources/META-INF/neoforge.mods.toml`: mod metadata and required `irons_spellbooks` dependency.
- `src/main/resources/pack.mcmeta`: resource pack metadata.
- `src/main/resources/assets/portable_inscription_table/lang/en_us.json`: keybinding text.
- `src/main/java/com/example/portableinscriptiontable/PortableInscriptionTable.java`: main mod class and network registration.
- `src/main/java/com/example/portableinscriptiontable/client/PortableInscriptionClient.java`: key mapping and client tick handling.
- `src/main/java/com/example/portableinscriptiontable/network/OpenInscriptionTablePayload.java`: custom payload definition.
- `src/main/java/com/example/portableinscriptiontable/network/OpenInscriptionTableHandler.java`: server-side open-menu action.
- `src/test/java/com/example/portableinscriptiontable/network/OpenInscriptionTablePayloadTest.java`: payload identity test.

### Task 1: Scaffold Build And Metadata

**Files:**
- Create: `settings.gradle`
- Create: `build.gradle`
- Create: `gradle.properties`
- Create: `src/main/resources/META-INF/neoforge.mods.toml`
- Create: `src/main/resources/pack.mcmeta`

- [ ] **Step 1: Write metadata files**

Create the Gradle and resource metadata using the versions from Iron's Spells 1.21 branch:

```properties
# gradle.properties
org.gradle.jvmargs=-Xmx3G
org.gradle.daemon=false

minecraft_version=1.21.1
minecraft_version_range=[1.21.1,1.21.2)
neo_version=21.1.200
neo_version_range=[21.0.0-beta,)
loader_version_range=[4,)

mod_id=portable_inscription_table
mod_name=Portable Inscription Table
mod_license=MIT
mod_version=1.0.0
mod_group_id=com.example.portableinscriptiontable
mod_authors=MKUXQ
mod_description=Opens Iron's Spells inscription table anywhere with a keybind.

irons_spellbooks_version=1.21.1-3.16.2
```

```groovy
// settings.gradle
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven { url = 'https://maven.neoforged.net/releases' }
    }
}

plugins {
    id 'org.gradle.toolchains.foojay-resolver-convention' version '0.8.0'
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        maven { url = 'https://maven.neoforged.net/releases' }
        maven { url = 'https://maven.theillusivec4.top' }
        maven { url = 'https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/' }
        maven { url = 'https://maven.kosmx.dev/' }
        maven { url = 'https://code.redspace.io/releases' }
        maven { url = 'https://code.redspace.io/snapshots' }
        maven { url = 'https://cursemaven.com' }
        mavenCentral()
    }
}

rootProject.name = 'portable-inscription-table'
```

```groovy
// build.gradle
plugins {
    id 'java-library'
    id 'net.neoforged.gradle.userdev' version '7.0.182'
}

version = mod_version
group = mod_group_id

base {
    archivesName = mod_id
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

runs {
    configureEach {
        systemProperty 'forge.logging.markers', 'REGISTRIES'
        systemProperty 'forge.logging.console.level', 'debug'
        modSource project.sourceSets.main
    }

    client {}
    server {
        programArgument '--nogui'
    }
}

dependencies {
    implementation "net.neoforged:neoforge:${neo_version}"
    implementation "io.redspace:irons_spellbooks:${irons_spellbooks_version}"
}

tasks.withType(ProcessResources).configureEach {
    var replaceProperties = [
            minecraft_version      : minecraft_version,
            minecraft_version_range: minecraft_version_range,
            neo_version            : neo_version,
            neo_version_range      : neo_version_range,
            loader_version_range   : loader_version_range,
            mod_id                 : mod_id,
            mod_name               : mod_name,
            mod_license            : mod_license,
            mod_version            : mod_version,
            mod_authors            : mod_authors,
            mod_description        : mod_description
    ]
    inputs.properties replaceProperties
    filesMatching(['META-INF/neoforge.mods.toml']) {
        expand replaceProperties
    }
}

tasks.withType(JavaCompile).configureEach {
    options.encoding = 'UTF-8'
}
```

```toml
# src/main/resources/META-INF/neoforge.mods.toml
modLoader="javafml"
loaderVersion="${loader_version_range}"
license="${mod_license}"

[[mods]]
modId="${mod_id}"
version="${mod_version}"
displayName="${mod_name}"
authors="${mod_authors}"
description='''${mod_description}'''

[[dependencies.${mod_id}]]
modId="neoforge"
type="required"
versionRange="${neo_version_range}"
ordering="NONE"
side="BOTH"

[[dependencies.${mod_id}]]
modId="minecraft"
type="required"
versionRange="${minecraft_version_range}"
ordering="NONE"
side="BOTH"

[[dependencies.${mod_id}]]
modId="irons_spellbooks"
type="required"
ordering="AFTER"
side="BOTH"
```

```json
{
  "pack": {
    "description": "Portable Inscription Table resources",
    "pack_format": 34
  }
}
```

- [ ] **Step 2: Verify Gradle can list tasks**

Run: `.\gradlew.bat tasks`

Expected: Gradle downloads NeoForge tooling and lists project tasks.

- [ ] **Step 3: Commit**

Run:

```bash
git add settings.gradle build.gradle gradle.properties src/main/resources/META-INF/neoforge.mods.toml src/main/resources/pack.mcmeta
git commit -m "chore: scaffold portable inscription addon"
```

### Task 2: Add Network Payload

**Files:**
- Create: `src/main/java/com/example/portableinscriptiontable/network/OpenInscriptionTablePayload.java`
- Create: `src/test/java/com/example/portableinscriptiontable/network/OpenInscriptionTablePayloadTest.java`

- [ ] **Step 1: Write failing payload test**

```java
package com.example.portableinscriptiontable.network;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenInscriptionTablePayloadTest {
    @Test
    void payloadTypeUsesModNamespace() {
        ResourceLocation id = OpenInscriptionTablePayload.TYPE.id();

        assertEquals("portable_inscription_table", id.getNamespace());
        assertEquals("open_inscription_table", id.getPath());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\gradlew.bat test --tests com.example.portableinscriptiontable.network.OpenInscriptionTablePayloadTest`

Expected: FAIL because `OpenInscriptionTablePayload` does not exist.

- [ ] **Step 3: Implement payload**

```java
package com.example.portableinscriptiontable.network;

import com.example.portableinscriptiontable.PortableInscriptionTable;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenInscriptionTablePayload() implements CustomPacketPayload {
    public static final Type<OpenInscriptionTablePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(PortableInscriptionTable.MOD_ID, "open_inscription_table")
    );

    public static final StreamCodec<ByteBuf, OpenInscriptionTablePayload> STREAM_CODEC = StreamCodec.unit(new OpenInscriptionTablePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `.\gradlew.bat test --tests com.example.portableinscriptiontable.network.OpenInscriptionTablePayloadTest`

Expected: PASS.

- [ ] **Step 5: Commit**

Run:

```bash
git add src/main/java/com/example/portableinscriptiontable/network/OpenInscriptionTablePayload.java src/test/java/com/example/portableinscriptiontable/network/OpenInscriptionTablePayloadTest.java
git commit -m "test: define open inscription payload"
```

### Task 3: Register Mod And Server Handler

**Files:**
- Create: `src/main/java/com/example/portableinscriptiontable/PortableInscriptionTable.java`
- Create: `src/main/java/com/example/portableinscriptiontable/network/OpenInscriptionTableHandler.java`

- [ ] **Step 1: Create main mod and handler**

```java
package com.example.portableinscriptiontable;

import com.example.portableinscriptiontable.network.OpenInscriptionTableHandler;
import com.example.portableinscriptiontable.network.OpenInscriptionTablePayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(PortableInscriptionTable.MOD_ID)
public class PortableInscriptionTable {
    public static final String MOD_ID = "portable_inscription_table";

    public PortableInscriptionTable(IEventBus modEventBus) {
        modEventBus.addListener(this::registerPayloads);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MOD_ID).versioned("1.0.0");
        registrar.playToServer(
                OpenInscriptionTablePayload.TYPE,
                OpenInscriptionTablePayload.STREAM_CODEC,
                OpenInscriptionTableHandler::handle
        );
    }
}
```

```java
package com.example.portableinscriptiontable.network;

import io.redspace.ironsspellbooks.gui.inscription_table.InscriptionTableMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class OpenInscriptionTableHandler {
    private OpenInscriptionTableHandler() {
    }

    public static void handle(OpenInscriptionTablePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> context.player().openMenu(new SimpleMenuProvider(
                (containerId, inventory, player) -> new InscriptionTableMenu(containerId, inventory, ContainerLevelAccess.NULL),
                Component.translatable("block.irons_spellbooks.inscription_table")
        )));
    }
}
```

- [ ] **Step 2: Compile**

Run: `.\gradlew.bat compileJava`

Expected: PASS, confirming the handler can reference Iron's Spells classes.

- [ ] **Step 3: Commit**

Run:

```bash
git add src/main/java/com/example/portableinscriptiontable/PortableInscriptionTable.java src/main/java/com/example/portableinscriptiontable/network/OpenInscriptionTableHandler.java
git commit -m "feat: open inscription table on server request"
```

### Task 4: Register Client Keybind

**Files:**
- Create: `src/main/java/com/example/portableinscriptiontable/client/PortableInscriptionClient.java`
- Create: `src/main/resources/assets/portable_inscription_table/lang/en_us.json`

- [ ] **Step 1: Implement client key handling**

```java
package com.example.portableinscriptiontable.client;

import com.example.portableinscriptiontable.PortableInscriptionTable;
import com.example.portableinscriptiontable.network.OpenInscriptionTablePayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = PortableInscriptionTable.MOD_ID, value = Dist.CLIENT)
public final class PortableInscriptionClient {
    private static final KeyMapping OPEN_INSCRIPTION_TABLE = new KeyMapping(
            "key.portable_inscription_table.open_inscription_table",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.portable_inscription_table"
    );

    private PortableInscriptionClient() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_INSCRIPTION_TABLE);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (OPEN_INSCRIPTION_TABLE.consumeClick()) {
            PacketDistributor.sendToServer(new OpenInscriptionTablePayload());
        }
    }
}
```

```json
{
  "key.categories.portable_inscription_table": "Portable Inscription Table",
  "key.portable_inscription_table.open_inscription_table": "Open Inscription Table"
}
```

- [ ] **Step 2: Compile**

Run: `.\gradlew.bat compileJava`

Expected: PASS.

- [ ] **Step 3: Commit**

Run:

```bash
git add src/main/java/com/example/portableinscriptiontable/client/PortableInscriptionClient.java src/main/resources/assets/portable_inscription_table/lang/en_us.json
git commit -m "feat: add inscription table keybind"
```

### Task 5: Verify Build

**Files:**
- Modify only if build errors reveal an API mismatch.

- [ ] **Step 1: Run full build**

Run: `.\gradlew.bat build`

Expected: PASS and a jar under `build/libs/`.

- [ ] **Step 2: Manual game check**

Run: `.\gradlew.bat runClient`

Expected:
- Client launches.
- In Controls, `G` is bound to "Open Inscription Table".
- In a world, pressing `G` opens the Iron's Spells inscription table UI.

- [ ] **Step 3: Commit verification fixes if needed**

If no fixes were needed, do not create an empty commit. If fixes were needed:

```bash
git add <changed-files>
git commit -m "fix: align with neoforge client api"
```
