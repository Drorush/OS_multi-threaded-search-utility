package multithreaded_search_utility;
import java.io.File;

public class Scouter implements Runnable {
    private SynchronizedQueue<java.io.File> queue;
    private java.io.File rootDir;

    public void run()
    {
        // registering to directory queue
        this.queue.registerProducer();
        File[] fList = rootDir.listFiles();
        for (File file : fList)
        {
            if(file.isDirectory())
            {
                insertAllSubdirectories(file);
            }
        }



        // unregisters from directory queue
        this.queue.unregisterProducer();
    }

    public Scouter(SynchronizedQueue<File> directoryQueue, File root)
    {
        this.queue = directoryQueue;
        this.rootDir = root;
    }

    private void insertAllSubdirectories(File dir) {
        /* base case */
        if (!hasMoreDirectories(dir)) {
            try {
                queue.enqueue(dir);
            } catch (InterruptedException e) {
                System.out.println("Caught interrupted exception !! " + dir.toString());
            }
        } else {
            File[] fList = dir.listFiles();
            for (File file : fList) {
                if (file.isDirectory()) {
                    insertAllSubdirectories(file);
                }
            }
            try
            {
                queue.enqueue(dir);
            }
            catch(InterruptedException e)
            {
                System.out.println("caught exp");
            }

        }
    }


    private boolean hasMoreDirectories(File dir)
    {
        File[] fList = dir.listFiles();
        for (File file : fList)
        {
            if (file.isDirectory())
            {
                return true;
            }
        }

        return false;
    }
}
