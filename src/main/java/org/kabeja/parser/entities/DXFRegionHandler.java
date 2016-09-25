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
package org.kabeja.parser.entities;

import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFRegion;
import org.kabeja.parser.DXFValue;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class DXFRegionHandler extends AbstractEntityHandler {
    protected static final int DATA = 1;
    protected static final int APPEND_DATA = 3;
    protected DXFRegion region;
    protected StringBuilder data = new StringBuilder();

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.parser.entities.AbstractEntityHandler#getDXFEntityName()
     */
    @Override
    public String getDXFEntityName() {
        return DXFConstants.ENTITY_TYPE_REGION;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.parser.entities.DXFEntityHandler#startDXFEntity()
     */
    @Override
    public void startDXFEntity() {
        region = new DXFRegion();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.parser.entities.DXFEntityHandler#parseGroup(int,
     *      de.miethxml.kabeja.parser.DXFValue)
     */
    @Override
    public void parseGroup(int groupCode, DXFValue value) {
        switch (groupCode) {
        case DATA:
            //first cleanup
            checkBuffer();
            data.append(value.getValue());

            break;

        case APPEND_DATA:
            data.append(value.getValue());

            break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.parser.entities.DXFEntityHandler#getDXFEntity()
     */
    @Override
    public DXFEntity getDXFEntity() {
        return region;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.parser.entities.DXFEntityHandler#endDXFEntity()
     */
    @Override
    public void endDXFEntity() {
        checkBuffer();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.parser.entities.DXFEntityHandler#isFollowSequence()
     */
    @Override
    public boolean isFollowSequence() {
        return false;
    }

    protected String decodeDATA(String s) {
        char[] c = s.toCharArray();
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < c.length; i++) {
            if (Character.isWhitespace(c[i])) {
                buf.append(' ');
            } else {
                int code = Math.abs((c[i]) - 159);
                buf.append((char) code);
            }
        }

        return buf.toString();
    }

    protected void checkBuffer() {
        if (data.length() > 0) {
            region.appendACISDATA(decodeDATA(data.toString()));
            data.delete(0, data.length());
        }
    }
}
