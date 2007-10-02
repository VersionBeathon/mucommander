/*
 * This file is part of muCommander, http://www.mucommander.com
 * Copyright (C) 2002-2007 Maxence Bernard
 *
 * muCommander is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * muCommander is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mucommander.file.icon;

import com.mucommander.file.AbstractFile;
import com.mucommander.file.FileFactory;
import com.mucommander.file.impl.local.LocalFile;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * <code>LocalFileIconProvider</code> is an abstract {@link FileIconProvider} which makes things easier for
 * implementations that are only able to provide icons for local files.
 *
 * <p>This class implements {@link #getFileIcon(com.mucommander.file.AbstractFile, java.awt.Dimension)} and passes on
 * requests for local file icons to {@link #getLocalFileIcon(com.mucommander.file.impl.local.LocalFile, java.awt.Dimension)}.
 * On the other hand, requests for non-local file icons are transformed to local ones, by creating a local temporary
 * file with the same name (best effort) and extension (guaranteed) as the non-local file, and passes on the file
 * to {@link #getLocalFileIcon(com.mucommander.file.impl.local.LocalFile, java.awt.Dimension)}.</p> 
 *
 * @author Maxence Bernard
 */
public abstract class LocalFileIconProvider implements FileIconProvider {

    /**
     * Creates a returns a temporary local file/directory with the same extension as the specified file/directory
     * (guaranteed), and the same filename as much as possible (best effort).
     * This method returns <code>null</code> if the temporary file/directory could not be created.
     *
     * @param nonLocalFile the non-local file for which to create a temporary file.
     * @return a temporary local file/directory with the same extension as the specified file/directory
     */
    protected LocalFile createTempLocalFile(AbstractFile nonLocalFile) {
        try {
            LocalFile tempFile = (LocalFile)FileFactory.getTemporaryFile(nonLocalFile.getName(), false);

            // Create a directory
            if(nonLocalFile.isDirectory())
                tempFile.mkdir();
            // Create a regular file
            else
                tempFile.getOutputStream(false).close();

            return tempFile;
        }
        catch(IOException e) {
            return null;
        }
    }


    /////////////////////////////////////
    // FileIconProvider implementation //
    /////////////////////////////////////

    public Icon getFileIcon(AbstractFile file, Dimension preferredResolution) {
        // Specified file is a LocalFile or a ProxyFile proxying a LocalFile (e.g. an archive file): let's simply get
        // the icon using #getLocalFileIcon(LocalFile)
        file = file.getTopAncestor();
        Icon icon;

        if(file instanceof LocalFile) {
            icon = getLocalFileIcon((LocalFile)file, preferredResolution);
        }
        // File is a remote file: create a temporary local file (or directory) with the same extension to grab the icon
        // and then delete the file. This operation is I/O bound and thus expensive, so an LRU is used to cache
        // frequently-accessed file extensions.
        else {
            // Create the temporary, local file
            LocalFile tempFile = createTempLocalFile(file);
            if(tempFile==null) {
                // No temp file, no icon!
                return null;
            }

            // Get the file icon
            icon = getLocalFileIcon(tempFile, preferredResolution);

            // Delete the temporary file
            try {
                tempFile.delete();
            }
            catch(IOException e) {
                // Not much to do
            }
        }

        return icon;
    }


    //////////////////////
    // Abstract methods //
    //////////////////////

    /**
     * Returns an icon for the given file, or <code>null</code> if it couldn't be retrieved, either because the
     * given file doesn't exist or for any other reason.
     *
     * <p>The specified <code>Dimension</code> is used as a hint at the preferred icon's resolution; there is
     * absolutely no guarantee that the returned <code>Icon</code> will indeed have this resolution. This dimension is
     * only used to choose between different resolutions should more than one resolution be available, and return the
     * one that most closely matches the specified one.<br/>
     * This method is not expected to perform any rescaling (either up or down), returned resolutions should only be
     * 'native' icon resolutions. For example, if this provider is able to create icons both in 16x16 and 32x32
     * resolutions, and a 48x48 resolution is preferred, the 32x32 resolution should be favored for the returned icon.</p>
     *
     * @param file the LocalFile instance for which an icon is requested
     * @param preferredResolution the preferred icon resolution
     * @return an icon for the requested file
     */
    public abstract Icon getLocalFileIcon(LocalFile file, Dimension preferredResolution);

}