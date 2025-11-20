package org.nishgrid.clienterp.model;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class SalesInvoiceSpecification {

    public static Specification<SalesInvoice> findByCriteria(String searchTerm, String status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchTerm != null && !searchTerm.isBlank()) {
                String searchPattern = "%" + searchTerm.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("invoiceNo")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("customer").get("name")), searchPattern)
                ));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("status")),
                        status.toLowerCase()
                ));
            }

            query.orderBy(criteriaBuilder.desc(root.get("invoiceId")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}