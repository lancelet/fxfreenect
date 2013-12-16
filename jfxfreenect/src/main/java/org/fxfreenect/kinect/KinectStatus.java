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

/**
 * Represents the ongoing status of the Kinect.
 * <p>
 * Some operations may result in errors from the Kinect that cannot easily
 * be represented within JavaFX property changes.  Instead, when errors occur,
 * a special <code>status</code> property on the Kinect is set to an error
 * value, so that problems may be monitored.
 * </p>
 */
public interface KinectStatus {

    /**
     * Returns whether or not the current status is "OK".
     * @return true if the current status is OK
     */
    boolean isOK();

    /**
     * Returns any exception, if the current status is not "OK".
     * @return exception detected
     */
    public KinectException getException();

}
