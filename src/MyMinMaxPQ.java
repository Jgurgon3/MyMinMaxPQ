import java.util.*;

//Jim Gurgone
//CSC 403-501
//Assignment 1 2.4.29 MyMinMaxP!



public class MyMinMaxPQ<Key extends Comparable<Key>> implements Iterable<Key> 
{
	
	private Key[] pq1;                    // store Min items at indices 1 to N
	private Key[] pq2;                    // store Max items at indices 1 to N
	private int N1;                       // number of items on Min priority queue
	private int N2;                       // number of items on Max priority queue
	private Comparator<Key> comparator1;  // Comparator for Min
	private Comparator<Key> comparator2;  // Comparator for Max
	

	private void resize1(final int capacity) 
	{
		assert capacity > N1;
		final Key[] temp = (Key[]) new Comparable[capacity];
		for (int i = 1; i <= N1; i++) temp[i] = pq1[i];
		pq1 = temp;
	}
	
	private void resize2(final int capacity) 
	{
		assert capacity > N2;
		final Key[] temp = (Key[]) new Comparable[capacity];
		for (int i = 1; i <= N2; i++) temp[i] = pq2[i];
		pq2 = temp;
	}
	
	@SuppressWarnings("unchecked")
	/** Create an empty priority queue with the given initial capacity, using the given comparator. */
	public MyMinMaxPQ(final int initCapacity, final Comparator<Key> comparator) 
	{
		pq1 = (Key[]) new Comparable[initCapacity + 1];
		N1 = 0;
		this.comparator1 = comparator;
		pq2 = (Key[]) new Comparable[initCapacity + 1];
		N2 = 0;
		this.comparator2 = comparator;
	}
	
	/** Create an empty priority queue with the given initial capacity. */
	public MyMinMaxPQ(final int initCapacity)           
	{ 
		this(initCapacity, null); 
	}
	/** Create an empty priority queue using the given comparator. */
	public MyMinMaxPQ(final Comparator<Key> comparator) 
	{ 
		this(1, comparator); 
	}
	/** Create an empty priority queue. */
	public MyMinMaxPQ()                           
	{ 
		this(1, null); 
	}

	/**
	 * Create a priority queue with the given items.
	 * Takes time proportional to the number of items using sink-based heap construction.
	 */
	public MyMinMaxPQ(final Key[] keys) 
	{
		this(keys.length, null);
		N1 = keys.length;
		for (int i = 0; i < N1; i++)
			pq1[i+1] = keys[i];
		for (int k = N1/2; k >= 1; k--)
			sink1(k);
		assert isMinHeap1();
		N2 = keys.length;
		for (int i = 0; i < N2; i++)
			pq2[i+1] = keys[i];
		for (int k = N2/2; k >= 1; k--)
			sink2(k);
		assert isMaxHeap();
	}

	/** Is the priority queue empty? */
	public boolean isEmpty1() 
	{ 
		return N1 == 0; 
	}

	/** Return the number of items on the priority queue. */
	public int size1() 
	{ 
		return N1; 
	}

	/**
	 * Return the smallest key on the priority queue.
	 * Throw an exception if the priority queue is empty.
	 */
	public Key min() 
	{
		if (isEmpty1()) 
			throw new RuntimeException("Priority queue underflow");
		return pq1[1];
	}

	/** Add a new key to the priority queue. */
	public void insert1(final Key x) 
	{
		// double size of array if necessary
		if (N1 >= pq1.length - 1) resize1(2 * pq1.length);

		// add x, and percolate it up to maintain heap invariant
		pq1[++N1] = x;
		swim1(N1);
		assert isMinHeap1();
	}

	public boolean isEmpty2() 
	{ 
		return N2 == 0; 
	}

	/** Return the number of items on the priority queue. */
	public int size2() 
	{ 
		return N2; 
	}

	/**
	 * Return the largest key on the priority queue.
	 * Throw an exception if the priority queue is empty.
	 */
	public Key max() 
	{
		if (isEmpty2()) 
			throw new RuntimeException("Priority queue underflow");
		return pq2[1];
	}

	/** Add a new key to the priority queue. */
	public void insert2(final Key x) 
	{
		// double size of array if necessary
		if (N2 >= pq2.length - 1) resize2(2 * pq2.length);

		// add x, and percolate it up to maintain heap invariant
		pq2[++N2] = x;
		swim2(N2);
		assert isMaxHeap();
	}

	/**
	 * Delete and return the smallest key on the priority queue.
	 * Throw an exception if the priority queue is empty.
	 */
	public Key delMin() 
	{
		if (N1 == 0) 
			throw new RuntimeException("Priority queue underflow");
		exch1(1, N1);
		final Key min = pq1[N1--];
		sink1(1);
		pq1[N1+1] = null; // avoid loitering and help with garbage collection
		if ((N1 > 0) && (N1 == (pq1.length - 1) / 4)) resize1(pq1.length / 2);
		assert isMinHeap1();
		return min;
	}

