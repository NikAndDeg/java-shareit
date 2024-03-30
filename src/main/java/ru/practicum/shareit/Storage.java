package ru.practicum.shareit;

import java.util.List;
import java.util.Optional;

public interface Storage<K, D> {

	D add(D data);

	Optional<D> get(K key);

	List<D> getByKeys(List<K> keys);

	List<D> getAll();

	D update(D updatedData);

	Optional<D> deleteByData(D data);

	Optional<D> deleteByKey(K key);

	boolean containsData(D data);

	boolean containsKey(K key);
}
