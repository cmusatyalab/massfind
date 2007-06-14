package edu.cmu.cs.diamond.rsna2007;

import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

import edu.cmu.cs.diamond.opendiamond.Result;
import edu.cmu.cs.diamond.opendiamond.Search;
import edu.cmu.cs.diamond.opendiamond.SearchEvent;
import edu.cmu.cs.diamond.opendiamond.SearchEventListener;

public class SearchModel extends DefaultListModel implements
        SearchEventListener {
    protected volatile boolean running;

    final protected Search search;

    final protected int limit;

    final protected Object lock = new Object();

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
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                int i = 0;
                while (running && i < SearchModel.this.limit) {
                    // pick one off
                    try {
                        final Result r = SearchModel.this.search
                                .getNextResult();
                        if (r == null) {
                            break;
                        }

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                addElement(r);
                            }
                        });
                    } catch (InterruptedException e) {
                    } finally {
                        running = false;
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void searchStarted(SearchEvent e) {
        synchronized (lock) {
            running = true;
            lock.notify();
        }
    }

    public void searchStopped(SearchEvent e) {
        running = false;
    }
}
