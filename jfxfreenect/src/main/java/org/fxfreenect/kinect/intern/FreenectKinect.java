/**
 * This file is part of the FxFreenect project.
 *
 * Copyright (C) 2013 FxFreenect contributors.  See the CONTRIB file for
 * details.
 *
 * This code is licensed to you under the terms of the Apache License, version
 * 2.0, or, at your option, the terms of the GNU General Public License,
 * version 2.0. See the APACHE20 and GPL20 files for the text of the licenses,
 * or the following URLs:
 * http://www.apache.org/licenses/LICENSE-2.0
 * http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * If you redistribute this file in source form, modified or unmodified,
 * you may:
 * 1) Leave this header intact and distribute it under the same terms,
 * accompanying it with the APACHE20 and GPL20 files, or
 * 2) Delete the Apache 2.0 clause and accompany it with the GPL20 file, or
 * 3) Delete the GPL v2.0 clause and accompany it with the APACHE20 file
 * In all cases you must keep the copyright notice intact and include a copy
 * of the CONTRIB file.
 * Binary distributions must follow the binary distribution requirements of
 * either License.
 */
package org.fxfreenect.kinect.intern;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.fxfreenect.kinect.*;
import org.openkinect.freenect.*;

import java.nio.ByteBuffer;

public class FreenectKinect implements Kinect {

    //---------------------------------------------------------------------------------------------------------- PUBLIC

    public FreenectKinect(Device device) {
        m_device = device;
        m_device.setLed(m_ledStatus.getValue());
    }

    public void close() {
        synchronized (m_device) {
            if (m_videoStreamActive) {
                stopVideo();
                m_videoStreamActive = false;
            }
            m_device.close();
            m_device = null;
        }
    }

    public boolean isClosed() { return m_device == null; }

    public KinectStatus getStatus() { return m_status.getValue(); }

    public ReadOnlyObjectProperty<KinectStatus> statusProperty() { return m_status.getReadOnlyProperty(); }

    public LedStatus getLedStatus() { return m_ledStatus.getValue(); }

    public void setLedStatus(LedStatus ledStatus) {
        synchronized (m_device) {
            int ret = m_device.setLed(ledStatus);
            if (ret < 0) {
                setErrorStatusFromUSB(ret);
            } else {
                m_ledStatus.setValue(ledStatus);
            }
        }
    }

    public ObjectProperty<LedStatus> ledStatusProperty() { return m_ledStatus; }

    public Image getVideoImage() {
        return m_videoImage.get();
    }

    public ReadOnlyObjectProperty<Image> videoImageProperty() {
        return m_videoImage.getReadOnlyProperty();
    }

    public void startVideo() {
        synchronized (m_device) {
            m_device.setVideoFormat(VideoFormat.RGB, m_videoResolution.get());
            m_device.startVideo(m_videoHandler);
            m_videoStreamActive = true;
        }
    }

    public void stopVideo() {
        synchronized (m_device) {
            m_device.stopVideo();
            m_videoStreamActive = false;
        }
    }

    public Resolution getVideoResolution() {
        return m_videoResolution.get();
    }

    public void setVideoResolution(Resolution resolution) {
        synchronized (m_device) {
            // stop the video stream if it's currently active
            if (m_videoStreamActive) {
                m_device.stopVideo();
            }
            //try { Thread.sleep(100); } catch (InterruptedException ex) { }
            // update the resolution
            m_videoResolution.set(resolution);
            m_device.setVideoFormat(VideoFormat.RGB, m_videoResolution.get());
            //try { Thread.sleep(100); } catch (InterruptedException ex) { }
            // re-start the video stream if it was originally active
            if (m_videoStreamActive) {
                m_device.startVideo(m_videoHandler);
            }
        }
    }

    public ObjectProperty<Resolution> videoResolutionProperty() {
        return m_videoResolution;
    }


    //--------------------------------------------------------------------------------------------------------- PRIVATE

    private ReadOnlyObjectWrapper<KinectStatus> m_status =
        new ReadOnlyObjectWrapper<>(this, "status", KinectStatusOK.getInstance());
    private Device m_device;
    private ObjectProperty<LedStatus> m_ledStatus =
        new ReadOnlyObjectWrapper<>(this, "ledStatus", LedStatus.BLINK_GREEN);
    private ReadOnlyObjectWrapper<Image> m_videoImage =
        new ReadOnlyObjectWrapper<>(this, "videoImage", new WritableImage(1, 1));
    private ObjectProperty<Resolution> m_videoResolution =
        new SimpleObjectProperty<>(this, "videoResolution", Resolution.MEDIUM);
    private boolean m_videoStreamActive = false;

    private void setErrorStatusFromUSB(int usbError) {
        assert(usbError < 0);
        KinectException ke = KinectException.fromUSBError(usbError);
        m_status.setValue(new KinectStatusException(ke));
    }

    private void setErrorStatusFromMessage(String message) {
        KinectException ke = new KinectException(message);
        m_status.setValue(new KinectStatusException(ke));
    }

    private VideoHandler m_videoHandler = new VideoHandler() {
        public void onFrameReceived(FrameMode frameMode, ByteBuffer buffer, int timestamp) {
            int width  = frameMode.getWidth();
            int height = frameMode.getHeight();
            WritableImage writeableImage = new WritableImage(width, height);
            VideoFormat videoFormat = frameMode.getVideoFormat();
            if (videoFormat == VideoFormat.RGB) {
                PixelWriter p = writeableImage.getPixelWriter();
                p.setPixels(0, 0, width, height, PixelFormat.getByteRgbInstance(), buffer, 3 * width);
            } else {
                setErrorStatusFromMessage("A non-RGB frame was received.");
            }
            // TODO: examine per-frame image switching
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    m_videoImage.set(writeableImage);
                }
            });
        }
    };

}
