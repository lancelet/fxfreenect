// Check for Java 8.
// Java 8 is required for JavaFX 8.
initialize := {
  val _ = initialize.value
  val specVersion = sys.props("java.specification.version")
  assert(specVersion == "1.8", "Java 1.8 is required to build and run.")
}

val nativeLibDir = (new File("./native-libs/build/osx/lib")).getCanonicalFile()

// JavaFX 8 libfreenect wrapper.
lazy val jfxfreenect = project settings (
  version := Common.version,
  scalaVersion := Common.buildScalaVersion,
  unmanagedBase := nativeLibDir,
  libraryDependencies ++= List(
    Common.jna
  )
)

// Viewer application for JavaFX libfreenect data.
lazy val `fxfreenect-viewer` = project settings (
  version := Common.version,
  scalaVersion := Common.buildScalaVersion,
  unmanagedBase := nativeLibDir,
  fork in run := true,
  scalacOptions ++= List(
    "-deprecation",
    "-Xlog-reflective-calls",
    "-Ywarn-adapted-args",
    "-unchecked",
    "-feature",
    "-target:jvm-1.7"
  ),
  libraryDependencies ++= List(
    Common.jna,
    Common.scalafx
  )
) dependsOn (
  jfxfreenect
)