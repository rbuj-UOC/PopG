# Changelog 4.0.4 alpha

## Added

- Added command-line startup options to streamline execution flows:
  - `-n` to start a New Run automatically.
  - `-p=path/to/file.png` to auto-save a plot screenshot after run completion and exit.
- Added support for loading simulation defaults from a JSON file at startup or using the menu item Open.
- Save menu item now saves the active parameters to a JSON file.
- Added Take Screenshot menu item which saves the graphic as a png image.
- Added keyboard accelerators: Menu items now include keyboard shortcuts.
- Added support for Look and Feel: Users can now choose from multiple visual themes, allowing the application’s appearance to adapt to their preferred style and system settings.

## Improved

- Place the window in the center of the screen and the dialogs in the center of the window.
- The ‘About PopG’ dialog has been fully rewritten for a cleaner and more informative design.

## Packaging & Distribution

- Added a build script that compiles sources and packages an executable PopG.jar.
- Added documented run modes for both script-based and JAR-based execution.
- Added a sample defaults JSON structure for quick onboarding and configuration.

