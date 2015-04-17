package com.github.reportengine.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.junit.Assert;

import com.google.common.collect.Lists;

/**
 * top n工具类
 * @author andy
 *
 * @param <T>
 */
public class TopN<T>{
	 private final int k;
	  private final Comparator<? super T> queueingComparator;
	  private final Comparator<? super T> sortingComparator;
	  private final Queue<T> queue;

	 TopN(int k, Comparator<? super T> comparator) {
	    Assert.assertTrue(k > 0);
	    this.k = k;
	    Assert.assertNotNull(comparator);
	    this.queueingComparator = queueingComparator(comparator);
	    this.sortingComparator = sortingComparator(comparator);
	    this.queue = new PriorityQueue<T>(k + 1, queueingComparator);
	  }

	  public void put(T item) {
	    if (queue.size() < k) {
	      queue.add(item);
	    } else if (queueingComparator.compare(item, queue.peek()) > 0) {
	      queue.add(item);
	      queue.poll();
	    }
	  }

	  public boolean isEmpty() {
	    return queue.isEmpty();
	  }

	  public int size() {
	    return queue.size();
	  }

	  public List<T> retrieve() {
	    List<T> topItems = Lists.newArrayList(queue);
	    Collections.sort(topItems, sortingComparator);
	    return topItems;
	  }

	  protected T peek() {
	    return queue.peek();
	  }
	  
	  public Comparator<? super T> queueingComparator(Comparator<? super T> stdComparator) {
		    return stdComparator;
		  }

	  public Comparator<? super T> sortingComparator(Comparator<? super T> stdComparator) {
		    return Collections.reverseOrder(stdComparator);
		  }

	  public T smallestGreat() {
		    return peek();
	  }
}
