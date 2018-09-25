# Changelog
All notable changes to this project will be documented in this file.

### [Unreleased]
- No current changes

## [1.1.0] - 2018-09-25
### Added
- `Config` class now supports the `subtree(String prefix)` function that returns a filtered Configuration with the prefix removed from matching elements.

### Changed
- Pass classloader through `ConfigSystem.load` to config SPI so that resources can be pulled as needed from the client program
- A backward compatible signature has been provided to allow clients to upgrade without requiring changes.  

### Fixed
- Properly initialize config modules from sources at construction time

## 1.0.0 - 2018-06-23
### Added
- `cagey-config-api` jar used as a compile time dependency for client code
- `cagey-config-spi` jar used as a compile time dependency for config plugin implementors
- `cagey-config-core` jar used as an API reference implementation and runtime dependency for client code
- `cagey-config-lightbend` jar adding runtime support for [lightbend/config](https://github.com/lightbend/config) to serve as a sample SPI implementation

[Unreleased]: https://github.com/xpcagey/keep-a-changelog/compare/v1..0...HEAD
[1.1.0]: https://github.com/olivierlacan/keep-a-changelog/compare/v1.0.0...v1.1.0
