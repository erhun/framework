package com.erhun.framework.basic.utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import static java.awt.Toolkit.getDefaultToolkit;

public class ClipboardUtils {

	public static void copyToClipboard(String str) {
		StringSelection copyItem = new StringSelection(str);
		Clipboard clipboard = getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(copyItem, null);
	}

	public static String getStringFromClipboard() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable paste = clipboard.getContents(null);
		if (paste == null) {
			return null;
		}
		try {
			return (String) paste.getTransferData(DataFlavor.stringFlavor);
		} catch (Exception ex) {
			return null;
		}
	}

}
