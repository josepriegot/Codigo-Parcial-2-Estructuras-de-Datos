package org.uma.ed.datastructures.heap;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MaxiphobicHeap tests (JUnit 5)")
class MaxiphobicHeapTest {

    @Test
    @DisplayName("empty() crea un heap vacío con orden natural")
    void emptyNatural() {
        MaxiphobicHeap<Integer> h = MaxiphobicHeap.empty();
        assertTrue(h.isEmpty());
        assertEquals(0, h.size());
        assertNotNull(h.comparator());
    }

    @Test
    @DisplayName("empty(Comparator) crea un heap vacío con el comparador dado")
    void emptyWithComparator() {
        Comparator<Integer> rev = Comparator.reverseOrder();
        MaxiphobicHeap<Integer> h = MaxiphobicHeap.empty(rev);
        assertTrue(h.isEmpty());
        assertEquals(0, h.size());
        assertSame(rev, h.comparator());
    }

    @Test
    @DisplayName("insert + minimum respetan el orden natural")
    void insertAndMinimumNatural() {
        MaxiphobicHeap<Integer> h = MaxiphobicHeap.empty();
        h.insert(7);
        h.insert(2);
        h.insert(5);
        assertFalse(h.isEmpty());
        assertEquals(3, h.size());
        assertEquals(2, h.minimum());
    }

    @Test
    @DisplayName("deleteMinimum elimina en orden creciente")
    void deleteMinimumSequence() {
        MaxiphobicHeap<Integer> h = MaxiphobicHeap.of(5, 1, 4, 1, 3, 2);
        List<Integer> extracted = new ArrayList<>();
        while (!h.isEmpty()) {
            extracted.add(h.minimum());
            h.deleteMinimum();
        }
        assertEquals(Arrays.asList(1, 1, 2, 3, 4, 5), extracted);
        assertTrue(h.isEmpty());
        assertEquals(0, h.size());
    }

    @Test
    @DisplayName("from(Iterable) construye correctamente y size() es O(1) lógico")
    void fromIterable() {
        List<Integer> data = Arrays.asList(9, 8, 7, 7, 3, 4, 2);
        MaxiphobicHeap<Integer> h = MaxiphobicHeap.from(data);
        assertEquals(data.size(), h.size());
        // Comprobación de orden de extracción
        int prev = Integer.MIN_VALUE;
        while (!h.isEmpty()) {
            int m = h.minimum();
            assertTrue(m >= prev);
            prev = m;
            h.deleteMinimum();
        }
    }

    @Test
    @DisplayName("of(T...) en O(n) produce la misma secuencia que PriorityQueue")
    void ofVarargsMatchesPriorityQueue() {
        Integer[] elems = { 10, -1, 5, 5, 2, 9, 0, 3, 3, 3 };
        MaxiphobicHeap<Integer> h = MaxiphobicHeap.of(elems);
        PriorityQueue<Integer> pq = new PriorityQueue<>(Arrays.asList(elems));
        while (!pq.isEmpty()) {
            assertFalse(h.isEmpty());
            assertEquals(pq.peek(), h.minimum());
            pq.poll();
            h.deleteMinimum();
        }
        assertTrue(h.isEmpty());
    }

    @Test
    @DisplayName("Comparador inverso: minimum() según el comparador (máximo numérico)")
    void reversedComparatorActsAsMaxHeap() {
        Comparator<Integer> rev = Comparator.reverseOrder();
        MaxiphobicHeap<Integer> h = MaxiphobicHeap.of(rev, 4, 1, 7, 6, 6, 5);
        assertEquals(7, h.minimum()); // con comparador inverso, el 'mínimo' lógico es el máximo numérico
        h.deleteMinimum();
        assertEquals(6, h.minimum());
    }

    @Test
    @DisplayName("clear() deja el heap vacío")
    void clearEmptiesHeap() {
        MaxiphobicHeap<Integer> h = MaxiphobicHeap.of(8, 2, 5);
        assertFalse(h.isEmpty());
        assertEquals(3, h.size());
        h.clear();
        assertTrue(h.isEmpty());
        assertEquals(0, h.size());
        assertThrows(EmptyHeapException.class, h::minimum);
    }

