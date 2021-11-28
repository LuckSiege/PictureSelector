package top.zibin.luban.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author：luck
 * @date：2021/8/26 3:16 下午
 * @describe：GroupedLinkedMap
 */
class GroupedLinkedMap<K extends PoolAble, V> {
    private final LinkedEntry<K, V> head = new LinkedEntry<>();
    private final Map<K, LinkedEntry<K, V>> keyToEntry = new HashMap<>();

    public void put(K key, V value) {
        LinkedEntry<K, V> entry = keyToEntry.get(key);

        if (entry == null) {
            entry = new LinkedEntry<>(key);
            makeTail(entry);
            keyToEntry.put(key, entry);
        } else {
            key.offer();
        }

        entry.add(value);
    }

    public V get(K key) {
        LinkedEntry<K, V> entry = keyToEntry.get(key);
        if (entry == null) {
            entry = new LinkedEntry<>(key);
            keyToEntry.put(key, entry);
        } else {
            key.offer();
        }

        makeHead(entry);

        return entry.removeLast();
    }

    public V removeLast() {
        LinkedEntry<K, V> last = head.prev;

        while (!last.equals(head)) {
            V removed = last.removeLast();
            if (removed != null) {
                return removed;
            } else {
                // We will clean up empty lru entries since they are likely to have been one off or
                // unusual sizes and
                // are not likely to be requested again so the gc thrash should be minimal. Doing so will
                // speed up our
                // removeLast operation in the future and prevent our linked list from growing to
                // arbitrarily large
                // sizes.
                removeEntry(last);
                keyToEntry.remove(last.key);
                last.key.offer();
            }

            last = last.prev;
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GroupedLinkedMap( ");
        LinkedEntry<K, V> current = head.next;
        boolean hadAtLeastOneItem = false;
        while (!current.equals(head)) {
            hadAtLeastOneItem = true;
            sb.append('{').append(current.key).append(':').append(current.size()).append("}, ");
            current = current.next;
        }
        if (hadAtLeastOneItem) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.append(" )").toString();
    }

    // Make the entry the most recently used item.
    private void makeHead(LinkedEntry<K, V> entry) {
        removeEntry(entry);
        entry.prev = head;
        entry.next = head.next;
        updateEntry(entry);
    }

    // Make the entry the least recently used item.
    private void makeTail(LinkedEntry<K, V> entry) {
        removeEntry(entry);
        entry.prev = head.prev;
        entry.next = head;
        updateEntry(entry);
    }

    private static <K, V> void updateEntry(LinkedEntry<K, V> entry) {
        entry.next.prev = entry;
        entry.prev.next = entry;
    }

    private static <K, V> void removeEntry(LinkedEntry<K, V> entry) {
        entry.prev.next = entry.next;
        entry.next.prev = entry.prev;
    }

    private static class LinkedEntry<K, V> {
        final K key;
        private List<V> values;
        LinkedEntry<K, V> next;
        LinkedEntry<K, V> prev;

        // Used only for the first item in the list which we will treat specially and which will not
        // contain a value.
        LinkedEntry() {
            this(null);
        }

        LinkedEntry(K key) {
            next = prev = this;
            this.key = key;
        }

        public V removeLast() {
            final int valueSize = size();
            return valueSize > 0 ? values.remove(valueSize - 1) : null;
        }

        public int size() {
            return values != null ? values.size() : 0;
        }

        public void add(V value) {
            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(value);
        }
    }
}
