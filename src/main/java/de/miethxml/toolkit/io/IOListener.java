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


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public interface IOListener {
    /**
     *
     * Will be called first and report the filecount.
     *
     * @param files
     *
     */
    public abstract void copy(int files);

    public abstract void startWriting(
        String name,
        long length);

    /**
     *
     *
     *
     * @param bytes
     *
     */
    public abstract void wrote(
        long count,
        long total);

    /**
     *
     * Will be called after a file is stored to the new location
     *
     *
     *
     */
    public abstract void completeWriting();

    /**
     *
     * Will be called after all work is done.
     *
     *
     *
     */
    public abstract void complete();
}
