package org.nishgrid.clienterp.service;

import jakarta.persistence.EntityNotFoundException;
import org.nishgrid.clienterp.dto.DiscountDTO;
import org.nishgrid.clienterp.model.Discount;
import org.nishgrid.clienterp.repository.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    public Discount createDiscount(DiscountDTO dto) {
        Discount discount = new Discount();
        discount.setTitle(dto.getTitle());
        discount.setValueType(dto.getValueType());
        discount.setValueDescription(dto.getValueDescription());
        return discountRepository.save(discount);
    }

    public Discount updateDiscount(Long id, DiscountDTO dto) {
        Discount existingDiscount = discountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Discount not found with id: " + id));

        existingDiscount.setTitle(dto.getTitle());
        existingDiscount.setValueType(dto.getValueType());
        existingDiscount.setValueDescription(dto.getValueDescription());

        return discountRepository.save(existingDiscount);
    }

    public void deleteDiscount(Long id) {
        if (!discountRepository.existsById(id)) {
            throw new EntityNotFoundException("Discount not found with id: " + id);
        }
        discountRepository.deleteById(id);
    }
}