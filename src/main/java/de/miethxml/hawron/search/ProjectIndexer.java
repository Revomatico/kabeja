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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.miethxml.hawron.io.FileMatcher;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;


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
public class ProjectIndexer {
    private final static String FIELD_FILENAME = "filename";
    private long lastIndex;
    private boolean interrupt = false;
    private FileMatcher matcher;

    /**
     *
     *
     *
     */
    public ProjectIndexer() {
        super();
        lastIndex = 0;

        ArrayList list = new ArrayList();
        list.add("*");
        matcher = new FileMatcher(list);
    }

    public void index(
        String indexFile,
        String dataFile) {
        boolean create = false;
        File index = new File(indexFile);

        if (!index.exists() && !index.isDirectory()) {
            create = true;
        }

        if (lastIndex == 0) {
            create = true;
        }

        File data = new File(dataFile);

        if (data.exists() && data.isDirectory()) {
            try {
                IndexWriter writer = new IndexWriter(index,
                        new StandardAnalyzer(), create);
                indexDirectory(writer, data);
                writer.optimize();
                writer.close();
                writer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void index(
        String indexDir,
        String dataDir,
        long lastIndex) {
        this.lastIndex = lastIndex;
        this.interrupt = false;
        index(indexDir, dataDir);
    }

    private void indexDirectory(
        IndexWriter writer,
        File directory) {
        File[] entries = directory.listFiles();

        for (int i = 0; (i < entries.length) && !interrupt; i++) {
            if (entries[i].isDirectory()) {
                indexDirectory(writer, entries[i]);
            } else if (entries[i].isFile()) {
                indexFile(writer, entries[i]);
            }
        }
    }

    private void indexFile(
        IndexWriter writer,
        File file) {
        if (isSupported(file) && (file.lastModified() > this.lastIndex)) {
            //			System.out.println("index file: " + file.getName() + " mod: " +
            // file.lastModified() + " index:"
            //					+ this.lastIndex);
            Document doc = new Document();

            if (file.getName().toLowerCase().endsWith(".sxw")) {
                doc.add(Field.Text("contents",
                        new ZipFileReader(file.getAbsolutePath(), "content.xml")));
            } else {
                try {
                    doc.add(Field.Text("contents", new FileReader(file)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            try {
                doc.add(Field.Keyword(FIELD_FILENAME, file.getCanonicalPath()));
                writer.addDocument(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isSupported(File f) {
        return matcher.matches(f.getAbsolutePath());
    }

    public void interruptIndexing() {
        this.interrupt = true;
    }

    public void setFilter(List filter) {
        matcher.setPattern(filter);
    }

    public void cleanupIndex(String indexfile) {
        try {
            IndexReader ireader = IndexReader.open(new File(indexfile));
            TermEnum te = ireader.terms();
            ArrayList list = new ArrayList();

            while (te.next()) {
                Term t = te.term();

                if (t.field().equals(FIELD_FILENAME)) {
                    File f = new File(t.text());

                    if (!f.exists()) {
                        list.add(t);
                    }
                }
            }

            Iterator i = list.iterator();

            while (i.hasNext()) {
                Term t = (Term) i.next();
                ireader.delete(t);
            }

            ireader.close();
            ireader = null;
            te = null;
            list.clear();
            list = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
