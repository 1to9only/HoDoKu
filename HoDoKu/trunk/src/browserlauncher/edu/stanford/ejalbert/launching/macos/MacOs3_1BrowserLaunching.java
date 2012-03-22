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
// $Id: MacOs3_1BrowserLaunching.java,v 1.3 2006/04/11 13:36:48 jchapman0 Exp $
package browserlauncher.edu.stanford.ejalbert.launching.macos;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import browserlauncher.edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import browserlauncher.edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import browserlauncher.edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import browserlauncher.edu.stanford.ejalbert.launching.IBrowserLaunching;

/**
 * @author Markus Gebhard
 */
public class MacOs3_1BrowserLaunching
        extends MacOsBrowserLaunching {
    private Method openURL;

    public void initialize()
            throws BrowserLaunchingInitializingException {
        try {
            Class mrjFileUtilsClass = Class.forName(
                    "com.apple.mrj.MRJFileUtils");
            openURL = mrjFileUtilsClass.getDeclaredMethod("openURL",
                    new Class[] {String.class});
        }
        catch (Exception e) {
            throw new BrowserLaunchingInitializingException(e);
        }
    }

    public void openUrl(String urlString)
            throws UnsupportedOperatingSystemException,
            BrowserLaunchingExecutionException,
            BrowserLaunchingInitializingException {
        try {
            openURL.invoke(null, new Object[] {urlString});
        }
        catch (Exception e) {
            throw new BrowserLaunchingExecutionException(e);
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
}
