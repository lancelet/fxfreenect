package org.fxfreenect.viewer

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.stage.Stage

import org.fxfreenect.kinect._
import org.openkinect.freenect.LedStatus;

object Test extends JFXApp {

  //System.setProperty("jna.debug_load", "true")
  //NativeLibrary.addSearchPath("freenect", "../native-libs/build/osx/lib")

  val sources: KinectSource = new DefaultKinectSource()
  val nDevices: Int = sources.getNumDevices()
  if (nDevices > 0) {
    println(s"$nDevices Kinect devices are attached")
    val kinect: Kinect = sources.getKinectNumber(0)
    kinect.setLedStatus(LedStatus.fromInt(LedStatus.BLINK_GREEN.intValue()))
    println(kinect.getStatus)
  } else {
    println("No Kinect devices attached.")
  }

  stage = new JFXApp.PrimaryStage {
    title = "Test"
    width = 600
    height = 450
    scene = new Scene { }
  }
}
