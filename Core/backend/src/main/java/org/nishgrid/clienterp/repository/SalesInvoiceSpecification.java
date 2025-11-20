package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.Customer;
import org.nishgrid.clienterp.model.SalesInvoice;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class SalesInvoiceSpecification {

    public static Specification<SalesInvoice> findByCriteria(String searchTerm, String status) {
        return (root, query, criteriaBuilder) -> {
            // This prevents duplicate rows when using JOIN FETCH in paged queries
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("customer");
            }

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String likePattern = "%" + searchTerm.toLowerCase() + "%";
                Join<SalesInvoice, Customer> customerJoin = root.join("customer");
                return criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("invoiceNo")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("name")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("mobile")), likePattern)
                );
            }
            return null; // No criteria, return all
        };
    }
}