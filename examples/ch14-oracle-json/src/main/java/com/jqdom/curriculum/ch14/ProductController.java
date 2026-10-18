package com.jqdom.curriculum.ch14;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** oracle Profile에서만 의미가 있다. JSON 컬럼의 내용을 그대로(JSON 텍스트로) 돌려준다. */
@RestController
public class ProductController {

    private final ProductMapper mapper;

    public ProductController(ProductMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<String> get(@PathVariable long id) {
        Product product = mapper.findById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        String body = "{\"id\":%d,\"name\":\"%s\",\"attributes\":%s}".formatted(
                product.getId(), product.getName(), product.getAttributes());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
    }

    @GetMapping("/products/{id}/tags")
    public List<String> tags(@PathVariable long id) {
        return mapper.findTags(id);
    }

    @GetMapping("/products")
    public List<String> names(@RequestParam(required = false) Integer minPrice,
                              @RequestParam(required = false) String tag) {
        if (tag != null) {
            return mapper.findByTag(tag).stream().map(Product::getName).toList();
        }
        return mapper.findPriceOver(minPrice == null ? 0 : minPrice).stream().map(Product::getName).toList();
    }
}
