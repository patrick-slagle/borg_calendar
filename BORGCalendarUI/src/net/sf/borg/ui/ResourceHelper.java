/*
  This file is part of BORG.

  BORG is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  BORG is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with BORG; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

  Copyright 2003 by Mike Berger
*/

package net.sf.borg.ui;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

import net.sf.borg.common.util.Resource;

/**
 * Helps parse resource strings (containing text and optional
 * mnemonics and accelerators) for GUI widgets.
 * @author membar
 */
public class ResourceHelper
{
public static void setText(JMenu mnu, String resourceKey)
{
	Resource.ComponentParms parms = Resource.parseParms(resourceKey);
	mnu.setText(parms.getText());
	if (parms.getKeyEvent() != -1)
		mnu.setMnemonic(parms.getKeyEvent());
}

public static void setText(JMenuItem mnuitm, String resourceKey)
{
	Resource.ComponentParms parms = Resource.parseParms(resourceKey);
	mnuitm.setText(parms.getText());
	if (parms.getKeyEvent() != -1)
		mnuitm.setMnemonic(parms.getKeyEvent());
	if (parms.getKeyStroke() != null)
		mnuitm.setAccelerator(parms.getKeyStroke());
}

public static void setText(AbstractButton button, String resourceKey)
{
	Resource.ComponentParms parms = Resource.parseParms(resourceKey);
	button.setText(parms.getText());
	if (parms.getKeyEvent() != -1)
		button.setMnemonic(parms.getKeyEvent());
}

public static void setText(JLabel label, String resourceKey)
{
	Resource.ComponentParms parms = Resource.parseParms(resourceKey);
	label.setText(parms.getText());
	if (parms.getKeyEvent() != -1)
		label.setDisplayedMnemonic(parms.getKeyEvent());
}

public static void addTab(JTabbedPane tabbedPane, String resourceKey,
		JComponent comp)
{
	Resource.ComponentParms parms = Resource.parseParms(resourceKey);
	tabbedPane.add(parms.getText(), comp);
	if (parms.getKeyEvent() != -1)
		tabbedPane.setMnemonicAt(tabbedPane.getTabCount()-1, parms.getKeyEvent());
}

public static void setTitle(JFrame frame, String resourceKey)
{
	frame.setTitle(Resource.getPlainResourceString(resourceKey));
}

public static void setTitle(JDialog dlg, String resourceKey)
{
	dlg.setTitle(Resource.getPlainResourceString(resourceKey));
}

// private //
private ResourceHelper()
{}

}
