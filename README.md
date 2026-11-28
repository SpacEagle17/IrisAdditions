# <img src="https://cdn.modrinth.com/data/cached_images/35ed978665cb0469e0aa20d0c6667011358ad205.png" alt="IrisAdditions_logo" width="60" height="auto" /> &nbsp; IrisAdditions

<center>
<div style="display: flex; justify-content: center; flex-wrap: wrap; gap: 8px; text-align: center;">
    <a href="https://www.euphoriapatches.com/discord/" target="_blank">
        <img alt="Discord" src="https://img.shields.io/badge/Discord-050d18?style=for-the-badge&logo=discord&logoColor=black&labelColor=f6f6b3">
    </a>
    <a href="https://github.com/SpacEagle17/IrisAdditions" target="_blank">
        <img alt="Source" src="https://img.shields.io/badge/Source-050d18?style=for-the-badge&logo=github&logoColor=black&labelColor=f6f6b3">
    </a>
        <img alt="Iris Additions" src="https://img.shields.io/badge/Iris%20%7C%20Oculus%20%7C%20and%20derivatives-050d18?style=for-the-badge&logoColor=black&label=WORKS%20ON%3A&labelColor=050d18&color=f6f6b3">
    <a href="https://www.euphoriapatches.com/support/" target="_blank">
        <img alt="Support" src="https://img.shields.io/badge/Spaceagle17-050d18?style=for-the-badge&logo=patreon&logoColor=black&label=Support&labelColor=f6f6b3">
    </a>
</div>
<br>
</center>

## A mod that brings newer Iris features and bug fixes to older Minecraft versions, and adds some of its own.

## 🧩 Why use this mod?
Staying on an older Minecraft version usually means staying on an older Iris, without its newer features and bug fixes. IrisAdditions is made to close that gap.

## Features
- **No Setup Needed:** Works out of the box.
- **Loader Compatibility:** Available for Fabric, Forge, and NeoForge.
- **Version Support:** Minecraft 1.20.1, 1.21 until latest
- **Modular:** Every feature and bug fix can be turned off in `config/irisAdditionsSettings.toml`.

All features can be found in the [<ins>features list</ins>](https://github.com/SpacEagle17/IrisAdditions/blob/main/FEATURES.md).

## Bug Fixes
All bug fixes can be found in the [<ins>bug fix list</ins>](https://github.com/SpacEagle17/IrisAdditions/blob/main/FIXES.md).

## For Shader Devs
All shader defines can be found in the [<ins>define list</ins>](https://github.com/SpacEagle17/IrisAdditions/blob/main/DEFINES.md).

All backported uniforms can be found in the [<ins>uniform list</ins>](https://github.com/SpacEagle17/IrisAdditions/blob/main/UNIFORMS.md).

With IrisAdditions installed (`IS_IRIS_ADDITIONS`, see [<ins>defines</ins>](https://github.com/SpacEagle17/IrisAdditions/blob/main/DEFINES.md)), older Iris versions also allow **32 colortex's** and **SSBOs up to 12**, like newer Iris.

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

<details>
<summary>Click to expand for more details</summary>

This is mostly so I can remember XD

### Building
```bash
./gradlew                              # builds and tests everything into build/libs/
./gradlew ":1.21.11-fabric:build"      # builds a single version
./gradlew ":1.21.11-fabric:runClient"  # starts the game with Iris
./gradlew checkIrisUpdates             # fails if there are newer Iris or Oculus dependencies
```

### Layout
- `core/` plain Java with most of the logic, no Minecraft, Iris or loader imports
- `mixin/` hooks into Iris
- `uniforms/` backported shader uniforms
- `fixes/` fix stuff, yes obvious XD
- `loader/` Fabric, Forge and NeoForge code
- `versions/` dependencies of each build

### Version specific code
Stonecutter switches code per version or loader with comments, so it can look weird:
```java
//? if >=1.21.9 {
newCall();
//?} else {
/*oldCall();
*///?}
```
`src/` is committed as `26.3-fabric`, so run `Reset active project` before committing. Or just install the IntelliJ Stonecutter plugin lol, makes it much simpler

### Adding a feature or fix
1. Declare it in `Features.java` or `Fixes.java`, the existing entries show the options
2. Put its mixins in `mixin/features/<id>/` or `mixin/fixes/<id>/` and list them in `iris_additions.mixins.json` (version specific ones in `VersionedMixins.java`)
3. Document it in `FEATURES.md` or `FIXES.md`, and shader defines in `DEFINES.md`

### Adding a uniform
Add it to a class in `uniforms/`, register that in `ShaderUniforms.java` and document it in `UNIFORMS.md`. The define comes by itself

### Adding a Minecraft version
Add it to `settings.gradle.kts` and `stonecutter.properties.toml`, copy a close folder in `versions/`, then run `./gradlew buildAll` to see what breaks

</details>
