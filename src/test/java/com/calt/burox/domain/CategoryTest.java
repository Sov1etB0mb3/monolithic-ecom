package com.calt.burox.domain;

import static com.calt.burox.domain.CategoryTestSamples.*;
import static com.calt.burox.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.calt.burox.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Category.class);
        Category category1 = getCategorySample1();
        Category category2 = new Category();
        assertThat(category1).isNotEqualTo(category2);

        category2.setId(category1.getId());
        assertThat(category1).isEqualTo(category2);

        category2 = getCategorySample2();
        assertThat(category1).isNotEqualTo(category2);
    }

    @Test
    void listProductTest() {
        Category category = getCategoryRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        category.addListProduct(productBack);
        assertThat(category.getListProducts()).containsOnly(productBack);
        assertThat(productBack.getCategory()).isEqualTo(category);

        category.removeListProduct(productBack);
        assertThat(category.getListProducts()).doesNotContain(productBack);
        assertThat(productBack.getCategory()).isNull();

        category.listProducts(new HashSet<>(Set.of(productBack)));
        assertThat(category.getListProducts()).containsOnly(productBack);
        assertThat(productBack.getCategory()).isEqualTo(category);

        category.setListProducts(new HashSet<>());
        assertThat(category.getListProducts()).doesNotContain(productBack);
        assertThat(productBack.getCategory()).isNull();
    }
}
