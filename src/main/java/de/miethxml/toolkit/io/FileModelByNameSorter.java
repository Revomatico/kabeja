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
 * @author simon
 */
public class FileModelByNameSorter implements FileModelSorter{

	/* (non-Javadoc)
	 * @see de.miethxml.toolkit.io.FileModelSorter#sort(de.miethxml.toolkit.io.FileModel[])
	 */
	public void sort(FileModel[] list) {
	      
        
        
        
        
        
        //sorting for directories and files and sorting both alphabetically
        FileModel pivot;
        int sortLength = list.length;
        boolean next = true;
        int lastDirectory = list.length - 1;

        for (int i = 0; i < sortLength; i++) {
            if (list[i].isFile()) {
                //search a directory from end and sort the file
                //if there are only files in the array this is very slow, so we
                // jump then to quicksort
                int x = list.length - 1;

                while (list[x].isFile() && (x > i)) {
                    x--;
                }

                if ((x == i) && list[i].isFile()) {
                    quickSortByName(list, i, (list.length - 1));

                    //finished
                    return;
                } else {
                    pivot = list[x];
                    list[x] = list[i];
                    list[i] = pivot;
                    lastDirectory = x;
                }
            }

            if (!list[i].isFile()) {
                //is directory
                int y = 0;
                next = true;

                while ((y < i) && !list[y].isFile()) {
                    if (list[y].getName().compareTo(list[i].getName()) >= 0) {
                        //change
                        pivot = list[y];
                        list[y] = list[i];
                        list[i] = pivot;
                    }

                    y++;
                }
            }
        }

        //sort the filenames now
        quickSortByName(list, lastDirectory, (list.length - 1));
	}
	
	
	
	   private void quickSortByName(FileModel[] list, int left, int right) {
        if (right <= left) {
            return;
        } else if (left == (right - 1)) {
            //only two elements
            if (list[left].getName().compareTo(list[right].getName()) > 0) {
                //change
                FileModel pivot = list[left];
                list[left] = list[right];
                list[right] = pivot;

                return;
            }
        }

        //create two partitionens
        int center = (right + left) / 2;
        FileModel pivot = list[center];
        int i = left;
        int j = right;

        do {
            while ((list[i].getName().compareTo(pivot.getName()) < 0)
                    && (i < right)) {
                i++;
            }

            while ((list[j].getName().compareTo(pivot.getName()) > 0)
                    && (j > left)) {
                j--;
            }

            if (i <= j) {
                FileModel cache = list[i];
                list[i] = list[j];
                list[j] = cache;
                i++;
                j--;
            }
        } while (i <= j);

        //test for degeneration
        if (j < left) {
            list[center] = list[left];
            list[left] = pivot;
            i++;
            j++;
        }

        quickSortByName(list, left, j);
        quickSortByName(list, i, right);
    }

}