    @Test
    @DisplayName("deleteMinimum y minimum lanzan EmptyHeapException en heap vacío")
    void emptyExceptions() {
        MaxiphobicHeap<Integer> h = MaxiphobicHeap.empty();
        assertThrows(EmptyHeapException.class, h::minimum);
        assertThrows(EmptyHeapException.class, h::deleteMinimum);
    }

    @Test
    @DisplayName("copyOf crea una copia profunda: modificar una no afecta a la otra")
    void copyOfDeepCopy() {
        MaxiphobicHeap<Integer> h1 = MaxiphobicHeap.of(7, 2, 9, 4);
        MaxiphobicHeap<Integer> h2 = MaxiphobicHeap.copyOf(h1);
        assertEquals(h1.minimum(), h2.minimum());
        assertEquals(h1.size(), h2.size());

        // Modifico h1 y compruebo que h2 permanece independiente
        h1.deleteMinimum(); // elimina el 2
        h1.insert(1);
        assertEquals(1, h1.minimum());
        // h2 no debería verse afectado (su mínimo sigue siendo 2)
        assertEquals(2, h2.minimum());
    }

    @Test
    @DisplayName("toString contiene el nombre de la clase y valores representativos")
    void toStringContainsHints() {
        MaxiphobicHeap<Integer> empty = MaxiphobicHeap.empty();
        String sEmpty = empty.toString();
        assertTrue(sEmpty.contains("MaxiphobicHeap"));
        assertTrue(sEmpty.contains("null"));

        MaxiphobicHeap<Integer> h = MaxiphobicHeap.of(3, 1, 4);
        String s = h.toString();
        assertTrue(s.contains("MaxiphobicHeap"));
        // No comprobamos la forma exacta del árbol, sólo que aparecen valores
        assertTrue(s.contains("1"));
        assertTrue(s.contains("3") || s.contains("4"));
    }

    @Test
    @DisplayName("Aleatoria determinista: comportamiento equivalente a PriorityQueue")
    void randomizedAgainstPriorityQueue() {
        long seed = 123456789L;
        Random rnd = new Random(seed);
        Comparator<Integer> cmp = rnd.nextBoolean() ? Comparator.naturalOrder() : Comparator.reverseOrder();

        MaxiphobicHeap<Integer> h = MaxiphobicHeap.empty(cmp);
        PriorityQueue<Integer> pq = new PriorityQueue<>(cmp);

        // Mezcla de inserts y deletes
        for (int i = 0; i < 500; i++) {
            if (rnd.nextDouble() < 0.7) { // 70% inserciones
                int v = rnd.nextInt(100) - 50;
                h.insert(v);
                pq.add(v);
            } else { // 30% borrados si hay elementos
                if (!pq.isEmpty()) {
                    assertFalse(h.isEmpty());
                    assertEquals(pq.peek(), h.minimum());
                    pq.poll();
                    h.deleteMinimum();
                } else {
                    assertTrue(h.isEmpty());
                    // En vacío ambos lanzan si se pide mínimo; comprobamos sólo el nuestro
                    assertThrows(EmptyHeapException.class, h::minimum);
                }
            }
            assertEquals(pq.size(), h.size());
        }

        // Vaciado final comparando elementos
        while (!pq.isEmpty()) {
            assertEquals(pq.peek(), h.minimum());
            pq.poll();
            h.deleteMinimum();
            assertEquals(pq.size(), h.size());
        }
        assertTrue(h.isEmpty());
    }

    @Test
    @DisplayName("from + of(Comparator, ...) conservan todos los duplicados")
    void duplicatesArePreserved() {
        Comparator<Integer> cmp = Comparator.naturalOrder();
        MaxiphobicHeap<Integer> h1 = MaxiphobicHeap.of(cmp, 2, 2, 2, 1, 1, 3);
        List<Integer> list = Arrays.asList(2, 2, 2, 1, 1, 3);
        MaxiphobicHeap<Integer> h2 = MaxiphobicHeap.from(cmp, list);

        List<Integer> out1 = new ArrayList<>();
        while (!h1.isEmpty()) { out1.add(h1.minimum()); h1.deleteMinimum(); }
        List<Integer> out2 = new ArrayList<>();
        while (!h2.isEmpty()) { out2.add(h2.minimum()); h2.deleteMinimum(); }

        assertEquals(Arrays.asList(1, 1, 2, 2, 2, 3), out1);
        assertEquals(out1, out2);
    }
}
