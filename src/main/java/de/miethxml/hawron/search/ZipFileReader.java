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
package de.miethxml.hawron.search;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 *
 *
 *
 */
public class ZipFileReader extends Reader {
    private ZipFile file;
    private InputStreamReader in;

    public ZipFileReader(
        String file,
        String entry) {
        init(file, entry);
    }

    public void close() throws IOException {
        if (in != null) {
            in.close();
        } else {
            throw new IOException("no inputstream");
        }
    }

    public int read(
        char[] cbuf,
        int off,
        int len) throws IOException {
        if (in != null) {
            return in.read(cbuf, off, len);
        } else {
            throw new IOException("no inputstream");
        }
    }

    private void init(
        String file,
        String entry) {
        File f = new File(file);

        if (f.exists() && f.isFile()) {
            try {
                this.file = new ZipFile(f);

                ZipEntry ze = this.file.getEntry(entry);

                in = new InputStreamReader(this.file.getInputStream(ze));
            } catch (ZipException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void mark(int readAheadLimit) throws IOException {
        if (in != null) {
            in.mark(readAheadLimit);
        } else {
            throw new IOException("no inputstream");
        }
    }

    public void reset() throws IOException {
        if (in != null) {
            in.reset();
        } else {
            throw new IOException("no inputstream");
        }
    }

    public boolean markSupported() {
        if (in != null) {
            return in.markSupported();
        }

        return false;
    }

    public boolean ready() throws IOException {
        if (in != null) {
            return in.ready();
        } else {
            throw new IOException("no inputstream");
        }
    }

    public long skip(long n) throws IOException {
        if (in != null) {
            return in.skip(n);
        } else {
            throw new IOException("no inputstream");
        }
    }

    public int read(char[] cbuf) throws IOException {
        if (in != null) {
            return in.read(cbuf);
        } else {
            throw new IOException("no inputstream");
        }
    }

    public int read() throws IOException {
        if (in != null) {
            return in.read();
        } else {
            throw new IOException("no inputstream");
        }
    }
}
