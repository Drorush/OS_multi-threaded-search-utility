package multithreaded_search_utility;
import java.io.File;
import java.io.FilenameFilter;

public class Searcher implements Runnable {
    private SynchronizedQueue<File> dirQueue;
    private SynchronizedQueue<File> resQueue;
    private String pattern;

    public void run()
    {
        resQueue.registerProducer();
        while (!dirQueue.isEmpty())
        {
            try
            {
                File dir = dirQueue.dequeue();
                if (dir != null)
                {
                    File[] patternFiles = dir.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.contains(pattern);
                        }
                    });
                    if (patternFiles != null)
                    {
                        for(File file : patternFiles)
                        {
                            resQueue.enqueue(file);
                        }
                    }

                }

            }
            catch(InterruptedException e)
            {
                System.out.println("Caught exception while trying to dequeue");
            }
        }

        resQueue.unregisterProducer();
    }

    public Searcher(String extension, SynchronizedQueue<File> directoryQueue, SynchronizedQueue<File> resultsQueue)
    {
        dirQueue = directoryQueue;
        resQueue = resultsQueue;
        pattern = extension;
    }
}
