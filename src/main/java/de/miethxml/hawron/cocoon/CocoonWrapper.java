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
package de.miethxml.hawron.cocoon;

import java.util.Hashtable;

import de.miethxml.hawron.project.ProcessListener;
import de.miethxml.hawron.project.ProcessURI;


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 *
 * @deprecated This interface will be removed, the CocoonBeanController will the
 *             new one.
 *
 */
public interface CocoonWrapper {
    public void addProcessListener(ProcessListener l);

    public void removeProcessListener(ProcessListener l);

    public void build();

    public void setInitializeProperties(Hashtable props);

    public void setProperties(Hashtable props);

    /*
     *
     * @deprecated
     */
    public void addTask(
        String sourceURI,
        String destURI);

    public void addTask(
        ProcessURI uri,
        boolean followLinks,
        boolean confirmExtension);

    public void recycle();

    public void cancel();
}
