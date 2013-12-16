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

import com.sun.jna.NativeLibrary;
import org.fxfreenect.kinect.Kinect;
import org.fxfreenect.kinect.KinectSource;
import org.openkinect.freenect.Context;
import org.openkinect.freenect.Freenect;

import java.util.HashMap;
import java.util.Map;

public final class FreenectKinectSource implements KinectSource {

    static {
        NativeLibrary.addSearchPath("freenect", "../native-libs/build/osx/lib/");
    }

    //---------------------------------------------------------------------------------------------------------- PUBLIC

    public FreenectKinectSource() {
        m_context = Freenect.createContext();
    }

    public int getNumDevices() {
        return m_context.numDevices();
    }

    public Kinect getKinectNumber(int index) {

        // if the Kinect is already active and hasn't been closed, then return it
        if (m_activeKinects.containsKey(index)) {
            FreenectKinect kinect = m_activeKinects.get(index);
            if (!kinect.isClosed()) {
                return kinect;
            } else {
                m_activeKinects.remove(index);
            }
        }

        // create a new Kinect object
        FreenectKinect kinect = new FreenectKinect(m_context.openDevice(index));
        m_activeKinects.put(index, kinect);
        return kinect;

    }

    public void close() {

        // shut down any active Kinects
        for (FreenectKinect kinect : m_activeKinects.values()) {
            if (!kinect.isClosed()) {
                kinect.close();
            }
        }

        // shut down the context and clean up
        m_activeKinects.clear();
        m_context.shutdown();
        m_context = null;

    }

    //--------------------------------------------------------------------------------------------------------- PRIVATE

    private Context m_context;

    private Map<Integer, FreenectKinect> m_activeKinects = new HashMap<>();

}
