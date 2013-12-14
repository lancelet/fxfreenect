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
package org.fxfreenect.kinect;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import org.openkinect.freenect.LedStatus;

/**
 * Single Kinect device.
 */
public interface Kinect extends AutoCloseable {


    /**
     * Returns the status of the Kinect.
     * <p>
     * If any operations result in an error code returned from the Kinect, then
     * the <code>status</code> property will immediately be updated with an
     * error code.
     * </p>
     *
     * @return status of the Kinect.
     */
    KinectStatus getStatus();

    /**
     * Status property of the Kinect.
     * <p>
     * If any operations result in an error code returned from the Kinect, then
     * the <code>status</code> property will immediately be updated with an
     * error code.
     * </p>
     *
     * @return status property
     */
    ReadOnlyObjectProperty<KinectStatus> statusProperty();


    /**
     * Returns the status of the LED indicator on the Kinect.
     * @return status of the LED indicator
     */
    LedStatus getLedStatus();

    /**
     * Sets the status of the LED indicator on the Kinect.
     * @param ledStatus status of the LED indicator
     */
    void setLedStatus(LedStatus ledStatus);

    /**
     * Returns the LED indicator status property of the Kinect.
     * @return LED indicator status property
     */
    ObjectProperty<LedStatus> ledStatusProperty();

}

