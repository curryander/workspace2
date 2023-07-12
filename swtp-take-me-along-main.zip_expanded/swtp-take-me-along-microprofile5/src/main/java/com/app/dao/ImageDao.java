package com.app.dao;

import java.util.Collection;
import java.util.Optional;

import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.app.model.Image;

@Singleton
public class ImageDao implements IDao<Image, Integer> {

    @PersistenceContext(name = "jpa-unit")
    private EntityManager _em;

    public int createImage(byte[] imageData, String contentType, String username) {
        try {
            Image image = new Image();
            image.setImageData(imageData);
            image.setContentType(contentType);

            _em.persist(image);
            _em.flush();
            _em.refresh(image);

            System.out.println("User" + username + "registered");

            return image.getImageId();

        } catch (Throwable thr) {
            thr.printStackTrace();
            throw new RuntimeException("ERROR createImage");
        }
    }

    @Override
    public Optional<Image> getById(Integer id) {
        Image image = _em.find(Image.class, id);

        if (image != null)
            return Optional.of(image);

        return Optional.empty();
    }

    @Override
    public Collection<Image> getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer insert(Image t) {
        try {
            System.out.println("image id before insert: " + t.getImageId());
            _em.persist(t);
            _em.flush();
            _em.refresh(t);

            System.out.println("image id after insert: " + t.getImageId());

            return t.getImageId();

        } catch (Throwable thr) {
            thr.printStackTrace();
            throw new RuntimeException("ERROR createImage");
        }
    }

    @Override
    public void update(Image t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(Image t) {
        // TODO Auto-generated method stub

    }
}
