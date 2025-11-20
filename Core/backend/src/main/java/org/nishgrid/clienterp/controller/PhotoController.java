package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.model.ClientPhoto;
import org.nishgrid.clienterp.repository.ClientPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/photo")
public class PhotoController {

    @Autowired
    private ClientPhotoRepository photoRepository;

    @Transactional
    @PostMapping("/{uniqueId}")
    public ResponseEntity<Void> uploadPhoto(
            @PathVariable("uniqueId") String uniqueId, // <-- FIX IS HERE
            @RequestBody byte[] photoData) {

        try {
            Optional<ClientPhoto> existingPhotoOpt = photoRepository.findByUniqueId(uniqueId);

            ClientPhoto clientPhoto;
            if (existingPhotoOpt.isPresent()) {
                clientPhoto = existingPhotoOpt.get();
            } else {
                clientPhoto = new ClientPhoto();
                clientPhoto.setUniqueId(uniqueId);
            }

            clientPhoto.setPhoto(photoData);
            photoRepository.save(clientPhoto);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Transactional(readOnly = true)
    @GetMapping("/{uniqueId}")
    public ResponseEntity<byte[]> getPhoto(
            @PathVariable("uniqueId") String uniqueId) { // <-- FIX IS HERE

        return photoRepository.findByUniqueId(uniqueId)
                .map(clientPhoto -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(clientPhoto.getPhoto()))
                .orElse(ResponseEntity.notFound().build());
    }
}

