A fix turns itself off if your Iris version already has it.

| Fix | What it does | Minecraft | Credit |
|---|---|---|---|
| **Sky Performance** | Fixes a big FPS loss caused by how the sky horizon is drawn with shaders. | 1.21.5-1.21.11 | [Iris PR #2941](https://github.com/IrisShaders/Iris/pull/2941) by LovelyCane |
| **Hide GUI Menu Jumping** | Hiding the GUI after applying changes no longer jumps back to the main settings screen. | 1.20.1-26.3 | [Iris PR #3495](https://github.com/IrisShaders/Iris/pull/3495) by SpacEagle17, me yay! |
| **Hide GUI Button Fading Away** | The button which hides the GUI no longer disappears in the shader settings screen. | 1.21.2-1.21.5 | IrisAdditions, SpacEagle17 |
| **Centred Selected ShaderPack** | Opening the shaderpack selection list now shows the selected shaderpack in the middle of the screen, instead of scrolling away from it. | 1.20.1-26.2 | [Iris PR #3106](https://github.com/IrisShaders/Iris/pull/3106) by GeForceLegend |
| <a name="at-midblock-fix"></a>**at_midBlock at High Coordinates** | The shader attribute `at_midBlock` no longer breaks far away from the world origin. Shaders can check for the define [`IRIS_ADDITIONS_AT_MIDBLOCK_FIX`](DEFINES.md). | 1.20.1-26.2 | [Iris PR #3246](https://github.com/IrisShaders/Iris/pull/3246) by GeForceLegend |
