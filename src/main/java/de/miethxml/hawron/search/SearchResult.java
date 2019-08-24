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
public class SearchResult {
    private String file;
    private double score;
    private long size;
    private long modified;

    /**
     *
     *
     *
     */
    public SearchResult() {
        super();

        file = "";

        score = 0.0;

        size = 0;

        modified = 0;
    }

    /**
     *
     * @return Returns the file.
     *
     */
    public String getFile() {
        return file;
    }

    /**
     *
     * @param file
     *            The file to set.
     *
     */
    public void setFile(String file) {
        this.file = file;

        File f = new File(file);

        if (f.exists() && f.isFile()) {
            modified = f.lastModified();

            size = f.length();
        }
    }

    /**
     *
     * @return Returns the score.
     *
     */
    public double getScore() {
        return score;
    }

    /**
     *
     * @param score
     *            The score to set.
     *
     */
    public void setScore(double score) {
        this.score = score;
    }

    public void setScore(float score) {
        this.score = (double) score;
    }

    public long getLength() {
        return size;
    }

    public long getModified() {
        return modified;
    }
}
