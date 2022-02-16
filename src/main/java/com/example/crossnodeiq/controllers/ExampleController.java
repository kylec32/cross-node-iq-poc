package com.example.crossnodeiq.controllers;

import com.example.crossnodeiq.event.model.ExampleSchema;
import com.example.crossnodeiq.kafka.query.ExampleQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExampleController {
    private final ExampleQueryService exampleQueryService;

    @GetMapping("example/{id}")
    public String getExample(@PathVariable String id) {
        return exampleQueryService.get(Integer.parseInt(id)).map(ExampleSchema::getTitle).orElse("unknown title");
    }

    @PostMapping(value = "example/lookup-avro", produces = "application/avro")
    public ExampleSchema lookupExample(@RequestBody String id) {
        return exampleQueryService.get(Integer.parseInt(id)).orElseThrow(() -> new RuntimeException("not found"));
    }
}
