package org.uma.ed.datastructures.priorityqueue;

import java.util.Comparator;

/**
 * Priority queue implemented as a sorted linked structure.
 *
 * @param <T> Type of elements.
 */
public class LinkedPriorityQueue<T> extends AbstractPriorityQueue<T> implements PriorityQueue<T> {
  private static final class Node<E> {
    E element;
    Node<E> next;

    Node(E element, Node<E> next) {
      this.element = element;
      this.next = next;
    }
  }

  /**
   * Comparator to establish the priority of elements.
   */
  private final Comparator<T> comparator;

  /**
   * Reference to first node in linked structure.
   */
  private Node<T> first;

  /**
   * Number of elements in priority queue.
   */
  private int size;

  /* INVARIANT:
   * - `size` is number of elements in priority queue.
   * - nodes are sorted by their priorities, which are established by `comparator`.
   * - `first` is a reference to the node storing the element with the highest priority or null if dictionary is empty.
   * - each node contains a reference to the next node or null if it is the last node.
   */

  private LinkedPriorityQueue(Comparator<T> comparator, Node<T> first, int size) {
    this.comparator = comparator;
    this.first = first;
    this.size = size;
  }
  
  /**
   * Creates an empty queue.
   */
  public LinkedPriorityQueue(Comparator<T> comparator) {
    this(comparator, null, 0);
  }

  public static <T> LinkedPriorityQueue<T> empty(Comparator<T> comparator) {
    return new LinkedPriorityQueue<>(comparator);
  }

  public static <T extends Comparable<? super T>> LinkedPriorityQueue<T> empty() {
    return LinkedPriorityQueue.<T>empty(Comparator.naturalOrder());
  }

  @SafeVarargs
  public static <T> LinkedPriorityQueue<T> of(Comparator<T> comparator, T... elements) {
    LinkedPriorityQueue<T> queue = LinkedPriorityQueue.empty(comparator);
    for(T elem : elements)
      queue.enqueue(elem);
    return queue;
  }

  @SafeVarargs
  public static <T extends Comparable<? super T>> LinkedPriorityQueue<T> of(T... elements) {
    return LinkedPriorityQueue.of(Comparator.naturalOrder(), elements);
  }

  public static <T> LinkedPriorityQueue<T> from(Comparator<T> comparator, Iterable<T> iterable) {
    LinkedPriorityQueue<T> queue = LinkedPriorityQueue.empty(comparator);
    for(T elem : iterable)
      queue.enqueue(elem);
    return queue;
  }

  public static <T extends Comparable<? super T>> LinkedPriorityQueue<T> from(Iterable<T> iterable) {
    return LinkedPriorityQueue.from(Comparator.naturalOrder(), iterable);
  }

  public static <T> LinkedPriorityQueue<T> copyOf(LinkedPriorityQueue<T> queue) {
    return new LinkedPriorityQueue<>(queue.comparator, copyOf(queue.first), queue.size);
  }
  
  private static <T> Node<T> copyOf(Node<T> node) {
    if (node == null) {
      return null;
    } else {
      return new Node<>(node.element, copyOf(node.next));
    }
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  public Comparator<T> comparator() {
    return comparator;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  @Override
  public int size() {
    return size;
  }

  /**
   * {@inheritDoc} Position of new element in queue depends on its priority. The less the value of the element, the
   * higher its priority.
   * <p> Time complexity: O(n)
   */
  @Override
  public void enqueue(T element) {
    if(element == null) {
        return;
    }

    //Si la lista está vacia:
    if(isEmpty()) {
        first = new Node<>(element, null);
        size++;
        return;
    }

      //Si el nuevo elemento es menor (va antes) que el primero:
      if(comparator().compare(first.element, element) > 0) {
          Node<T> nuevo = new Node<>(element, first);
          first = nuevo;
          size++;
          return;
      }

    //Recorremos cada nodo buscando el sitio donde iría el nuevo elemento:
    Node<T> actual = first.next;
    Node<T> anterior = first;

    while(actual != null) {
        if(comparator().compare(actual.element, element) >= 0) {
            Node<T> nuevo = new Node<>(element, actual);
            anterior.next = nuevo;
            size++;
            return;
        } else {
            anterior = actual;
            actual = actual.next;
        }
    }

    //Si hemos llegado al final y no va antes que nadie, significa que va el ultimo:
    Node<T> nuevo = new Node<>(element, null);
    anterior.next = nuevo;
    size++;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   *
   * @throws <code>EmptyQueueException</code> if queue stores no element.
   */
  @Override
  public T first() {
      if(isEmpty()) {
          throw new EmptyPriorityQueueException("first on empty priority queue");
      }
    return first.element;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   *
   * @throws <code>EmptyPriorityQueueException</code> if queue stores no element.
   */
  @Override
  public void dequeue() {
    if(isEmpty()) {
        throw new EmptyPriorityQueueException("dequeue on empty priority queue");
    }

    first = first.next;
    size--;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  @Override
  public void clear() {
    first = null;
    size = 0;
  }

  /**
   * A protected iterable over elements in this priority queue.
   *
   * @return An iterable over elements in this priority queue.
   */
  protected Iterable<T> elements() {
    return () -> new java.util.Iterator<>() {
      Node<T> current = first;

      public boolean hasNext() {
        return current != null;
      }

      public T next() {
        if (!hasNext()) {
          throw new java.util.NoSuchElementException();
        }
        T element = current.element;
        current = current.next;
        return element;
      }
    };
  }
}
