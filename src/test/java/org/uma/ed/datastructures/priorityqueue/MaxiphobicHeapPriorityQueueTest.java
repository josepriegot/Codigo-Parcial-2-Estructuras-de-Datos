package org.uma.ed.datastructures.priorityqueue;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Test suite for MaxiphobicHeapPriorityQueue using JUnit 5
 *
 * @author Test Suite
 */
@DisplayName("MaxiphobicHeapPriorityQueue Tests")
class MaxiphobicHeapPriorityQueueTest {

  private MaxiphobicHeapPriorityQueue<Integer> pq;

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Empty queue with natural order should be created")
    void testEmptyNaturalOrder() {
      pq = MaxiphobicHeapPriorityQueue.empty();

      assertAll("Empty queue properties",
              () -> assertTrue(pq.isEmpty()),
              () -> assertEquals(0, pq.size()),
              () -> assertNotNull(pq.comparator())
      );
    }

    @Test
    @DisplayName("Empty queue with custom comparator should be created")
    void testEmptyCustomComparator() {
      pq = MaxiphobicHeapPriorityQueue.empty(Comparator.<Integer>reverseOrder());

      assertAll("Custom comparator queue",
              () -> assertTrue(pq.isEmpty()),
              () -> assertEquals(0, pq.size()),
              () -> assertEquals(Comparator.reverseOrder(), pq.comparator())
      );
    }

    @Test
    @DisplayName("Create queue with of() method - natural order")
    void testOfNaturalOrder() {
      pq = MaxiphobicHeapPriorityQueue.of(5, 3, 8, 1, 9, 2);

      assertAll("Queue from varargs",
              () -> assertFalse(pq.isEmpty()),
              () -> assertEquals(6, pq.size()),
              () -> assertEquals(1, pq.first())
      );
    }

    @Test
    @DisplayName("Create queue with of() method - reverse order")
    void testOfReverseOrder() {
      pq = MaxiphobicHeapPriorityQueue.of(Comparator.reverseOrder(), 5, 3, 8, 1, 9, 2);

      assertAll("Queue with reverse comparator",
              () -> assertFalse(pq.isEmpty()),
              () -> assertEquals(6, pq.size()),
              () -> assertEquals(9, pq.first())
      );
    }

    @Test
    @DisplayName("Create queue from iterable - natural order")
    void testFromIterableNaturalOrder() {
      List<Integer> list = List.of(5, 3, 8, 1, 9, 2);
      pq = MaxiphobicHeapPriorityQueue.from(list);

      assertAll("Queue from iterable",
              () -> assertEquals(6, pq.size()),
              () -> assertEquals(1, pq.first())
      );
    }

    @Test
    @DisplayName("Create queue from iterable - custom comparator")
    void testFromIterableCustomComparator() {
      List<Integer> list = List.of(5, 3, 8, 1, 9, 2);
      pq = MaxiphobicHeapPriorityQueue.from(Comparator.reverseOrder(), list);

      assertAll("Queue from iterable with comparator",
              () -> assertEquals(6, pq.size()),
              () -> assertEquals(9, pq.first())
      );
    }

