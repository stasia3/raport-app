package com.raport.app.service;

import com.raport.app.entity.PersoanaFizica;
import com.raport.app.entity.Photo;
import com.raport.app.entity.Post;
import com.raport.app.entity.enums.PhotoType;
import com.raport.app.repository.PhotoRepository;
import com.raport.app.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PhotoRepository photoRepository;

    private final Path uploadDir = Paths.get("uploads/posts");

    public PostService(PostRepository postRepository, PhotoRepository photoRepository) {
        this.postRepository = postRepository;
        this.photoRepository = photoRepository;
    }

    public void createPost(Post post,
                           PersoanaFizica creator,
                           boolean isAuthenticated,
                           MultipartFile[] images) {

        normalizePost(post);
        if (post.getCity() == null || post.getCity().isBlank()) {
            post.setCity("Galați");
        }
        validatePost(post, isAuthenticated, images);

        post.setCreator(creator);

        if (post.getCreatedAt() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }

        if (post.getIsAnonymous() == null) {
            post.setIsAnonymous(false);
        }
        Post savedPost = postRepository.save(post);
        savePhotos(savedPost, images);
    }

    private void normalizePost(Post post) {
        if (post.getTitle() != null) {
            post.setTitle(post.getTitle().trim());
        }

        if (post.getDescription() != null) {
            post.setDescription(post.getDescription().trim());
        }

        if (post.getProblemTag() != null) {
            post.setProblemTag(post.getProblemTag().trim());
        }

        if (post.getStreet() != null) {
            post.setStreet(post.getStreet().trim());
        }

        if (post.getCity() != null) {
            post.setCity(post.getCity().trim());
        }

        if (post.getDistrict() != null) {
            post.setDistrict(post.getDistrict().trim());
        }

        if (post.getLandmark() != null) {
            post.setLandmark(post.getLandmark().trim());
        }
    }

    private void validatePost(Post post,
                              boolean isAuthenticated,
                              MultipartFile[] images) {

        if (post.getTitle() == null || post.getTitle().isBlank()) {
            throw new IllegalArgumentException("Titlul este obligatoriu.");
        }

        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new IllegalArgumentException("Descrierea este obligatorie.");
        }

        if (post.getProblemTag() == null || post.getProblemTag().isBlank()) {
            throw new IllegalArgumentException("Categoria este obligatorie.");
        }

        if (post.getStreet() == null || post.getStreet().isBlank()) {
            throw new IllegalArgumentException("Adresa este obligatorie.");
        }

        if (post.getCity() == null || post.getCity().isBlank()) {
            throw new IllegalArgumentException("Orașul este obligatoriu.");
        }

        int imagesCount = countNonEmptyImages(images);

        if (isAuthenticated) {
            if (imagesCount > 5) {
                throw new IllegalArgumentException("Poți încărca maximum 5 fotografii.");
            }
        } else {
            if (imagesCount > 2) {
                throw new IllegalArgumentException("Fără autentificare poți încărca maximum 2 fotografii.");
            }

            if (post.getDescription().length() > 500) {
                throw new IllegalArgumentException("Fără autentificare descrierea poate avea maximum 500 de caractere.");
            }
        }
    }

    private int countNonEmptyImages(MultipartFile[] images) {
        if (images == null || images.length == 0) {
            return 0;
        }

        int count = 0;
        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                count++;
            }
        }

        return count;
    }

    private void savePhotos(Post savedPost, MultipartFile[] images) {
        if (images == null || images.length == 0) {
            return;
        }

        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new IllegalArgumentException("Nu s-a putut crea folderul pentru poze.");
        }

        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) {
                continue;
            }

            String originalName = image.getOriginalFilename();
            String extension = getExtension(originalName);
            String generatedName = UUID.randomUUID() + extension;

            Path filePath = uploadDir.resolve(generatedName);

            try (var inputStream = image.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
//                image.transferTo(filePath.toFile());
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Eroare la salvarea unei poze.");
            }

            Photo photo = new Photo();
            photo.setPost(savedPost);
            photo.setPhotoUrl("/uploads/posts/" + generatedName);
            photo.setPhotoType(PhotoType.Before);

            photoRepository.save(photo);
        }
    }
    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}