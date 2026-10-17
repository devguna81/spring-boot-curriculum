package com.jqdom.curriculum.ch15;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    public record ProductRequest(String name, ProductAttributes attributes) {
    }

    public record ProductResponse(long id, String name, ProductAttributes attributes) {
    }

    private final ProductMapper mapper;

    public ProductController(ProductMapper mapper) {
        this.mapper = mapper;
    }

    /** 요청 JSON -> record -> (TypeHandler) -> JSON 컬럼. */
    @PostMapping("/products")
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        Product product = new Product(request.name(), request.attributes());
        mapper.insert(product);
        return ResponseEntity.created(URI.create("/products/" + product.getId()))
                .body(new ProductResponse(product.getId(), product.getName(), product.getAttributes()));
    }

    /** JSON 컬럼 -> (TypeHandler) -> record -> 응답 JSON. */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> get(@PathVariable long id) {
        Product product = mapper.findById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ProductResponse(product.getId(), product.getName(), product.getAttributes()));
    }
}
