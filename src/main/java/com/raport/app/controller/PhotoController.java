package com.raport.app.controller;

import com.raport.app.entity.Photo;
import com.raport.app.entity.Post;
import com.raport.app.entity.enums.PhotoType;
import com.raport.app.repository.PhotoRepository;
import com.raport.app.repository.PostRepository;
import com.raport.app.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/admin/photos")
public class PhotoController {
    private final String rootFolder = "admin/db-tables/";

    private final PhotoRepository photoRepository;
    private final PostRepository postRepository;
    private final FileStorageService fileStorageService;

    public PhotoController(PhotoRepository photoRepository,
                           PostRepository postRepository,
                           FileStorageService fileStorageService) {
        this.photoRepository = photoRepository;
        this.postRepository = postRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String listPhotos(Model model) {
        model.addAttribute("photos", photoRepository.findAll());
        return rootFolder + "photo-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("photo", new Photo());
        model.addAttribute("posts", postRepository.findAll());
        model.addAttribute("photoTypes", PhotoType.values());
        return rootFolder + "photo-form";
    }

    @PostMapping("/save")
    public String savePhoto(@ModelAttribute("photo") Photo photo,
                            @RequestParam("postId") Integer postId,
                            @RequestParam("imageFile") MultipartFile imageFile,
                            Model model) {

        try {
            Optional<Post> postOptional = postRepository.findById(postId);

            if (postOptional.isEmpty()) {
                model.addAttribute("error", "Post not found");
                model.addAttribute("posts", postRepository.findAll());
                model.addAttribute("photoTypes", PhotoType.values());
                return rootFolder + "photo-form";
            }

            photo.setPost(postOptional.get());

            if (imageFile != null && !imageFile.isEmpty()) {
                String savedPath = fileStorageService.saveFile(imageFile);
                photo.setPhotoUrl(savedPath);
            } else if (photo.getId() != null) {
                Photo existingPhoto = photoRepository.findById(photo.getId()).orElse(null);
                if (existingPhoto != null) {
                    photo.setPhotoUrl(existingPhoto.getPhotoUrl());
                }
            }

            photoRepository.save(photo);
            return "redirect:/admin/photos";

        } catch (IOException e) {
            model.addAttribute("error", "File upload failed");
            model.addAttribute("posts", postRepository.findAll());
            model.addAttribute("photoTypes", PhotoType.values());
            return rootFolder + "photo-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<Photo> photoOptional = photoRepository.findById(id);

        if (photoOptional.isPresent()) {
            model.addAttribute("photo", photoOptional.get());
            model.addAttribute("posts", postRepository.findAll());
            model.addAttribute("photoTypes", PhotoType.values());
            return rootFolder + "photo-form";
        }

        return "redirect:/admin/photos";
    }

    @GetMapping("/delete/{id}")
    public String deletePhoto(@PathVariable Integer id) {
        photoRepository.deleteById(id);
        return "redirect:/admin/photos";
    }

    @GetMapping("/view/{id}")
    public String viewPhoto(@PathVariable Integer id, Model model) {
        Optional<Photo> photoOptional = photoRepository.findById(id);

        if (photoOptional.isPresent()) {
            model.addAttribute("photo", photoOptional.get());
            return rootFolder + "photo-view";
        }

        return "redirect:/admin/photos";
    }
}