    @Test
    @DisplayName("CopyOf creates independent copy")
    void testCopyOf() {
      MaxiphobicHeapPriorityQueue<Integer> original =
              MaxiphobicHeapPriorityQueue.of(5, 3, 8, 1, 9, 2);
      MaxiphobicHeapPriorityQueue<Integer> copy =
              MaxiphobicHeapPriorityQueue.copyOf(original);

      assertAll("Copy independence",
              () -> assertEquals(original.size(), copy.size()),
              () -> assertEquals(original.first(), copy.first())
      );

      // Verify independence
      copy.dequeue();
      assertNotEquals(original.size(), copy.size(),
              "Copy should be independent from original");
    }
  }

  @Nested
  @DisplayName("Enqueue Operation Tests")
  class EnqueueTests {

    @BeforeEach
    void setUp() {
      pq = MaxiphobicHeapPriorityQueue.empty();
    }

    @Test
    @DisplayName("Enqueue increases size correctly")
    void testEnqueueSize() {
      pq.enqueue(5);
      assertEquals(1, pq.size());

      pq.enqueue(3);
      assertEquals(2, pq.size());

      pq.enqueue(8);
      assertEquals(3, pq.size());
    }

    @Test
    @DisplayName("Enqueue maintains minimum at front")
    void testEnqueueMinimum() {
      pq.enqueue(5);
      assertEquals(5, pq.first());

      pq.enqueue(3);
      assertEquals(3, pq.first());

      pq.enqueue(8);
      assertEquals(3, pq.first());

      pq.enqueue(1);
      assertEquals(1, pq.first());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 50, 100})
    @DisplayName("Enqueue multiple elements")
    void testEnqueueMultiple(int count) {
      for (int i = count; i > 0; i--) {
        pq.enqueue(i);
      }

      assertEquals(count, pq.size());
      assertEquals(1, pq.first());
    }
  }

  @Nested
  @DisplayName("First Operation Tests")
  class FirstTests {

    @Test
    @DisplayName("First returns minimum without removing")
    void testFirstDoesNotRemove() {
      pq = MaxiphobicHeapPriorityQueue.of(5, 3, 8, 1, 9, 2);

      assertEquals(1, pq.first());
      assertEquals(6, pq.size(), "Size should not change");
      assertEquals(1, pq.first(), "Should still return same element");
    }

    @Test
    @DisplayName("First throws exception on empty queue")
    void testFirstOnEmpty() {
      pq = MaxiphobicHeapPriorityQueue.empty();

      EmptyPriorityQueueException exception = assertThrows(
              EmptyPriorityQueueException.class,
              () -> pq.first()
      );

      assertTrue(exception.getMessage().contains("empty"));
    }
  }

  @Nested
  @DisplayName("Dequeue Operation Tests")
  class DequeueTests {

    @Test
    @DisplayName("Dequeue removes minimum element")
    void testDequeueRemovesMin() {
      pq = MaxiphobicHeapPriorityQueue.of(5, 3, 8, 1, 9, 2);

      assertEquals(1, pq.first());
      pq.dequeue();

      assertAll("After dequeue",
              () -> assertEquals(5, pq.size()),
              () -> assertEquals(2, pq.first())
      );
    }

    @Test
    @DisplayName("Dequeue throws exception on empty queue")
    void testDequeueOnEmpty() {
      pq = MaxiphobicHeapPriorityQueue.empty();

      EmptyPriorityQueueException exception = assertThrows(
              EmptyPriorityQueueException.class,
              () -> pq.dequeue()
      );

      assertTrue(exception.getMessage().contains("empty"));
    }

    @Test
    @DisplayName("Multiple dequeues maintain sorted order")
    void testMultipleDequeue() {
      pq = MaxiphobicHeapPriorityQueue.of(5, 3, 8, 1, 9, 2, 4, 7, 6);

      List<Integer> extracted = new ArrayList<>();
      while (!pq.isEmpty()) {
        extracted.add(pq.first());
        pq.dequeue();
      }

      assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), extracted);
    }

    @Test
    @DisplayName("Dequeue until empty")
    void testDequeueUntilEmpty() {
      pq = MaxiphobicHeapPriorityQueue.of(5, 3, 8);

      assertFalse(pq.isEmpty());
      pq.dequeue();
      assertFalse(pq.isEmpty());
      pq.dequeue();
      assertFalse(pq.isEmpty());
      pq.dequeue();
      assertTrue(pq.isEmpty());
    }
  }

  @Nested
  @DisplayName("Clear and Empty Tests")
  class ClearTests {

    @Test
    @DisplayName("Clear empties non-empty queue")
    void testClear() {
      pq = MaxiphobicHeapPriorityQueue.of(5, 3, 8, 1, 9, 2);

      assertFalse(pq.isEmpty());
      pq.clear();

      assertAll("After clear",
              () -> assertTrue(pq.isEmpty()),
              () -> assertEquals(0, pq.size())
      );
    }

    @Test
    @DisplayName("Clear on empty queue is safe")
    void testClearOnEmpty() {
      pq = MaxiphobicHeapPriorityQueue.empty();

      assertDoesNotThrow(() -> pq.clear());
      assertTrue(pq.isEmpty());
    }

    @Test
    @DisplayName("isEmpty returns correct state")
    void testIsEmpty() {
      pq = MaxiphobicHeapPriorityQueue.empty();

      assertTrue(pq.isEmpty());
      pq.enqueue(5);
      assertFalse(pq.isEmpty());
      pq.dequeue();
      assertTrue(pq.isEmpty());
    }
  }

  @Nested
  @DisplayName("Size Tests")
  class SizeTests {

    @BeforeEach
    void setUp() {
      pq = MaxiphobicHeapPriorityQueue.empty();
    }

    @Test
    @DisplayName("Size reflects queue operations")
    void testSize() {
      assertEquals(0, pq.size());

      pq.enqueue(5);
      assertEquals(1, pq.size());

      pq.enqueue(3);
      assertEquals(2, pq.size());

      pq.dequeue();
      assertEquals(1, pq.size());

      pq.dequeue();
      assertEquals(0, pq.size());
    }

    @ParameterizedTest
    @CsvSource({
            "1, 1",
            "5, 5",
            "10, 10",
            "100, 100"
    })
    @DisplayName("Size after multiple enqueues")
    void testSizeAfterEnqueues(int count, int expectedSize) {
      IntStream.range(0, count).forEach(pq::enqueue);
      assertEquals(expectedSize, pq.size());
    }
  }

  @Nested
  @DisplayName("Special Cases Tests")
  class SpecialCasesTests {

    @Test
    @DisplayName("Handle duplicate elements correctly")
    void testDuplicates() {
      pq = MaxiphobicHeapPriorityQueue.of(5, 3, 3, 1, 5, 1);

      assertAll("Duplicates handling",
              () -> assertEquals(6, pq.size()),
              () -> assertEquals(1, pq.first())
      );

      pq.dequeue();
      assertEquals(1, pq.first(), "Should return second 1");
    }

    @Test
    @DisplayName("Single element queue operations")
    void testSingleElement() {
      pq = MaxiphobicHeapPriorityQueue.of(42);

      assertAll("Single element",
              () -> assertEquals(1, pq.size()),
              () -> assertEquals(42, pq.first())
      );

      pq.dequeue();
      assertTrue(pq.isEmpty());
    }

    @Test
    @DisplayName("Interleaved enqueue and dequeue operations")
    void testInterleavedOperations() {
      pq = MaxiphobicHeapPriorityQueue.empty();

      pq.enqueue(5);
      pq.enqueue(3);
      assertEquals(3, pq.first());

      pq.dequeue();
      pq.enqueue(1);
      assertEquals(1, pq.first());

      pq.enqueue(7);
      pq.dequeue();
      assertEquals(5, pq.first());
    }
  }

  @Nested
  @DisplayName("Different Data Types Tests")
  class DataTypeTests {

    @Test
    @DisplayName("Priority queue with strings")
    void testWithStrings() {
      MaxiphobicHeapPriorityQueue<String> stringPQ =
              MaxiphobicHeapPriorityQueue.of("dog", "cat", "elephant", "ant", "bear");

      assertEquals("ant", stringPQ.first());
      stringPQ.dequeue();
      assertEquals("bear", stringPQ.first());
    }

    @Test
    @DisplayName("Priority queue with custom comparable objects")
    void testWithCustomObjects() {
      record Person(String name, int age) implements Comparable<Person> {
        @Override
        public int compareTo(Person other) {
          return Integer.compare(this.age, other.age);
        }
      }

      MaxiphobicHeapPriorityQueue<Person> personPQ = MaxiphobicHeapPriorityQueue.of(
              new Person("Alice", 30),
              new Person("Bob", 25),
              new Person("Charlie", 35)
      );

      assertAll("Person queue",
              () -> assertEquals("Bob", personPQ.first().name()),
              () -> assertEquals(25, personPQ.first().age())
      );
    }

    @Test
    @DisplayName("Priority queue with custom comparator for objects")
    void testCustomComparatorForObjects() {
      record Item(String name, double price) {}

      Comparator<Item> byPrice = Comparator.comparingDouble(Item::price);
      MaxiphobicHeapPriorityQueue<Item> itemPQ =
              MaxiphobicHeapPriorityQueue.empty(byPrice);

      itemPQ.enqueue(new Item("Laptop", 999.99));
      itemPQ.enqueue(new Item("Mouse", 25.50));
      itemPQ.enqueue(new Item("Keyboard", 75.00));

      assertEquals("Mouse", itemPQ.first().name());
      assertEquals(25.50, itemPQ.first().price(), 0.01);
    }
  }

  @Nested
  @DisplayName("Stress and Performance Tests")
  class StressTests {

    @Test
    @DisplayName("Large queue maintains order - 1000 elements")
    @Tag("slow")
    void testLargeQueue() {
      pq = MaxiphobicHeapPriorityQueue.empty();

      // Insert 1000 elements in reverse order
      for (int i = 1000; i > 0; i--) {
        pq.enqueue(i);
      }

      assertEquals(1000, pq.size());

      // Verify they come out in sorted order
      int previous = 0;
      while (!pq.isEmpty()) {
        int current = pq.first();
        assertTrue(current > previous,
                "Elements should be in ascending order");
        previous = current;
        pq.dequeue();
      }
    }

    @RepeatedTest(10)
    @DisplayName("Random operations maintain invariants")
    void testRandomOperations() {
      pq = MaxiphobicHeapPriorityQueue.empty();

      // Random mix of operations
      for (int i = 0; i < 50; i++) {
        pq.enqueue((int) (Math.random() * 100));
      }

      for (int i = 0; i < 25; i++) {
        if (!pq.isEmpty()) {
          pq.dequeue();
        }
      }

      // Should still maintain heap property
      if (pq.size() > 1) {
        int first = pq.first();
        pq.dequeue();
        int second = pq.first();
        assertTrue(first <= second,
                "Heap property should be maintained");
      }
    }
  }

  @Nested
  @DisplayName("Edge Cases Tests")
  class EdgeCasesTests {

    @Test
    @DisplayName("Operations after clear")
    void testOperationsAfterClear() {
      pq = MaxiphobicHeapPriorityQueue.of(5, 3, 8);
      pq.clear();

      assertDoesNotThrow(() -> pq.enqueue(10));
      assertEquals(1, pq.size());
      assertEquals(10, pq.first());
    }

    @Test
    @DisplayName("Queue with negative numbers")
    void testNegativeNumbers() {
      pq = MaxiphobicHeapPriorityQueue.of(-5, -3, -8, -1, 0, 2);

      assertEquals(-8, pq.first());
      pq.dequeue();
      assertEquals(-5, pq.first());
    }

    @Test
    @DisplayName("Queue with all same elements")
    void testAllSameElements() {
      pq = MaxiphobicHeapPriorityQueue.of(5, 5, 5, 5, 5);

      assertEquals(5, pq.size());

      while (!pq.isEmpty()) {
        assertEquals(5, pq.first());
        pq.dequeue();
      }
    }
  }

  @Test
  @DisplayName("toString returns meaningful representation")
  void testToString() {
    pq = MaxiphobicHeapPriorityQueue.of(5, 3, 8, 1);

    String str = pq.toString();
    assertAll("toString output",
            () -> assertNotNull(str),
            () -> assertTrue(str.contains("MaxiphobicHeapPriorityQueue") ||
                    str.length() > 0)
    );
  }
}