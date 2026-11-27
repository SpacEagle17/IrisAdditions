Uniforms of newer Iris versions which IrisAdditions adds to older ones.

| Uniform | Define | Credit |
|---|---|---|
| `bool heavyFog` | `IRIS_ADDITIONS_UNIFORM_HEAVY_FOG` | [Iris commit 052faeb](https://github.com/IrisShaders/Iris/commit/052faeb4c4382cc4d186ced14ec7a160615801e3) by IMS212 |
| `bool feetInWater` | `IRIS_ADDITIONS_UNIFORM_FEET_IN_WATER` | [Iris PR #2951](https://github.com/IrisShaders/Iris/pull/2951) by Merlin1809 |
| `bool inSwimmingAnimation` | `IRIS_ADDITIONS_UNIFORM_IN_SWIMMING_ANIMATION` | [Iris PR #2951](https://github.com/IrisShaders/Iris/pull/2951) by Merlin1809 |
| `bool isElytraFlying` | `IRIS_ADDITIONS_UNIFORM_IS_ELYTRA_FLYING` | [Iris PR #2951](https://github.com/IrisShaders/Iris/pull/2951) by Merlin1809 |
| `bool isRiding` | `IRIS_ADDITIONS_UNIFORM_IS_RIDING` | [Iris PR #2951](https://github.com/IrisShaders/Iris/pull/2951) by Merlin1809 |
| `bool vehicleInWater` | `IRIS_ADDITIONS_UNIFORM_VEHICLE_IN_WATER` | [Iris PR #2951](https://github.com/IrisShaders/Iris/pull/2951) by Merlin1809 |
| `int vehicleId` | `IRIS_ADDITIONS_UNIFORM_VEHICLE_ID` | [Iris PR #2951](https://github.com/IrisShaders/Iris/pull/2951) by Merlin1809 |
| `vec3 vehicleLookVector` | `IRIS_ADDITIONS_UNIFORM_VEHICLE_LOOK_VECTOR` | [Iris PR #2951](https://github.com/IrisShaders/Iris/pull/2951) by Merlin1809 |
| `vec3 relativeVehiclePosition` | `IRIS_ADDITIONS_UNIFORM_RELATIVE_VEHICLE_POSITION` | [Iris PR #2951](https://github.com/IrisShaders/Iris/pull/2951) by Merlin1809 |
| `float glintAlpha` | `IRIS_ADDITIONS_UNIFORM_GLINT_ALPHA` | [Iris commit 0a70868](https://github.com/IrisShaders/Iris/commit/0a708682a00cf009dba383945670dcda979de7ae) by IMS212 |
| `int gtextureId` | `IRIS_ADDITIONS_UNIFORM_GTEXTURE_ID` | [Iris PR #3028](https://github.com/IrisShaders/Iris/pull/3028) by xirreal |
| `int textureReloadCount` | `IRIS_ADDITIONS_UNIFORM_TEXTURE_RELOAD_COUNT` | [Iris PR #3028](https://github.com/IrisShaders/Iris/pull/3028) by xirreal |
| `int seaLevel` | `IRIS_ADDITIONS_UNIFORM_SEA_LEVEL` | [Iris commit c9926be](https://github.com/IrisShaders/Iris/commit/c9926beb6d3471f64104287f35186ce0fdbc3674) by IMS212 |
| `int currentSelectedBlockId` | `IRIS_ADDITIONS_UNIFORM_CURRENT_SELECTED_BLOCK_ID` | [Iris commit bc4b8c4](https://github.com/IrisShaders/Iris/commit/bc4b8c4634f2aed4e08fce87cae16ab772257938) by IMS212 |
| `vec3 currentSelectedBlockPos` | `IRIS_ADDITIONS_UNIFORM_CURRENT_SELECTED_BLOCK_POS` | [Iris commit bc4b8c4](https://github.com/IrisShaders/Iris/commit/bc4b8c4634f2aed4e08fce87cae16ab772257938) by IMS212 |
| `float constantMood` | `IRIS_ADDITIONS_UNIFORM_CONSTANT_MOOD` | [Iris commit e9c0031](https://github.com/IrisShaders/Iris/commit/e9c00310ab644cfc88928502807cbb77632140b3) by IMS212 |

With IrisAdditions all of these work on every supported version, except `seaLevel`, which needs 1.21.2 or newer.

The define is only auto added where iris itself does not add the uniform
