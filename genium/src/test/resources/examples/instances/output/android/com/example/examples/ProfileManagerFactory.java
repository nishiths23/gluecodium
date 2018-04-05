/*
 *
 * Automatically generated. Do not modify. Your changes will be lost.
 */

package com.example.examples;

import com.example.NativeBase;

public class ProfileManagerFactory extends NativeBase {
    /** For internal use only */
    protected ProfileManagerFactory(final long nativeHandle) {
        super(nativeHandle, new Disposer() {
            @Override
            public void disposeNative(long handle) {
                disposeNativeHandle(handle);
            }
        });
    }
    private static native void disposeNativeHandle(long nativeHandle);
    public static native ProfileManager createProfileManager();
    public static native ProfileManagerInterface createProfileManagerInterface();
}