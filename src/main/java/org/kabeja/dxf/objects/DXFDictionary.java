/*
   Copyright 2007 Simon Mieth

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
package org.kabeja.dxf.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kabeja.dxf.DXFConstants;


public class DXFDictionary extends DXFObject {
    protected List<DXFDictionaryRecord> records = new ArrayList<DXFDictionaryRecord>();

    @Override
    public String getObjectType() {
        return DXFConstants.OBJECT_TYPE_DICTIONARY;
    }

    public boolean hasDXFObjectByID(String id) {
        return findByID(id) != null;
    }

    public String getNameForDXFObjectID(String id) {
        return findByID(id).getName();
    }

    /**
     * Gets the
     *
     * @see DXFObject with the specified ID.
     * @param id
     * @return the DXFObject or null if there is no such DXFObject
     */
    public DXFObject getDXFObjectByID(String id) {
        //search for child dictionaries
        DXFDictionary dic = this.getDXFDictionaryForID(id);

        if (dic != null) {
            DXFDictionaryRecord dicRecord = dic.findByID(id);

            if (dicRecord != null) {
                return dicRecord.getDXFObject();
            }
        }

        return null;
    }

    public DXFObject getDXFObjectByName(String name) {
        DXFDictionaryRecord record = findByName(name);

        if (record != null) {
            return record.getDXFObject();
        }

        return null;
    }

    public void putDXFObject(DXFObject obj) {
        findByID(obj.getID()).setDXFObject(obj);
    }

    public void putDXFObjectRelation(String name, String id) {
        DXFDictionaryRecord record = null;

        if ((record = findByName(name)) != null) {
            record.setID(id);
        } else {
            record = new DXFDictionaryRecord(name, id);
            this.records.add(record);
        }
    }

    protected DXFDictionaryRecord findByName(String name) {
        for (int i = 0; i < this.records.size(); i++) {
            DXFDictionaryRecord record = records.get(i);

            if (record.getName().equals(name)) {
                return record;
            }
        }

        return null;
    }

    protected DXFDictionaryRecord findByID(String id) {
        for (int i = 0; i < this.records.size(); i++) {
            DXFDictionaryRecord record = records.get(i);

            if (record.getID().equals(id)) {
                return record;
            }
        }

        return null;
    }

    /**
     * Searches recursive for the dictionary which holds the ID
     *
     * @param id
     * @return the dictionary or null
     */
    public DXFDictionary getDXFDictionaryForID(String id) {
        Set<DXFDictionary> dictionaries = new HashSet<DXFDictionary>();
        DXFObject obj = null;

        for (int i = 0; i < this.records.size(); i++) {
            DXFDictionaryRecord record = records.get(i);

            if (record.getID().equals(id)) {
                return this;
            } else if (((obj = record.getDXFObject()) != null) &&
                    obj.getObjectType()
                           .equals(DXFConstants.OBJECT_TYPE_DICTIONARY)) {
                dictionaries.add((DXFDictionary) obj);
            }
        }

        Iterator<DXFDictionary> ie = dictionaries.iterator();

        while (ie.hasNext()) {
            DXFDictionary dic = ie.next();
            DXFDictionary d = dic.getDXFDictionaryForID(id);

            if (d != null) {
                return d;
            }
        }

        return null;
    }

    /**
     *
     * @return iterator over all DXFObjects in this dictionary
     */
    public Iterator<DXFObject> getDXFObjectIterator() {
        return new Iterator<DXFObject>() {
                int count = 0;

                @Override
                public boolean hasNext() {
                    return count < records.size();
                }

                @Override
                public DXFObject next() {
                    return records.get(count++).getDXFObject();
                }

                @Override
                public void remove() {
                    records.remove(count - 1);
                }
            };
    }

    private class DXFDictionaryRecord {
        private String id;
        private String name;
        private DXFObject obj;

        public DXFDictionaryRecord(String name, String id) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String getID() {
            return this.id;
        }

        public void setID(String id) {
            this.id = id;
        }

        public void setDXFObject(DXFObject obj) {
            this.obj = obj;
        }

        public DXFObject getDXFObject() {
            return this.obj;
        }
    }
}
