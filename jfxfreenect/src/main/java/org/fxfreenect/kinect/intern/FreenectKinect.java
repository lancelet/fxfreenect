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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.fxfreenect.kinect.*;
import org.openkinect.freenect.Device;
import org.openkinect.freenect.LedStatus;

public class FreenectKinect implements Kinect {

    //---------------------------------------------------------------------------------------------------------- PUBLIC

    public FreenectKinect(Device device) {
        m_device = device;
        m_device.setLed(m_ledStatus.getValue());
    }

    public void close() {
        m_device.close();
        m_device = null;
    }

    public KinectStatus getStatus() { return m_status.getValue(); }

    public ReadOnlyObjectProperty<KinectStatus> statusProperty() { return m_status.getReadOnlyProperty(); }

    public LedStatus getLedStatus() { return m_ledStatus.getValue(); }

    public void setLedStatus(LedStatus ledStatus) {
        int ret = m_device.setLed(ledStatus);
        if (ret < 0) {
            setErrorStatusFromUSB(ret);
        } else {
            m_ledStatus.setValue(ledStatus);
        }
    }

    public ObjectProperty<LedStatus> ledStatusProperty() { return m_ledStatus; }

    //--------------------------------------------------------------------------------------------------------- PRIVATE

    private ReadOnlyObjectWrapper<KinectStatus> m_status =
        new ReadOnlyObjectWrapper<>(this, "status", KinectStatusOK.getInstance());
    private Device m_device;
    private ObjectProperty<LedStatus> m_ledStatus =
        new ReadOnlyObjectWrapper<>(this, "ledStatus", LedStatus.BLINK_GREEN);

    private void setErrorStatusFromUSB(int usbError) {
        assert(usbError < 0);
        KinectException ke = KinectException.fromUSBError(usbError);
        m_status.setValue(new KinectStatusException(ke));
    }

}
