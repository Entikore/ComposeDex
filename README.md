# ComposeDex

![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg) ![Build Status](https://github.com/entikore/composedex/workflows/Android%20CI/badge.svg) ![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)

ComposeDex is an learning app created by modern Android development methods that showcases the 
Android Guide to App Architecture in a real world example. Explore the world of Pokémon by searching
through all generations and learning details about each Pokémon and its evolutions. Learn about
their types and variations and listen to their cries or Pokédex entries. Save your favorites for
quick access. ComposeDex is perfect for Pokémon fans and those interested in modern Android
development.

## Table of Contents

 - [Getting Started](#getting-started)
 - [Preview](#preview)
 - [Architecture](#architecture)
 - [Dependencies](#dependencies)
 - [PokeAPI](#pokéapi)
 - [Contributing](#contributing)
 - [License](#license)
 - [Changelog](#changelog)

## Getting Started

To try out ComposeDex, use the latest stable version of [Android Studio](https://developer.android.com/studio).
You can clone this repository or import the project from Android Studio following the steps [here](https://developer.android.com/jetpack/compose/setup#sample).

### Compatibility

- **Minimum SDK**: 24
- **Target SDK**: 35
- **Orientations Supported**: Portrait

## Preview

<table style="width:100%">
  <tr>
    <th>Navigation</th>
    <th>Pokémon Screen</th>
  </tr>
  <tr>
    <td><img src="preview/preview_navigation.gif" width="50%" height="50%"></td>
    <td><img src="preview/preview_detail.gif" width="50%" height="50%"></td>
  </tr>
  <tr>
    <th>Generations Screen</th>
    <th>Types Screen</th>
  </tr>
  <tr>
    <td><img src="preview/preview_generations.gif" width="50%" height="50%"></td>
    <td><img src="preview/preview_types.gif" width="50%" height="50%"></td>
  </tr>
</table>

## Architecture

ComposeDex follows the [Guide to app architecture](https://developer.android.com/topic/architecture) and is built of three layers; 
the UI layer, the domain layer and the data layer.

### UI layer

The UI layer is built on [best practices](https://developer.android.com/topic/architecture/recommendations#ui-layer) 

- Activity: ComposeDex is a single-activity application and uses the [Navigation Compose](https://developer.android.com/develop/ui/compose/navigation) to switch between screens.
- Screens: Each screen is build out of composable components and can be navigated to by the user.
- ViewModels: Each UI 'screen' has its own [ViewModel][viewmodel], which exposes a single [StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow) containing the entire view state. Each [ViewModel][viewmodel] is responsible for subscribing to any data streams required for the view, as well as exposing functions which allow the UI to send events. 
- Navigation: Defines different destinations (each destination is one of the defined screens) that can be navigated to.

### Domain layer

- Use Cases: Each use case defines an [`invoke()` function](https://developer.android.com/topic/architecture/domain-layer#use-cases-kotlin) which represents a specific actions or operation that can be performed within the app.
- Models: These represent the core data objects of the app, independent of any specific data source.
- Repositories (Interfaces): Defined interfaces for data repositories, which are implemented in the data layer. This allows the domain layer to access data without depending on the specific implementations.

### Data layer

The data layer is designed with an offline-first approach in mind. There exists two different kind of data sources; the local data source and the remote data source.

- Data Sources: Each data source defines their own model and access logic. The local data source is based on a local database and the remote data source is based on an API interface. 
- Repositories: The main entry point for data access from the domain layer. Each repository that depends on a local and a remote data source is trying to fetch the requested data from the local data source first, downloading and saving it locally otherwise.

## Dependencies

### Application Dependencies

- [AndroidX Compose](https://developer.android.com/jetpack/androidx/releases/compose): A modern toolkit for building native Android user interfaces using a declarative approach, where UI elements are described as functions that transform data into UI.
- [AndroidX Core Splash](https://developer.android.com/jetpack/androidx/releases/core): Provides a way to customize the splash screen, ensuring a consistent launch animation while the app initializes.
- [AndroidX Datastore](https://developer.android.com/jetpack/androidx/releases/datastore): A data storage solution that provides a safe and consistent way to store key-value pairs.
- [AndroidX Drawerlayout](https://developer.android.com/jetpack/androidx/releases/drawerlayout): Provides a UI panel that slides in from the edge of the screen, used for the navigation menu.
- [AndroidX ExoPlayer](https://developer.android.com/reference/androidx/media3/exoplayer/ExoPlayer): An application-level media player for Android to play audio.
- [AndroidX Hilt](https://developer.android.com/jetpack/androidx/releases/hilt): A dependency injection library to integrate Dagger with Android components.
- [AndroidX Navigation](https://developer.android.com/jetpack/androidx/releases/navigation): A framework for navigating between different screens, providing a structured way to manage navigation flows and transitions.
- [AndroidX Room](https://developer.android.com/jetpack/androidx/releases/room): An abstraction layer over SQLite, providing a convenient and type-safe way to interact with the app's database.
- [Coil](https://github.com/coil-kt/coil): An image loading and caching library for Android.
- [ksp](https://github.com/google/ksp): Kotlin Symbol Processing API.
- [Material-Components](https://github.com/material-components/material-components-android): Material design components for building ripple animation, and CardView.
- [Moshi](https://github.com/square/moshi/): A modern JSON library for Android and Kotlin to parse JSON into Kotlin classes.
- [Retrofit](https://github.com/square/retrofit): Generates the REST API into an interface and provides a type-safe HTTP client.
- [Timber](https://github.com/JakeWharton/timber): Extensible logging library for Android.

### Development Environment Dependencies

- [Androidx Arch Core](https://developer.android.com/jetpack/androidx/releases/arch-core): Helper for other arch dependencies, including JUnit test rules that can be used with LiveData.
- [Androidx Test](https://developer.android.com/jetpack/androidx/releases/test): A collection of Android libraries that provide a framework for writing and running UI and instrumentation tests for Android applications.
- [detekt](https://github.com/detekt/detekt): A static code analysis tool for Kotlin to identify code smells and style violations.
- [Hilt Android Compiler](https://mvnrepository.com/artifact/com.google.dagger/hilt-android-compiler): Responsible for generating the code necessary for dependency injection. 
- [Hilt Android Testing](https://mvnrepository.com/artifact/com.google.dagger/hilt-android-testing): Provides utilities and annotations for writing and running tests with Hilt dependency injection. 
- [konsist](https://github.com/LemonAppDev/konsist): To enforce coding conventions and consistency.
- [kotlinx-coroutines-test](https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-test): Provides tools for writing and running tests for Kotlin coroutines, including utilities for controlling the dispatcher and testing asynchronous operations.
- [JUnit5](https://github.com/junit-team/junit5): A testing framework for Kotlin, providing a modern and flexible approach to writing unit and integration tests.
- [Mockito](https://github.com/mockito/mockito): A mocking framework for Kotlin, to create and configure mock objects for testing purposes.
- [Mockito-kotlin](https://github.com/mockito/mockito-kotlin): A wrapper library around Mockito that provides idiomatic Kotlin support for creating and using mocks.
- [mockwebserver](https://github.com/square/okhttp/tree/master/mockwebserver): Provides a mock web server for testing HTTP clients, to simulate network responses and verify interactions with the API.
- [Truth](https://github.com/google/truth): For writing assertions in tests, providing fluent and readable assertions.
- [Turbine](https://github.com/cashapp/turbine): For testing Kotlin flows, providing a simple and concise way to verify emitted values and flow completion.

## PokéAPI

ComposeDex is using the [PokéAPI](https://pokeapi.co/) which provides a RESTful API interface to highly detailed objects built from thousands of lines of data related to [Pokémon](https://en.wikipedia.org/wiki/Pok%C3%A9mon).

## Contributing

If you'd like to contribute to ComposeDex, please follow these guidelines:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and commit them, please use the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/#summary) specification.
4. Push your changes to your fork.
5. Submit a pull request to the main repository.

**Licensing:**

By contributing to this project, you agree that your contributions will be licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0), the same license as the project itself. This ensures that your contributions can be freely used, modified, and distributed as part of the project.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Changelog

The changelog is available [here](CHANGELOG.md).

[viewmodel]: https://developer.android.com/topic/libraries/architecture/viewmodel
