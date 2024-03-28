package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.DtoMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
	private final ItemService itemService;
	private final DtoMapper<ItemDto, Item> mapper;

	@Autowired
	public ItemController(ItemService itemService,
						  DtoMapper<ItemDto, Item> mapper) {
		this.itemService = itemService;
		this.mapper = mapper;
	}

	@PostMapping
	public ItemDto addItem(@RequestBody @Valid ItemDto itemDto,
						   @RequestHeader("X-Sharer-User-Id") Integer userId) {
		log.info("Request to save item [{}] with userId [{}]", itemDto, userId);
		if (itemDto.getName() == null || itemDto.getDescription() == null || itemDto.getAvailable() == null
				|| itemDto.getName().isBlank() || itemDto.getDescription().isBlank()) {
			log.info("Item isn't save. Item without name or description or available.");
			throw new BadRequestException("Item without name or description or available.");
		}
		Item item = mapper.toModel(itemDto, userId.toString());
		Item savedItem = itemService.addItem(item);
		log.info("Item [{}] saved.", savedItem);
		return mapper.toDto(savedItem);
	}

	@PatchMapping("/{itemId}")
	public ItemDto updateItem(@RequestBody @Valid ItemDto itemDto,
							  @RequestHeader("X-Sharer-User-Id") Integer userId,
							  @PathVariable int itemId) {
		log.info("Request to update item [{}] with userId [{}].", itemDto, userId);
		Item item = mapper.toModel(itemDto, userId.toString());
		Item updatedItem = itemService.updateItem(item, itemId);
		log.info("Item [{}] updated.", updatedItem);
		return mapper.toDto(updatedItem);
	}

	@GetMapping
	public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") int userId) {
		log.info("Request to get all items with userId [{}].", userId);
		List<Item> items = itemService.getAllItemsByUserId(userId);
		log.info("All items received.");
		return items.stream()
				.map(mapper::toDto)
				.collect(Collectors.toList());
	}

	@GetMapping("/{itemId}")
	public ItemDto getItemById(@PathVariable int itemId) {
		log.info("Request to get item by id [{}].", itemId);
		Item item = itemService.getItemById(itemId);
		log.info("Item [{}] received.", item);
		return mapper.toDto(item);
	}

	@DeleteMapping("/{itemId}")
	public ItemDto deleteItemById(@PathVariable int itemId,
								  @RequestHeader("X-Sharer-User-Id") int userId) {
		log.info("Request to delete item by id [{}], with userId [{}].", itemId, userId);
		Item deletedItem = itemService.deleteItemById(itemId, userId);
		log.info("Item [{}] deleted.", deletedItem);
		return mapper.toDto(deletedItem);
	}

	@GetMapping("/search")
	public List<ItemDto> searchByText(@RequestParam String text) {
		log.info("Request to search item by text [{}].", text);
		List<Item> items = itemService.searchByText(text);
		log.info("Items received.");
		return items.stream()
				.map(mapper::toDto)
				.collect(Collectors.toList());
	}
}
