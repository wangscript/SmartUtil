package iminto.cache;
import java.util.Iterator;
public interface Cache<K, V> {
	int getCacheSize();
	long getCacheTimeout();
	void put(K key, V object);
	void put(K key, V object, long timeout);
	V get(K key);
	Iterator<V> iterator();

	/**
	 * Prunes objects from cache and returns the number of removed objects.
	 * Used strategy depends on cache implementation.
	 */
	int prune();

	boolean isFull();
	void remove(K key);
	void clear();
	int size();
	boolean isEmpty();
}
