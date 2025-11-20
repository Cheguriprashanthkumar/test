package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.model.ProductCatalog;
import org.nishgrid.clienterp.repository.ProductCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductCatalogController {

    @Autowired
    private ProductCatalogRepository productCatalogRepository;

    @PostMapping // Create
    public ProductCatalog createProduct(@RequestBody ProductCatalog product) {
        return productCatalogRepository.save(product);
    }

    @GetMapping // Read All
    public List<ProductCatalog> getAllProducts() {
        return productCatalogRepository.findAll();
    }

    @PutMapping("/{id}") // Update
    public ResponseEntity<ProductCatalog> updateProduct(@PathVariable Long id, @RequestBody ProductCatalog productDetails) {
        return productCatalogRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setPurity(productDetails.getPurity());
                    product.setDefaultRate(productDetails.getDefaultRate());
                    ProductCatalog updatedProduct = productCatalogRepository.save(product);
                    return ResponseEntity.ok(updatedProduct);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}") // Delete
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return productCatalogRepository.findById(id)
                .map(product -> {
                    productCatalogRepository.delete(product);
                    return ResponseEntity.ok().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }
}