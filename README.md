# IrisAdditions

<center>
<div style="display: flex; justify-content: center; flex-wrap: wrap; gap: 8px; text-align: center;">
    <a href="https://www.euphoriapatches.com/discord/" target="_blank">
        <img alt="Discord" src="https://img.shields.io/badge/Discord-050d18?style=for-the-badge&logo=discord&logoColor=black&labelColor=e5f7ff">
    </a>
    <a href="https://github.com/SpacEagle17/IrisAdditions" target="_blank">
        <img alt="Source" src="https://img.shields.io/badge/Source-050d18?style=for-the-badge&logo=github&logoColor=black&labelColor=e5f7ff">
    </a>
        <img alt="Iris Additions" src="https://img.shields.io/badge/Iris%20%7C%20Oculus-050d18?style=for-the-badge&logoColor=black&label=WORKS%20ON%3A&labelColor=050d18&color=e5f7ff">
    <a href="https://www.euphoriapatches.com/support/" target="_blank">
        <img alt="Support" src="https://img.shields.io/badge/Spaceagle17-050d18?style=for-the-badge&logo=patreon&logoColor=black&label=Support&labelColor=e5f7ff">
    </a>
</div>
<br>
</center>

## A mod that brings newer Iris features and bug fixes to older Minecraft versions, and adds some of its own.

## 🔍 Why use this mod?
Staying on an older Minecraft version usually means staying on an older Iris, without its newer features and bug fixes. IrisAdditions is made to close that gap.

## Features
- **No Setup Needed:** Works out of the box.
- **Loader Compatibility:** Available for Fabric, Forge, and NeoForge.
- **Version Support:** Minecraft 1.20.1, 1.21.1, 1.21.11 and 26.1 to 26.3.
- **Modular:** Every feature and bug fix can be turned off in `config/irisAdditionsSettings.toml`.

All features can be found in the [<ins>features list</ins>](https://github.com/SpacEagle17/IrisAdditions/blob/main/FEATURES.md).

## Bug Fixes
All bug fixes can be found in the [<ins>bug fix list</ins>](https://github.com/SpacEagle17/IrisAdditions/blob/main/BUG_FIXES.md).

## Links
* **Support SpacEagle17 on [<ins>Patreon or Kofi</ins>](https://www.euphoriapatches.com/support/)**
* **Contact:** **https://euphoriapatches.com/contact**
* **Discord:** **https://www.euphoriapatches.com/discord**

## Sponsored by Ember Host
Looking for an affordable and reliable Minecraft server host? Ember Host offers incredible value and top-tier support!

Click the banner or use code **[EUPHORIA](https://www.euphoriapatches.com/ember-referral)** at checkout for **10%** off your first purchase. Plus, **70%** of your purchase supports Euphoria Patches!

<a href="https://www.euphoriapatches.com/ember-referral"><img src=https://www.euphoriapatches.com/assets/img/other/ember_host_ep.png></a>

## For Developers
One source folder builds every version and loader using [Stonecutter](https://stonecutter.kikugie.dev/).

### Building
- `./gradlew buildAll` (or just `./gradlew`) builds and tests everything. The jars and `IrisAdditionsMod_UNZIP_ME.zip` end up in `build/libs/`
- `./gradlew ":1.21.1-fabric:build"` builds a single version
- `./gradlew ":1.21.1-fabric:runClient"` starts the game with Iris, using the folder `run/1.21.1-fabric/`
- `./gradlew ":1.21.1-fabric:runProductionClient"` (Fabric only) starts the game with the finished jar, like a player would. Use this one on 1.21.1 Fabric, where `runClient` crashes

Gradle needs Java 25, any missing Java version is downloaded automatically.

### Project layout
```
src/main/java/.../irisadditions/
  core/      plain Java, most of the logic goes here. No Minecraft, Iris or loader imports
  mixin/     mixins which pass values to core/
  loader/    Fabric, Forge and NeoForge specific code
stonecutter.properties.toml   mod info, Java and release versions per Minecraft version
settings.gradle.kts           list of all Minecraft versions and loaders
build.<loader>.gradle.kts     loader specific build setup
buildSrc/                     build setup shared by all loaders
versions/<version>-<loader>/  Iris, loader and library versions of one build, plus its build output
```

### Version specific code
```java
//? if >=1.21.9 {
newCall();
//?} else {
/*oldCall();
*///?}
```

`src/` is committed as `26.3-fabric`. To work on another version, run its `Set active project to ...` task, and run `Reset active project` before committing. Or just install the intellij stonecutter plugin lol, makes it much simpler

### Adding a feature or fix
1. Declare it in `core/feature/Features.java` or `core/feature/Fixes.java`, which also creates its config option. If Iris has it itself from some version on, add `.includedSince(Mods.IRIS, "x.y.z")` (`Mods.OCULUS` for Forge), so it is skipped there
2. Write the logic in `core/` where possible and check `isEnabled()`
3. Put its mixins in `mixin/features/<id>/` or `mixin/fixes/<id>/`
4. List mixins for all versions in `iris_additions.mixins.json`, version specific ones in `mixin/VersionedMixins.java`

`Mods.isModLoaded("modid")` and `Mods.isEuphoriaLoaded()` can be used anywhere.

### Adding a Minecraft version
1. Add it in `settings.gradle.kts`
2. Add its Java and release versions to `stonecutter.properties.toml`
3. Create `versions/<version>-<loader>/stonecutter.properties.toml` with the dependencies, copy a close version as a start
4. Run `./gradlew buildAll`, the errors and tests show what needs version specific code
