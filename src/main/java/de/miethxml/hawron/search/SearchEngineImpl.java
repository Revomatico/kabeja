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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import de.miethxml.hawron.gui.context.FileSystemModel;
import de.miethxml.hawron.project.Project;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.io.DefaultFileModel;
import de.miethxml.toolkit.repository.RepositorySelectionListener;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;


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
public class SearchEngineImpl implements ListSelectionListener, SearchEngine {
    private ArrayList listeners;
    private ArrayList results;
    private ArrayList fsmListener;

    //private Project project;
    private ResultTableModel resultTableModel;
    private String indexDir;
    private long lastIndex = -1;
    private Properties props;
    private boolean initialized = false;
    private FileSystemModel dummy;
    private ProjectIndexer indexer;
    private String query;
    private boolean interrupt = false;
    private boolean updateIndex = true;
    private SimpleDateFormat dateformat;
    private Date date;

    //config settings
    private String configLocation = "conf";
    private String searchRootPath = "";
    public final String CONFIGFILE = "lucene.properties";
    public final String EXTENSIONFILE = "lucene-extensions.properties";
    public final String LASTMODIEFIEDKEY = "lucene.index.lastmodifies";
    public final String INDEXDIRECTORYKEY = "lucene.index.directory";
    public final String INDEXDIRECTORY = "lucene_index";
    private NumberFormat sizeformat;
    private List fileExtensions;

    /**
     *
     *
     *
     */
    public SearchEngineImpl() {
        super();
        listeners = new ArrayList();
        results = new ArrayList();
        resultTableModel = new ResultTableModel();
        indexDir = "";
        dummy = new FileSystemModel(".");
        indexer = new ProjectIndexer();
        props = new Properties();
        fsmListener = new ArrayList();
        query = "";
        dateformat = new SimpleDateFormat(" EEE, dd. MMM yyyy hh:mm:ss");
        sizeformat = NumberFormat.getInstance();
        date = new Date();
        fileExtensions = new ArrayList();
        fileExtensions.add("*");
    }

