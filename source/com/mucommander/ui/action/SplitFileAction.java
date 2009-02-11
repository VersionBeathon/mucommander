/*
 * This file is part of muCommander, http://www.mucommander.com
 * Copyright (C) 2002-2009 Maxence Bernard
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

package com.mucommander.ui.action;

import java.util.Hashtable;

import com.mucommander.file.AbstractFile;
import com.mucommander.ui.dialog.file.SplitFileDialog;
import com.mucommander.ui.main.MainFrame;

/**
 * This action invokes the split file dialog which allows to
 * split the selected file.
 *
 * @author Mariusz Jakubowski
 */
public class SplitFileAction extends SelectedFilesAction implements InvokesDialog {
	

    public SplitFileAction(MainFrame mainFrame, Hashtable properties) {
        super(mainFrame, properties);
    }

    public void performAction() {
        AbstractFile selectedFile = mainFrame.getActiveTable().getSelectedFile();
        if (selectedFile != null && !selectedFile.isDirectory() && selectedFile.getSize() > 0) {
            AbstractFile destFolder = mainFrame.getInactivePanel().getCurrentFolder();
            new SplitFileDialog(mainFrame, selectedFile, destFolder).showDialog();
        }

    }


}