# Phantasm Game Engine

A modern, high-performance Java game engine built with LWJGL. Developed by Horrorcore Software.

## Features

- Modern OpenGL rendering pipeline
- Scene graph system with component-based architecture
- Event-driven input system supporting keyboard, mouse, and gamepads
- Resource management with automatic reference counting
- Configuration system with JSON support
- Comprehensive logging and debugging facilities
- Cross-platform support (Windows, Linux, macOS)

## Dependencies

- Java 17 or higher
- LWJGL 3.3.1
- JOML 1.10.5 (Java OpenGL Math Library)
- JBullet Physics Engine
- libGDX Scene2D (UI Framework)
- Gson 2.10.1 (JSON parsing)

## Project Structure

```
com.horrorcoresoftware
├── core            # Core engine systems
│   ├── config     # Configuration management
│   ├── graphics   # Rendering and graphics
│   ├── input      # Input handling
│   ├── resource   # Resource management
│   ├── scene      # Scene graph
│   └── events     # Event system
└── game           # Game-specific implementations
```

## Usage Example

```java
public class MyGame {
    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.initialize();
        
        Scene scene = new Scene();
        GameObject player = new GameObject("player");
        scene.addGameObject(player);
        
        engine.setScene(scene);
        engine.start();
    }
}
```

# Development Roadmap

## Near-term Features
- Physics system integration with JBullet
- Audio system with OpenAL
- Material system with PBR support
- Particle system
- Animation system with skeletal animation support

## Medium-term Features
- Editor tools and GUI
- Scripting system
- Asset pipeline and packaging
- Network multiplayer support
- Post-processing effects
- Dynamic shadows

## Long-term Features
- Ray tracing support
- Advanced AI systems
- Visual scripting
- Performance profiling tools
- Level streaming
- Advanced physics simulation