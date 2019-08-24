/*
 Copyright 2005 Simon Mieth

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package de.miethxml.toolkit.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
public class Utilities {
	public static int BUFFER_SIZE = 8192;

	private ArrayList listeners;

	private boolean interrupt = false;

	/**
	 * 
	 * 
	 * 
	 */
	public Utilities() {
		super();
		listeners = new ArrayList();
	}

	/**
	 * delete a file or directory (recursive).
	 * 
	 * @param dir
	 */

	public static void deleteFile(String dir) {
		File d = new File(dir);

		if (d.exists()) {

			if (d.isDirectory()) {
				File[] files = d.listFiles();

				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						files[i].delete();
						files[i] = null;
					} else {
						deleteFile(files[i].getAbsolutePath());
					}
				}
				d.delete();
			} else {

				d.delete();
				d = null;
			}
		}

	}

	public static int fileCount(String path) {
		int count = 0;
		File directory = new File(path);

		if (directory.isDirectory()) {
			// first count Files
			File[] files = directory.listFiles();

			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					count++;
				} else if (files[i].isDirectory()) {
					count += fileCount(files[i].getPath());
				}
			}
		}

		return count;
	}

	private void processCopy(File from, File to) {
		File dest = null;

		if (to.exists() && to.isDirectory()) {
			// file to directory
			dest = new File(to.getAbsolutePath() + File.separator
					+ from.getName());

			int count = 1;

			// rename to non-existing file
			while (dest.exists()) {
				dest = new File(to.getAbsolutePath() + File.separator
						+ from.getName() + "(" + count + ")");
				count++;
			}
		} else {
			// file to file
			dest = to;
		}

		if (from.isFile()) {
			store(from, dest);
		} else if (from.isDirectory()) {
			// dest not exists
			dest.mkdir();

			File[] files = from.listFiles();

			for (int i = 0; (i < files.length) && !interrupt; i++) {
				processCopy(files[i], dest);
			}
		}
	}

	private void store(File from, File to) {
		fireStartWriting(from.getName(), from.length());

		BufferedInputStream in = null;
		BufferedOutputStream out = null;

		try {
			in = new BufferedInputStream(new FileInputStream(from));
			out = new BufferedOutputStream(new FileOutputStream(to));

			byte[] bytes = new byte[1024];
			int len = -1;

			while (((len = in.read(bytes)) > -1) && !interrupt) {
				out.write(bytes, 0, len);
			}

			in.close();
			out.close();
			in = null;
			out = null;
			fireCompleteWriting();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (Exception e) {
				}
			}

			if (out != null) {
				try {
					out.close();
					out = null;
				} catch (Exception e) {
				}
			}
		}
	}

	public void copy(File from, File to) {
		interrupt = false;

		if (from.isFile()) {
			fireCopy(1);
		} else {
			// always directory?
			fireCopy(fileCount(from.getAbsolutePath()));
		}

		processCopy(from, to);
		fireComplete();
	}

	public void addIOListener(IOListener l) {
		listeners.add(l);
	}

	public void removeIOListener(IOListener l) {
		listeners.remove(l);
	}

	private void fireStartWriting(String name, long length) {
		Iterator i = listeners.iterator();

		while (i.hasNext()) {
			IOListener l = (IOListener) i.next();
			l.startWriting(name, length);
		}
	}

	private void fireCopy(int count) {
		Iterator i = listeners.iterator();

		while (i.hasNext()) {
			IOListener l = (IOListener) i.next();
			l.copy(count);
		}
	}

	private void fireStore(long currentBytes, long total) {
		Iterator i = listeners.iterator();

		while (i.hasNext()) {
			IOListener l = (IOListener) i.next();
			l.wrote(currentBytes, total);
		}
	}

	private void fireCompleteWriting() {
		Iterator i = listeners.iterator();

		while (i.hasNext()) {
			IOListener l = (IOListener) i.next();
			l.completeWriting();
		}
	}

	private void fireComplete() {
		Iterator i = listeners.iterator();

		while (i.hasNext()) {
			IOListener l = (IOListener) i.next();
			l.complete();
		}
	}

	public void interrupt() {
		interrupt = true;
	}

	/**
	 * Copy recursive a locale File to the FileModel
	 * 
	 * @param from
	 * @param to
	 */
	public void copy(File from, FileModel to) throws Exception {
		interrupt = false;

		if (from.isFile()) {
			fireCopy(1);
		} else {
			// always directory?
			fireCopy(fileCount(from.getAbsolutePath()));
		}

		processCopy(from, to);
		fireComplete();
	}

	public void copy(FileModel from, FileModel to) throws Exception {
		interrupt = false;

		if (from.isFile()) {
			fireCopy(1);
		} else {
			// always directory?
			//fireCopy(fileCount(from.getPath()));
			//fireCopy(1000);
		}

		processCopy(from, to);
		fireComplete();
	}
	
	
	
	
	
	private void processCopy(File from, FileModel to) throws Exception {
		FileModel dest = to;

		if (to.exists() && !to.isFile()) {
			// file to directory
			if (from.isFile()) {
				dest = to.createFile(from.getName());

				int count = 1;

				// rename to non-existing file and give up after 30 errors
				while (dest.exists() && (count < 31)) {
					dest = to.createFile(from.getName() + "(" + count + ")");
					count++;
				}
			}
		}

		if (from.isFile()) {
			fireStartWriting(from.getName(), from.length());
			copy(new FileInputStream(from), dest.getContent().getOutputStream());
		} else if (from.isDirectory()) {
			// dest not exists
			dest = dest.createDirectory(from.getName());

			File[] files = from.listFiles();

			for (int i = 0; (i < files.length) && !interrupt; i++) {
				processCopy(files[i], dest);
			}
		}

	}

	
	private void processCopy(FileModel from,FileModel to) throws Exception{
		FileModel dest = to;

		if (to.exists() && !to.isFile()) {
			// file to directory
			if (from.isFile()) {
				dest = to.createFile(from.getName());

				int count = 1;

				// rename to non-existing file and give up after 30 errors
				while (dest.exists() && (count < 31)) {
					dest = to.createFile(from.getName() + "(" + count + ")");
					count++;
				}
			}
		}

		if (from.isFile()) {
			fireStartWriting(from.getName(), from.getLength());
			copy(from.getContent().getInputStream(), dest.getContent().getOutputStream());
		} else if (!from.isFile()) {
			// dest not exists
			dest = dest.createDirectory(from.getName());

			FileModel[] files = from.getChildren();

			for (int i = 0; (i < files.length) && !interrupt; i++) {
				processCopy(files[i], dest);
			}
		}
	}
	
	
	
	private void copy(InputStream in, OutputStream out) throws Exception {
		BufferedInputStream inBuffer = null;
		BufferedOutputStream outBuffer = null;

		try {
			inBuffer = new BufferedInputStream(in);
			outBuffer = new BufferedOutputStream(out);

			byte[] bytes = new byte[BUFFER_SIZE];
			int len = -1;

			while (((len = inBuffer.read(bytes)) > -1) && !interrupt) {
				outBuffer.write(bytes, 0, len);
				
			}

			inBuffer.close();
			outBuffer.flush();
			outBuffer.close();
			inBuffer = null;
			outBuffer = null;
			fireCompleteWriting();
		} catch (IOException e) {
			throw e;
		} finally {
			if (inBuffer != null) {
				try {
					inBuffer.close();
					inBuffer = null;
				} catch (Exception e) {
				}
			}

			if (outBuffer != null) {
				try {
					outBuffer.close();
					outBuffer = null;
				} catch (Exception e) {
				}
			}
		}
	}
}
