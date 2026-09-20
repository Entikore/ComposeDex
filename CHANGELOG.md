# Changelog
All notable changes to ComposeDex will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Add `com.autonomousapps.dependency-analysis` Gradle plugin to monitor and analyze dependency health.

### Changed
- Migrate navigation layer to AndroidX Navigation3 library (`NavDisplay`, `entryProvider`, and type-safe destinations).
- Explicitly declare transitive dependencies in version catalog (`libs.versions.toml`) and module build scripts.
- Refactor domain UseCases to standard `BaseFetchUseCase` pattern.
- Improve UI screen state building and state management across screens.
- Replace `LaunchedEffect` with `DisposableEffect` for non-suspending handlers.
- Update project dependencies (Kotlin 2.4.20, AGP 9.4.1, Compose BOM 2026.09.00, Navigation3 1.1.7, Coil 3.6.3, Room 2.8.5, Retrofit 3.0.0).

### Fixed
- Asynchronous DAO queries no longer suspend indefinitely if table is empty.
- Navigation backstack state and animation transition issues in Navigation3 drawer host.
- Make complex Room database operations atomic using `@Transaction`.
- Resolve Detekt static analysis and linter findings across all modules.
- The theme is correctly updated when navigating back to a previously selected Pokémon.
- The selected Pokémon is reset when navigating from NavDrawer to PokemonScreen.
- PokemonScreenState.Loading is emitted on loading a selected Pokémon.

## [1.0.0] - 2024-12-30

### Added
- Initial release of the ComposeDex Android app. 