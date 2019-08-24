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

import java.util.List;

import javax.swing.table.TableModel;

import de.miethxml.hawron.project.Project;

import de.miethxml.toolkit.repository.RepositorySelectionListener;


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
public interface SearchEngine {
    public abstract void search(String query);

    public abstract void addSearchResultListener(SearchResultListener l);

    public abstract void removeSearchResultListener(SearchResultListener l);

    /**
     *
     * @param project
     *
     * The project to set.
     *
     */
    public abstract void setProject(Project project);

    /**
     *
     * @return Returns the resultTableModel.
     *
     */
    public abstract TableModel getResultTableModel();

    public abstract void addFileSystemSelectionListener(
        RepositorySelectionListener l);

    public abstract void removeFileSystemSelectionListener(
        RepositorySelectionListener l);

    public abstract void interruptSearch();

    public abstract void setUpdateIndex(boolean state);

    public void setFileExtensions(List extensions);

    public List getFileExtensions();
}
