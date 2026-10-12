# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [0.3] — YYYY-MM-DD

### Changed

- `Endpoint` now extends `java.io.Closeable`, so endpoints can be used in
  try-with-resources. Closing an endpoint twice, or closing one that has never
  been connected, has no effect.
- `isConnected()` now means "usable now": socket based endpoints report `false`
  once closed (`Socket.isConnected()` keeps returning `true` after a close), and
  a client socket endpoint reports `false` until `connect()` has been called.
- `Channel.closeOutputStream()` and `Channel.closeInputStream()` now really
  close the underlying streams; they used to do nothing. Closing a pipe based
  endpoint therefore gives the peer the end of the stream, and writing to a pipe
  whose reading side has been closed fails.
- A pipe based endpoint that has been closed, or never initialized, can no
  longer be used: `connect()`, `getInputStream()` and `getOutputStream()` throw
  an `IOException`.
- `PipeEndpointImpl.getInputStream()` and `getOutputStream()` now declare
  `throws IOException`, as `Endpoint` already did.
- `slf4j-simple` is now a test-only dependency and is no longer pulled in
  transitively. Projects relying on it must declare their own SLF4J binding.

### Added

- ZIP and TAR.GZ distribution archives (library jar, `README.md`, `LICENSE.md`
  and runtime dependencies in `lib/`), attached to the GitHub Release.
- Unit tests covering endpoint closing.

### Build and release

- GitHub Actions CI building and testing on every push and pull request.
- GitHub Actions release workflow: pushing a `vX.Y.Z` tag signs the artifacts,
  publishes them to Maven Central through the Sonatype Central Portal and
  creates the GitHub Release.
- The POM carries the metadata required by Maven Central (license, developers,
  SCM) and the signing and publishing plugins.
- JUnit upgraded from 4.11 to 4.12, as required by Surefire 3.6.0 (test only).

## [0.2] — 2026-09-14

Published to Maven Central after the fact: the POM deployed there includes the
metadata required by Central (license, developers, SCM), which the tagged
source does not.

### Changed

- **Breaking:** the socket based endpoint is split into a server flavor and a
  client flavor. `SocketEndpointImpl` is replaced by `ServerSocketEndpointImpl`
  and `ClientSocketEndpointImpl`, which share `AbstractSocketEndpointImpl`.
- **Breaking:** the class names that can be overridden in
  `EndpointFactory.properties` changed accordingly:
  `org.paternostro.mock.ipc.SocketEndpoint.class` is replaced by
  `org.paternostro.mock.ipc.ServerSocketEndpoint.class` and
  `org.paternostro.mock.ipc.ClientSocketEndpoint.class`.
- **Breaking:** constructors and `init` methods of the `*Impl` classes are now
  package-private. Obtain endpoints through `EndpointFactory`.

### Added

- The build produces a Javadoc jar and a sources jar.
- The README documents the two flavors of socket endpoint.

### Fixed

- The warning messages logged by `EndpointFactory` when an overridden class
  cannot be used named the wrong classes.

## [0.1] — 2026-09-01

Published to Maven Central after the fact: the POM deployed there includes the
metadata required by Central (license, developers, SCM), which the tagged
source does not.

First release.

- `Endpoint` abstraction over `java.net.Socket`, with a socket based
  implementation for production code and a pipe based one (`PipeEndpointImpl`
  and `Channel`) for unit tests.
- `EndpointFactory`, whose implementation classes can be overridden through an
  optional `EndpointFactory.properties` file.
- Javadoc for the public API.
- Licensed under EUPL-1.2.

[0.3]: https://github.com/upaternostro/mock-ipc/compare/v0.2...v0.3
[0.2]: https://github.com/upaternostro/mock-ipc/compare/v0.1...v0.2
[0.1]: https://github.com/upaternostro/mock-ipc/compare/bf0db5f...v0.1
