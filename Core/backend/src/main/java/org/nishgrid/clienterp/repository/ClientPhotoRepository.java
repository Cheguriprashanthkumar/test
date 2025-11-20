package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.ClientPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientPhotoRepository extends JpaRepository<ClientPhoto, Long> {

    Optional<ClientPhoto> findByUniqueId(String uniqueId);
}