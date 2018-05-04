package multithreaded_search_utility;

import java.io.*;

public class Copier implements Runnable {
    private static final int COPY_BUFFER_SIZE = 4096;
    private File dest;
    private SynchronizedQueue<File> resQue;

    public void run()
    {
        while (!resQue.isEmpty())
        {
            try
            {
                File fileToCopy = resQue.dequeue();
                FileInputStream fis = new FileInputStream(fileToCopy);
                File newFile = new File(dest, fileToCopy.getName());
                FileOutputStream fos = new FileOutputStream(newFile);
                byte[] buffer = new byte[COPY_BUFFER_SIZE];
                int length;
                while ((length = fis.read(buffer)) != -1)
                {
                    fos.write(buffer, 0, length);
                }

                    fos.close();
                    fis.close();
            }
            catch (IOException ex)
            {
                System.out.println("caught IOException");
            }
            catch (InterruptedException e)
            {
                System.out.println("Caught interrupted exception !!!");
            }
        }

    }

    public Copier(File destination, SynchronizedQueue<File> resultsQueue)
    {
        this.dest = destination;
        this.resQue = resultsQueue;
    }
}
