// Check for Java 8.
// Java 8 is required for JavaFX 8.
initialize := {
  val _ = initialize.value
  val specVersion = sys.props("java.specification.version")
  assert(specVersion == "1.8", "Java 1.8 is required to build and run.")
}

// JavaFX 8 libfreenect wrapper.
lazy val jfxfreenect = project settings (
  version := Common.version,
  scalaVersion := Common.buildScalaVersion
)

// Viewer application for JavaFX libfreenect data.
lazy val `fxfreenect-viewer` = project settings (
  version := Common.version,
  scalaVersion := Common.buildScalaVersion
)