	public Key delMax() 
	{
		if (N2 == 0) 
			throw new RuntimeException("Priority queue underflow");
		final Key max = pq2[1];
		exch2(1, N2--);
		sink2(1);
		pq2[N2+1] = null; // avoid loitering and help with garbage collection
		if ((N2 > 0) && (N2 == (pq2.length - 1) / 4)) resize2(pq2.length / 2);
		assert isMaxHeap();
		return max;
	}


	/***********************************************************************
	 * Helper functions to restore the heap invariant.
	 **********************************************************************/

	private void swim1(int k) 
	{
		while (k > 1 && greater(k/2, k)) 
		{
			exch1(k, k/2);
			k = k/2;
		}
	}

	private void sink1(int k) 
	{
		while (2*k <= N1) 
		{
			int j = 2*k;
			if (j < N1 && greater(j, j+1)) j++;
			if (!greater(k, j)) 
				break;
			exch1(k, j);
			k = j;
		}
	}

	private void swim2(int k) 
	{
		while (k > 1 && less(k/2, k))
		{
			exch2(k, k/2);
			k = k/2;
		}
	}

	private void sink2(int k)
	{
		while (2*k <= N2) 
		{
			int j = 2*k;
			if (j < N2 && less(j, j+1)) j++;
			if (!less(k, j)) 
				break;
			exch2(k, j);
			k = j;
		}
	}

	/***********************************************************************
	 * Helper functions for compares and swaps.
	 **********************************************************************/
	private boolean greater(final int i, final int j)
	{
		if (comparator1 == null) 
		{
			return pq1[i].compareTo(pq1[j]) > 0;
		}
		else 
		{
			return comparator1.compare(pq1[i], pq1[j]) > 0;
		}
	}

	private void exch1(final int i, final int j) 
	{
		final Key swap = pq1[i];
		pq1[i] = pq1[j];
		pq1[j] = swap;
	}

	// is pq[1..N] a min heap?
	private boolean isMinHeap1() 
	{
		return isMinHeap(1);
	}

	// is subtree of pq[1..N] rooted at k a min heap?
	private boolean isMinHeap(final int k) 
	{
		if (k > N1) return true;
		final int left = 2*k, right = 2*k + 1;
		if (left  <= N1 && greater(k, left))  
			return false;
		if (right <= N1 && greater(k, right))
			return false;
		return isMinHeap(left) && isMinHeap(right);
	}

	private boolean less(final int i, final int j) 
	{
		if (comparator2 == null) 
		{
			return pq2[i].compareTo(pq2[j]) < 0;
		}
		else 
		{
			return comparator2.compare(pq2[i], pq2[j]) < 0;
		}
	}

	private void exch2(final int i, final int j) 
	{
		final Key swap = pq2[i];
		pq2[i] = pq2[j];
		pq2[j] = swap;
	}

	// is pq[1..N] a max heap?
	private boolean isMaxHeap() 
	{
		return isMaxHeap(1);
	}

	// is subtree of pq[1..N] rooted at k a max heap?
	private boolean isMaxHeap(final int k) 
	{
		if (k > N2) return true;
		final int left = 2*k, right = 2*k + 1;
		if (left  <= N2 && less(k, left))  
			return false;
		if (right <= N2 && less(k, right)) 
			return false;
		return isMaxHeap(left) && isMaxHeap(right);
	}

	/***********************************************************************
	 * Iterator
	 **********************************************************************/

	/**
	 * Return an iterator that iterates over all of the keys on the priority queue
	 * in ascending order.
	 * <p>
	 * The iterator doesn't implement <tt>remove()</tt> since it's optional.
	 */
	public Iterator<Key> iterator() { return new HeapIterator1(); }
	

	private class HeapIterator1 implements Iterator<Key> 
	{
		// create a new pq
		private MinPQ<Key> copy;

		// add all items to copy of heap
		// takes linear time since already in heap order so no keys move
		public HeapIterator1() {
			if (comparator1 == null) 
				copy = new MinPQ<Key>(size1());
			else                    
				copy = new MinPQ<Key>(size1(), comparator1);
			for (int i = 1; i <= N1; i++)
				copy.insert(pq1[i]);
		}

		public boolean hasNext()  
		{ 
			return !copy.isEmpty();                     
		}
		public void remove()      
		{ 
			throw new UnsupportedOperationException();  
		}

		public Key next()
		{
			if (!hasNext()) throw new NoSuchElementException();
			return copy.delMin();
		}
	}
	public Iterator<Key> iterator2() { return new HeapIterator2(); }
	private class HeapIterator2 implements Iterator<Key> 
	{
		// create a new pq
		private MaxPQ<Key> copy;

		// add all items to copy of heap
		// takes linear time since already in heap order so no keys move
		public HeapIterator2() 
		{
			if (comparator2 == null) 
				copy = new MaxPQ<Key>(size2());
			else                   
				copy = new MaxPQ<Key>(size2(), comparator2);
			for (int i = 1; i <= N2; i++)
				copy.insert(pq2[i]);
		}

		public boolean hasNext()  { return !copy.isEmpty();                     }
		public void remove()      { throw new UnsupportedOperationException();  }

		public Key next() 
		{
			if (!hasNext()) 
				throw new NoSuchElementException();
			return copy.delMax();
		}
	}
}	