    private void search(
        String indexDir,
        String q) {
        File index = new File(indexDir);

        if (index.exists() && index.isDirectory()) {
            try {
                IndexSearcher is = new IndexSearcher(indexDir);
                Query query = QueryParser.parse(q, "contents",
                        new StandardAnalyzer());
                Hits hits = is.search(query);

                //System.out.println("Found " + hits.length() + " hits.");
                results.clear();

                for (int i = 0; i < hits.length(); i++) {
                    Document doc = hits.doc(i);

                    //System.out.println("file: " + doc.get("filename") + "
                    // score: " + hits.score(i));
                    SearchResult result = new SearchResult();
                    result.setFile(doc.get("filename"));
                    result.setScore(hits.score(i));
                    results.add(result);
                }

                resultTableModel.fireTableUpdate();
                fireFinishedSearching(hits.length());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (org.apache.lucene.queryParser.ParseException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void search(String query) {
        this.query = query;
        this.interrupt = false;

        fireStartSearching();
        init();

        if (updateIndex) {
            indexer.cleanupIndex(this.indexDir);
            indexer.index(this.indexDir, searchRootPath, this.lastIndex);
        }

        if (!interrupt) {
            storeConfig();
            fireFinishIndexing();
            search(this.indexDir, this.query);
        }
    }

    public void addSearchResultListener(SearchResultListener l) {
        listeners.add(l);
    }

    public void removeSearchResultListener(SearchResultListener l) {
        listeners.remove(l);
    }

    private void fireStartSearching() {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            SearchResultListener l = (SearchResultListener) i.next();
            l.startSearching();
        }
    }

    private void fireFinishIndexing() {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            SearchResultListener l = (SearchResultListener) i.next();
            l.finishIndexing();
        }
    }

    private void fireFinishedSearching(int hits) {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            SearchResultListener l = (SearchResultListener) i.next();
            l.finishSearching(hits);
        }
    }

    /**
     * @return Returns the project.
     *
     */
    public Project getProject() {
        return null;
    }

    /**
     * @param project
     *
     * The project to set.
     *
     */
    public void setProject(Project project) {
        //this.project = project; 
    }

    /**
     * @return Returns the resultTableModel.
     *
     */
    public TableModel getResultTableModel() {
        return resultTableModel;
    }

    private void init() {
        if (!initialized) {
            String file = configLocation + File.separator + this.CONFIGFILE;
            File conf = new File(file);

            if (conf.exists() && conf.isFile()) {
                props = new Properties();

                try {
                    props.load(new FileInputStream(conf));

                    if (props.containsKey(this.INDEXDIRECTORYKEY)) {
                        this.indexDir = props.getProperty(this.INDEXDIRECTORYKEY);
                    } else {
                        //set new properties
                        this.indexDir = configLocation + File.separator
                            + this.INDEXDIRECTORY;
                        props.setProperty(this.INDEXDIRECTORYKEY, this.indexDir);
                        this.lastIndex = 0;

                        //System.out.println("new indexdir"+this.indexDir);
                    }

                    if (props.containsKey(this.LASTMODIEFIEDKEY)) {
                        String value = props.getProperty(this.LASTMODIEFIEDKEY);

                        try {
                            this.lastIndex = Long.parseLong(value);
                        } catch (NumberFormatException e1) {
                            this.lastIndex = 0;
                        }
                    } else {
                        this.lastIndex = 0;
                    }

                    initialized = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                this.indexDir = configLocation + File.separator + "luceneindex";
                props.setProperty("lucene.index.directory", this.indexDir);
                initialized = true;
            }
        }
    }

    public void addFileSystemSelectionListener(RepositorySelectionListener l) {
        fsmListener.add(l);
    }

    public void removeFileSystemSelectionListener(
        RepositorySelectionListener l) {
        fsmListener.remove(l);
    }

    private void fireFileSelectedEvent(String path) {
        for (int i = 0; i < fsmListener.size(); i++) {
            RepositorySelectionListener l = (RepositorySelectionListener) fsmListener
                .get(i);
            l.fileSelected(dummy, new DefaultFileModel(new File(path), null));
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     *
     */
    public void valueChanged(ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        int first = lsm.getMinSelectionIndex();

        if (first > -1) {
            SearchResult result = (SearchResult) results.get(first);
            fireFileSelectedEvent(result.getFile());
        }
    }

    public void recycle() {
        initialized = false;
        results.clear();
    }

    private void storeConfig() {
        //store config
        String file = configLocation + File.separator + this.CONFIGFILE;

        if (updateIndex) {
            this.lastIndex = System.currentTimeMillis();
            props.setProperty(this.LASTMODIEFIEDKEY,
                Long.toString(this.lastIndex));
        }

        try {
            props.store(new FileOutputStream(file), "Lucene configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void interruptSearch() {
        //only indexing is interrupted
        this.interrupt = true;
        indexer.interruptIndexing();
        fireFinishedSearching(0);
    }

    public void setUpdateIndex(boolean state) {
        this.updateIndex = state;
    }

    public void setFileExtensions(List extensions) {
        this.fileExtensions = extensions;
        indexer.setFilter(fileExtensions);
        saveFileExtensions();
    }

    public void setConfigLocation(String configLocation) {
        if (configLocation != null) {
            this.configLocation = configLocation;
            recycle();
            init();
            loadFileExtension();
        } else {
            configLocation = "";
        }
    }

    public void setSearchRootPath(String rootPath) {
        this.searchRootPath = rootPath;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.search.SearchEngine#getFileExtensions()
     */
    public List getFileExtensions() {
        // TODO Auto-generated method stub
        return fileExtensions;
    }

    private void saveFileExtensions() {
        String file = configLocation + File.separator + this.EXTENSIONFILE;
        File f = new File(file);

        if (f.exists()) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(f));
                Iterator i = fileExtensions.iterator();

                while (i.hasNext()) {
                    out.write((String) i.next() + "\n");
                }

                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadFileExtension() {
        String file = configLocation + File.separator + this.EXTENSIONFILE;
        File f = new File(file);
        fileExtensions.clear();

        if (f.exists() && f.isFile()) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(f));
                String line = null;

                while ((line = in.readLine()) != null) {
                    fileExtensions.add(line);
                }

                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            fileExtensions.add("*");
        }

        indexer.setFilter(fileExtensions);
    }

    public class ResultTableModel implements TableModel {
        private ArrayList listeners;
        private TableModelEvent event;
        private DecimalFormat scoreFormat = new DecimalFormat(" ###.## % ");

        public ResultTableModel() {
            listeners = new ArrayList();
            event = new TableModelEvent(this);
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
         *
         */
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.table.TableModel#getColumnClass(int)
         *
         */
        public Class getColumnClass(int columnIndex) {
            return "".getClass();
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.table.TableModel#getColumnCount()
         *
         */
        public int getColumnCount() {
            return 2;
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.table.TableModel#getColumnName(int)
         *
         */
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
            case 0:
                return LocaleImpl.getInstance().getString("search.result.table.header.file");

            case 1:
                return LocaleImpl.getInstance().getString("search.result.table.header.score");
            }

            return null;
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.table.TableModel#getRowCount()
         *
         */
        public int getRowCount() {
            return results.size();
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         *
         */
        public Object getValueAt(
            int rowIndex,
            int columnIndex) {
            if ((rowIndex >= 0) && (rowIndex < results.size())) {
                SearchResult result = (SearchResult) results.get(rowIndex);

                switch (columnIndex) {
                case 0:
                    return result.getFile();

                case 1:
                    return scoreFormat.format(result.getScore());
                }
            }

            return null;
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         *
         */
        public boolean isCellEditable(
            int rowIndex,
            int columnIndex) {
            return false;
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
         *
         */
        public void removeTableModelListener(TableModelListener l) {
            listeners.remove(l);
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int,
         *
         * int)
         *
         */
        public void setValueAt(
            Object aValue,
            int rowIndex,
            int columnIndex) {
        }

        public void fireTableUpdate() {
            Iterator i = listeners.iterator();

            while (i.hasNext()) {
                TableModelListener l = (TableModelListener) i.next();
                l.tableChanged(event);
            }
        }
    }
}
