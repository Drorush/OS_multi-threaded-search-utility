package multithreaded_search_utility;

/**
 * A synchronized bounded-size queue for multithreaded producer-consumer applications.
 * 
 * @param <T> Type of data items
 */
public class SynchronizedQueue<T> {

	private T[] buffer;
	private int producers;
	private int front;
	private int rear;
	private int currentSize;
	private int capacity;
	
	/**
	 * Constructor. Allocates a buffer (an array) with the given capacity and
	 * resets pointers and counters.
	 * @param capacity Buffer capacity
	 */
	@SuppressWarnings("unchecked")
	public SynchronizedQueue(int capacity) {
		this.buffer = (T[])(new Object[capacity]);
		this.producers = 0;
		this.capacity = capacity;
		this.front = -1;
		this.rear = -1;
		this.currentSize = 0;
	}
	
	/**
	 * Dequeues the first item from the queue and returns it.
	 * If the queue is empty but producers are still registered to this queue, 
	 * this method blocks until some item is available.
	 * If the queue is empty and no more items are planned to be added to this 
	 * queue (because no producers are registered), this method returns null.
	 * 
	 * @return The first item, or null if there are no more items
	 * @see #registerProducer()
	 * @see #unregisterProducer()
	 */
	public synchronized T dequeue() throws InterruptedException{
		while (this.currentSize == 0 && this.producers > 0)
		{
			wait();
		}

		if (this.currentSize == 0 && this.producers == 0)
		{
			return null;
		}
		else
		{
			T item = buffer[front];
			buffer[front] = null;
			if (front == rear)
			{
				front = -1;
				rear = -1;
			}
			else if (front == capacity - 1)
			{
				front = 0;
			}
			else
			{
				front++;
			}

			this.currentSize--;
			notifyAll();
			return item;
		}
	}

	public synchronized boolean isEmpty()
	{
		return (this.currentSize == 0 && producers == 0);
	}

	/**
	 * Enqueues an item to the end of this queue. If the queue is full, this 
	 * method blocks until some space becomes available.
	 * 
	 * @param item Item to enqueue
	 */
	public synchronized void enqueue(T item) throws InterruptedException{
		while (this.currentSize == capacity) /* queue is full */
		{
			wait();
		}

		if (front == -1) /* insert first element */
		{
			front = rear = 0;
			buffer[rear] = item;
		}
		else if (rear == capacity - 1 && front != 0)
		{
			rear = 0;
			buffer[rear] = item;
		}
		else
		{
			rear++;
			buffer[rear] = item;
		}

		currentSize++;
		notifyAll();
	}

	/**
	 * Returns the capacity of this queue
	 * @return queue capacity
	 */
	public int getCapacity() {

		return capacity;
	}

	/**
	 * Returns the current size of the queue (number of elements in it)
	 * @return queue size
	 */
	public synchronized int getSize() {

		return currentSize;
	}

	public synchronized int getNumOfProducers()
	{
		return producers;
	}
	
	/**
	 * Registers a producer to this queue. This method actually increases the
	 * internal producers counter of this queue by 1. This counter is used to
	 * determine whether the queue is still active and to avoid blocking of
	 * consumer threads that try to dequeue elements from an empty queue, when
	 * no producer is expected to add any more items.
	 * Every producer of this queue must call this method before starting to 
	 * enqueue items, and must also call <see>{@link #unregisterProducer()}</see> when
	 * finishes to enqueue all items.
	 * 
	 * @see #dequeue()
	 * @see #unregisterProducer()
	 */
	public synchronized void registerProducer() {
		this.producers++;
	}

	/**
	 * Unregisters a producer from this queue. See <see>{@link #registerProducer()}</see>.
	 * 
	 * @see #dequeue()
	 * @see #registerProducer()
	 */
	public synchronized void unregisterProducer() {
		this.producers--;
		notifyAll();
	}
}
