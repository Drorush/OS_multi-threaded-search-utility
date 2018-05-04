package multithreaded_search_utility;
import java.io.File;

public class DiskSearcher {
    private static final int DIRECTORY_QUEUE_CAPACITY = 50;
    private static final int RESULTS_QUEUE_CAPACITY = 50;

    public static void main(String[] args) throws InterruptedException
        {

        /* get input of the format */
        /* <pattern> <root directory> <destination directory> <#ofsearchers> <#ofcopiers> */
        String pattern = args[0];
        String rootDir = args[1];
        File rootDirectory = new File(rootDir);
        String destDir = args[2];
        File destinationDir = new File(destDir);
        int numOfSearchers = Integer.parseInt(args[3]);
        int numOfCopiers = Integer.parseInt(args[4]);

        SynchronizedQueue<File> directoryQueue = new SynchronizedQueue<>(DIRECTORY_QUEUE_CAPACITY);
        SynchronizedQueue<File> resultsQueue = new SynchronizedQueue<>(RESULTS_QUEUE_CAPACITY);

        Scouter scouter = new Scouter(directoryQueue, rootDirectory);
        Thread scouterThread = new Thread(scouter);
        scouterThread.start();


        Searcher[] searchers = new Searcher[numOfSearchers];
        Thread[] searcherz = new Thread[numOfSearchers];
        for (int i = 0; i < numOfSearchers; i++)
        {
            searchers[i] = new Searcher(pattern, directoryQueue, resultsQueue);
            searcherz[i] = new Thread(searchers[i]);
            searcherz[i].start();
        }

        Copier[] copiers = new Copier[numOfCopiers];
        Thread[] copierz = new Thread[numOfCopiers];
        for (int i = 0; i < numOfCopiers; i++)
        {
            copiers[i] = new Copier(destinationDir, resultsQueue);
            copierz[i] = new Thread(copiers[i]);
            copierz[i].start();
        }

        scouterThread.join();

        for(int i = 0; i < searcherz.length; i++)
        {
            searcherz[i].join();
        }

        for(int i = 0; i < copierz.length; i++)
        {
            copierz[i].join();
        }
    }
}
