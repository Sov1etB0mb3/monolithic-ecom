package com.calt.burox.service;

import com.calt.burox.domain.Product;
import com.calt.burox.repository.ProductRepository;
import com.calt.burox.repository.search.ProductSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.calt.burox.domain.Product}.
 */
@Service
@Transactional
public class ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    private final ProductSearchRepository productSearchRepository;

    public ProductService(ProductRepository productRepository, ProductSearchRepository productSearchRepository) {
        this.productRepository = productRepository;
        this.productSearchRepository = productSearchRepository;
    }

    /**
     * Save a product.
     *
     * @param product the entity to save.
     * @return the persisted entity.
     */
    public Mono<Product> save(Product product) {
        LOG.debug("Request to save Product : {}", product);
        return productRepository.save(product).flatMap(productSearchRepository::save);
    }

    /**
     * Update a product.
     *
     * @param product the entity to save.
     * @return the persisted entity.
     */
    public Mono<Product> update(Product product) {
        LOG.debug("Request to update Product : {}", product);
        return productRepository.save(product).flatMap(productSearchRepository::save);
    }

    /**
     * Partially update a product.
     *
     * @param product the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Product> partialUpdate(Product product) {
        LOG.debug("Request to partially update Product : {}", product);

        return productRepository
            .findById(product.getId())
            .map(existingProduct -> {
                if (product.getName() != null) {
                    existingProduct.setName(product.getName());
                }
                if (product.getQuantity() != null) {
                    existingProduct.setQuantity(product.getQuantity());
                }
                if (product.getPrice() != null) {
                    existingProduct.setPrice(product.getPrice());
                }
                if (product.getCreatedAt() != null) {
                    existingProduct.setCreatedAt(product.getCreatedAt());
                }
                if (product.getUpdatedAt() != null) {
                    existingProduct.setUpdatedAt(product.getUpdatedAt());
                }

                return existingProduct;
            })
            .flatMap(productRepository::save)
            .flatMap(savedProduct -> {
                productSearchRepository.save(savedProduct);
                return Mono.just(savedProduct);
            });
    }

    /**
     * Get all the products.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Product> findAll(Pageable pageable) {
        LOG.debug("Request to get all Products");
        return productRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of products available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return productRepository.count();
    }

    /**
     * Returns the number of products available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return productSearchRepository.count();
    }

    /**
     * Get one product by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Product> findOne(Long id) {
        LOG.debug("Request to get Product : {}", id);
        return productRepository.findById(id);
    }

    /**
     * Delete the product by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Product : {}", id);
        return productRepository.deleteById(id).then(productSearchRepository.deleteById(id));
    }

    /**
     * Search for the product corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Product> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Products for query {}", query);
        return productSearchRepository.search(query, pageable);
    }
}
