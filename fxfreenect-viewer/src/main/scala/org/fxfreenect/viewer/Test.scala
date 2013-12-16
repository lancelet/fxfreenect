package org.fxfreenect.viewer

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.stage.Stage

import org.fxfreenect.kinect._
import org.openkinect.freenect.{Resolution, LedStatus}
import scalafx.scene.image.ImageView
import scalafx.scene.control.ComboBox
import scalafx.event.ActionEvent

object Test extends JFXApp {

  //System.setProperty("jna.debug_load", "true")

  val sources: KinectSource = new DefaultKinectSource()
  val kinect: Option[Kinect] = {
    val nDevices: Int = sources.getNumDevices()
    if (nDevices > 0) {
      println(s"$nDevices Kinect devices are attached.")
      Some(sources.getKinectNumber(0))
    } else {
      println("No Kinect devices attached.")
      None
    }
  }

  // set LED
  for (k <- kinect) k.setLedStatus(LedStatus.fromInt(LedStatus.BLINK_GREEN.intValue))

  // set up RGB image view
  val imageViewRGB = new ImageView
  for (k <- kinect) {
    imageViewRGB.image <== k.videoImageProperty
    k.startVideo()
    k.setVideoResolution(Resolution.LOW)
  }

  // resolution of RGB image
  val comboBox = new ComboBox[String]
  comboBox.getItems.add("LOW")
  comboBox.getItems.add("MEDIUM")
  comboBox.getItems.add("HIGH")
  comboBox.onAction = {(e: ActionEvent) => {
    val s: String = comboBox.getValue
    val r: Resolution = s match {
      case "LOW"    => Resolution.LOW
      case "MEDIUM" => Resolution.MEDIUM
      case "HIGH"   => Resolution.HIGH
    }
    // TODO: Investigate why mode-switching is not working.
    for (k <- kinect) {
      println(s"Setting resolution to ${comboBox.getValue}")
      k.setVideoResolution(r)
    }
  }}

  stage = new JFXApp.PrimaryStage {
    title = "Test"
    width = 600
    height = 450
    scene = new Scene {
      content = Set(
        imageViewRGB,
        comboBox
      )
    }
  }

  override def stopApp() {
    shutdownKinect()
    super.stopApp()
  }

  private def shutdownKinect() {
    sources.close()
  }

}
