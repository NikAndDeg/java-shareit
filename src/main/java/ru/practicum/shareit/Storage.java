package ru.practicum.shareit;

import java.util.List;
import java.util.Optional;

public interface Storage<Key, Data> {
	Data add(Data data);
	Optional<Data> get(Key key);
	List<Data> getByKeys(List<Key> keys);
	List<Data> getAll();
	Data update(Data updatedData);
	Optional<Data> deleteByData(Data data);
	Optional<Data> deleteByKey(Key key);
	boolean containsData(Data data);
	boolean containsKey(Key key);
}
