package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemBookingsCommentsDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.util.Pagenator;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";
	private final ItemService itemService;

	@PostMapping
	public ItemDto addItem(@RequestBody @Valid ItemDto itemDto,
						   @RequestHeader(USER_ID_HEADER) int userId) {
		log.info("Request to save item [{}] with userId [{}]", itemDto, userId);
		ItemDto savedItem = itemService.addItem(itemDto, userId);
		log.info("Item [{}] saved.", savedItem);
		return savedItem;
	}

	@PatchMapping("/{itemId}")
	public ItemDto updateItem(@RequestBody ItemDto itemDto,
							  @RequestHeader(USER_ID_HEADER) int userId,
							  @PathVariable int itemId) {
		itemDto.setId(itemId);
		log.info("Request to update item [{}] with userId [{}].", itemDto, userId);
		ItemDto updatedItem = itemService.updateItem(itemDto, userId);
		log.info("Item [{}] updated.", updatedItem);
		return updatedItem;
	}

	@GetMapping
	public List<ItemBookingsCommentsDto> getAllItems(@RequestHeader(USER_ID_HEADER) int userId,
													 @RequestParam(defaultValue = "0") int from,
													 @RequestParam(defaultValue = "20") int size) {
		log.info("Request to get all items with userId [{}].", userId);
		Pageable pageable = Pagenator.getPage(from, size);
		List<ItemBookingsCommentsDto> itemDtos = itemService.getAllItemsByUserId(userId, pageable);
		log.info("All items received.");
		return itemDtos;
	}

	@GetMapping("/{itemId}")
	public ItemBookingsCommentsDto getItemById(@RequestHeader(USER_ID_HEADER) int userId,
											   @PathVariable int itemId) {
		log.info("Request to get item by id [{}].", itemId);
		ItemBookingsCommentsDto itemDto = itemService.getItemById(itemId, userId);
		log.info("Item [{}] received.", itemDto);
		return itemDto;
	}

	@DeleteMapping("/{itemId}")
	public ItemDto deleteItemById(@PathVariable int itemId,
								  @RequestHeader(USER_ID_HEADER) int userId) {
		log.info("Request to delete item by id [{}], with userId [{}].", itemId, userId);
		ItemDto itemDto = itemService.deleteItemById(itemId, userId);
		log.info("Item [{}] deleted.", itemDto);
		return itemDto;
	}

	@GetMapping("/search")
	public List<ItemDto> searchByText(@RequestParam String text,
									  @RequestParam(defaultValue = "0") int from,
									  @RequestParam(defaultValue = "20") int size) {
		log.info("Request to search item by text [{}].", text);
		Pageable pageable = Pagenator.getPage(from, size);
		List<ItemDto> itemsDtos = itemService.searchByText(text, pageable);
		log.info("Items received.");
		return itemsDtos;
	}

	@PostMapping("/{itemId}/comment")
	public CommentDto addComment(@PathVariable int itemId,
								 @RequestHeader(USER_ID_HEADER) int userId,
								 @RequestBody @Valid CommentDto commentDto) {
		log.info("Request to add comment [{}] with itemId [{}] and userId [{}]",
				commentDto, itemId, userId);
		CommentDto savedComment = itemService.addComment(itemId, userId, commentDto);
		log.info("Comment [{}] saved.", savedComment);
		return savedComment;
	}
}
