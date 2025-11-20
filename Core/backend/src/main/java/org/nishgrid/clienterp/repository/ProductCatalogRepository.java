package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.ProductCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductCatalogRepository extends JpaRepository<ProductCatalog, Long> {}