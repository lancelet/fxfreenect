package org.fxfreenect.viewer

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.stage.Stage

import org.fxfreenect.kinect._
import org.openkinect.freenect.LedStatus;

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

  stage = new JFXApp.PrimaryStage {
    title = "Test"
    width = 600
    height = 450
    scene = new Scene { }
  }

  override def stopApp() {
    shutdownKinect()
    super.stopApp()
  }

  private def shutdownKinect() {
    sources.close()
  }

}
