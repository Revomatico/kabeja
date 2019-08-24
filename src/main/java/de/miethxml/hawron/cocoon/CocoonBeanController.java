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

import java.io.OutputStream;

import de.miethxml.hawron.project.ProcessListener;
import de.miethxml.hawron.project.Task;


/**
 *
 * This controller interface for the CocoonBean
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
public interface CocoonBeanController {
    /**
     *
     * Configure and initialize a the CocoonBean
     *
     * with the given BeanConfiguration
     *
     */
    public void initialize(BeanConfiguration config);

    /**
     *
     * Process the given Task
     *
     */
    public void processTask(Task task);

    /**
     *
     * Recycle the CocoonBean for reprocessing Pipelines, with the current
     * implementation of CocoonBean is only
     *
     * a dispose() and creating a new CocoonBean instance.
     *
     *
     *
     */
    public void recycle();

    /**
     *
     * Interrupt the processing.
     *
     */
    public void interrupt();

    public void addProcessListener(ProcessListener l);

    public void removeProcessListener(ProcessListener l);

    /**
     *
     * @return the used CocoonBean
     */
    public Object getCocoonBean();

    public void processUri(
        String uri,
        OutputStream out);

    public void dispose();
}
