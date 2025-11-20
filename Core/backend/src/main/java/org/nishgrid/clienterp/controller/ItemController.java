package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.SalesItemSelectionDTO;
import org.nishgrid.clienterp.repository.SalesItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final SalesItemRepository itemRepository;

    public ItemController(SalesItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/selection")
    public List<SalesItemSelectionDTO> getAllItemsForSelection() {
        // This simple version returns all items. You could later modify this
        // to return distinct items or items currently in stock.
        return itemRepository.findAll().stream()
                .map(item -> new SalesItemSelectionDTO(item.getSalesItemId(), item.getItemName()))
                .collect(Collectors.toList());
    }
}