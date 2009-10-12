/*
 * MassFind 2: A Diamond application for exploration of breast tumors
 *
 * Copyright (c) 2007-2008 Carnegie Mellon University. All rights reserved.
 * Additional copyrights may be listed below.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution in the file named LICENSE.
 *
 * Technical and financial contributors are listed in the file named
 * CREDITS.
 */

package edu.cmu.cs.diamond.massfind2;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

import edu.cmu.cs.diamond.opendiamond.Result;
import edu.cmu.cs.diamond.opendiamond.Search;

final public class SearchModel extends AbstractListModel {
    final protected Search search;

    final protected int limit;

    final protected List<MassResult> list = new LinkedList<MassResult>();

    public SearchModel(Search search, int limit) {
        this.search = search;
        this.limit = limit;

        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    int i = 0;
                    while (i < SearchModel.this.limit) {
                        final Result r = SearchModel.this.search
                                .getNextResult();
                        if (r == null) {
                            break;
                        }

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                System.out.println(" *** adding " + r);
                                list.add(new MassResult(r, 256, 4, 8));
                                int index = list.size();
                                fireIntervalAdded(SearchModel.this, index,
                                        index);
                            }
                        });
                        i++;
                    }
                } catch (InterruptedException e) {
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("search done");
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    final protected static Comparator<MassResult> comparator = new Comparator<MassResult>() {
        public int compare(MassResult o1, MassResult o2) {
            return Integer.valueOf(o2.getSimilarity()).compareTo(
                    Integer.valueOf(o1.getSimilarity()));
        }
    };

    public void reorder() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Collections.sort(list, comparator);
                fireContentsChanged(SearchModel.this, 0, list.size());
            }
        });
    }

    public Object getElementAt(int index) {
        return list.get(index);
    }

    public int getSize() {
        return list.size();
    }
}
