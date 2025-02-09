# Phantasm Game Engine

A modern, high-performance Java game engine built with LWJGL. Developed by Horrorcore Software.

## Features

- Modern OpenGL 3.3 Core Profile rendering
- Scene graph system with component-based architecture
- Editor interface with multiple viewports (Scene, Hierarchy, Inspector)
- Camera system with orbit controls
- Grid visualization for scene editing
- Cross-platform support (Windows, Linux, macOS)
- Window management and input handling

## Dependencies

- **Java 17** or higher
- **LWJGL 3.3.6** (OpenGL, GLFW bindings)
- **JOML 1.10.8** (Java OpenGL Math Library)

## Project Structure

com.horrorcore.engine
├── core
│ ├── graphics # Rendering systems (Camera, Shaders, Viewports)
│ ├── scene # Scene management and GameObject system
│ └── components # Component architecture (Transform, etc.)
└── editor # Editor implementation
└── viewports # Viewport management system

## Getting Started

1. Clone the repository
2. Build with Gradle:
```bash
./gradlew build
```
Run the editor
```java
public class Main {
public static void main(String[] args) {
Window window = new Window("Phantasm Engine Editor", 1280, 720);
window.init();

        // Main loop
        while (!window.shouldClose()) {
            window.update();
        }
        window.cleanup();
    }
}
```

## Key Components
### GameObject System

```java
GameObject player = new GameObject("Player");
player.addComponent(new Transform());
scene.addGameObject(player);
```

## Camera Controls
 - WASD: Movement

 - Right Click + Drag: Rotate view

 - Space/Shift: Vertical movement

 - Mouse Wheel: Zoom

## Development Roadmap
### Near-term Features
 - Physics system integration

 - Material system

 - Basic lighting system

 - Asset import pipeline

### Medium-term Features
 - Particle system

 - Animation system

 - Post-processing effects

 - Improved editor tools

### Long-term Features
 - Scripting system

 - Network support

 - Advanced profiling tools

 - VR support

## License
This project is licensed under the GNU General Public License v3.0.
© 2025 Horrorcore Software. All rights reserved.

For more details, see the LICENSE file.
