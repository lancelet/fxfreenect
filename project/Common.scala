import sbt._
import Keys._

/**
 * Common settings for all sub-projects.  Also encapsulates dependencies.
 */
object Common {

  // scala version
  val version = "0.1-SNAPSHOT"
  val buildScalaVersion = "2.11.0-M7"

  // dependencies
  val jna = "net.java.dev.jna" % "jna" % "4.0.0"

}