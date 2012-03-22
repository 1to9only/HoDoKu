/************************************************
    Copyright 2004,2006 Markus Gebhard, Jeff Chapman

    This file is part of BrowserLauncher2.

    BrowserLauncher2 is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    BrowserLauncher2 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with BrowserLauncher2; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 ************************************************/
// $Id: MacOs3_0BrowserLaunching.java,v 1.3 2006/04/11 13:36:48 jchapman0 Exp $
package browserlauncher.edu.stanford.ejalbert.launching.macos;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import browserlauncher.edu.stanford.ejalbert.BrowserLauncher;
import browserlauncher.edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import browserlauncher.edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import browserlauncher.edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import browserlauncher.edu.stanford.ejalbert.launching.IBrowserLaunching;

/**
 * @author Markus Gebhard
 */
public class MacOs3_0BrowserLaunching
        extends MacOsBrowserLaunching {

    public void initialize()
            throws BrowserLaunchingInitializingException {
        try {
            Class linker = Class.forName("com.apple.mrj.jdirect.Linker");
            Constructor constructor = linker.getConstructor(new Class[] {Class.class});
            Object linkage = constructor.newInstance(new Object[] {
                    BrowserLauncher.class});
        }
        catch (Exception e) {
            throw new BrowserLaunchingInitializingException(e);
        }
    }

    public void openUrl(String urlString)
            throws UnsupportedOperatingSystemException,
            BrowserLaunchingExecutionException,
            BrowserLaunchingInitializingException {
        int[] instance = new int[1];
        int result = ICStart(instance, 0);
        if (result == 0) {
            int[] selectionStart = new int[] {0};
            byte[] urlBytes = urlString.getBytes();
            int[] selectionEnd = new int[] {urlBytes.length};
            result =
                    ICLaunchURL(instance[0], new byte[] {0}, urlBytes,
                                urlBytes.length, selectionStart, selectionEnd);
            if (result == 0) {
                // Ignore the return value; the URL was launched successfully
                // regardless of what happens here.
                ICStop(instance);
            }
            else {
                throw new BrowserLaunchingExecutionException(
                        "Unable to launch URL: " + result);
            }
        }
        else {
            throw new BrowserLaunchingExecutionException(
                    "Unable to create an Internet Config instance: " + result);
        }
    }

    /**
     * Returns a list of browsers to be used for browser targetting.
     * This list will always contain at least one item--the BROWSER_DEFAULT.
     *
     * @return List
     */
    public List getBrowserList() {
        List browserList = new ArrayList(1);
        browserList.add(IBrowserLaunching.BROWSER_DEFAULT);
        return browserList;
    }

    /**
     * Methods required for Mac OS X.  The presence of native methods does not cause
     * any problems on other platforms.
     */
    private native static int ICStart(int[] instance, int signature);

    private native static int ICStop(int[] instance);

    private native static int ICLaunchURL(
            int instance,
            byte[] hint,
            byte[] data,
            int len,
            int[] selectionStart,
            int[] selectionEnd);
}
