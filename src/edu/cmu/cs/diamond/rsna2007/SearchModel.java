package edu.cmu.cs.diamond.rsna2007;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.naming.directory.SearchResult;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

import edu.cmu.cs.diamond.opendiamond.Result;
import edu.cmu.cs.diamond.opendiamond.Search;
import edu.cmu.cs.diamond.opendiamond.SearchEvent;
import edu.cmu.cs.diamond.opendiamond.SearchEventListener;

public class SearchModel extends AbstractListModel implements
        SearchEventListener {
    protected volatile boolean running;

    final protected Search search;

    final protected int limit;

    final protected Object lock = new Object();

    final protected List<MassResult> list = new LinkedList<MassResult>();

    public SearchModel(Search search, int limit) {
        this.search = search;
        this.limit = limit;

        search.addSearchEventListener(this);

        Thread t = new Thread(new Runnable() {
            public void run() {
                // wait for start
                synchronized (lock) {
                    while (!running) {
                        try {
                            System.out.println("waiting for start signal");
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    int i = 0;
                    while (running && i < SearchModel.this.limit) {
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
                } finally {
                    System.out.println("search done");
                    running = false;
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void searchStarted(SearchEvent e) {
        synchronized (lock) {
            System.out.println("sending start notify");
            running = true;
            lock.notify();
        }
    }

    public void searchStopped(SearchEvent e) {
        running = false;
    }

    public void removeSearchListener() {
        search.removeSearchEventListener(this);
    }

    final protected static Comparator<MassResult> comparator = new Comparator<MassResult>() {
        public int compare(MassResult o1, MassResult o2) {
            return new Integer(o2.getSimilarity()).compareTo(new Integer(o1
                    .getSimilarity()));
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
