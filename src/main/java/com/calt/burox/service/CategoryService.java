package com.calt.burox.service;

import com.calt.burox.domain.Category;
import com.calt.burox.repository.CategoryRepository;
import com.calt.burox.repository.search.CategorySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.calt.burox.domain.Category}.
 */
@Service
@Transactional
public class CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    private final CategorySearchRepository categorySearchRepository;

    public CategoryService(CategoryRepository categoryRepository, CategorySearchRepository categorySearchRepository) {
        this.categoryRepository = categoryRepository;
        this.categorySearchRepository = categorySearchRepository;
    }

    /**
     * Save a category.
     *
     * @param category the entity to save.
     * @return the persisted entity.
     */
    public Mono<Category> save(Category category) {
        LOG.debug("Request to save Category : {}", category);
        return categoryRepository.save(category).flatMap(categorySearchRepository::save);
    }

    /**
     * Update a category.
     *
     * @param category the entity to save.
     * @return the persisted entity.
     */
    public Mono<Category> update(Category category) {
        LOG.debug("Request to update Category : {}", category);
        return categoryRepository.save(category).flatMap(categorySearchRepository::save);
    }

    /**
     * Partially update a category.
     *
     * @param category the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Category> partialUpdate(Category category) {
        LOG.debug("Request to partially update Category : {}", category);

        return categoryRepository
            .findById(category.getId())
            .map(existingCategory -> {
                if (category.getName() != null) {
                    existingCategory.setName(category.getName());
                }
                if (category.getDescription() != null) {
                    existingCategory.setDescription(category.getDescription());
                }
                if (category.getCreatedAt() != null) {
                    existingCategory.setCreatedAt(category.getCreatedAt());
                }
                if (category.getUpdatedAt() != null) {
                    existingCategory.setUpdatedAt(category.getUpdatedAt());
                }

                return existingCategory;
            })
            .flatMap(categoryRepository::save)
            .flatMap(savedCategory -> {
                categorySearchRepository.save(savedCategory);
                return Mono.just(savedCategory);
            });
    }

    /**
     * Get all the categories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Category> findAll(Pageable pageable) {
        LOG.debug("Request to get all Categories");
        return categoryRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of categories available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return categoryRepository.count();
    }

    /**
     * Returns the number of categories available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return categorySearchRepository.count();
    }

    /**
     * Get one category by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Category> findOne(Long id) {
        LOG.debug("Request to get Category : {}", id);
        return categoryRepository.findById(id);
    }

    /**
     * Delete the category by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Category : {}", id);
        return categoryRepository.deleteById(id).then(categorySearchRepository.deleteById(id));
    }

    /**
     * Search for the category corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Category> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Categories for query {}", query);
        return categorySearchRepository.search(query, pageable);
    }
}
