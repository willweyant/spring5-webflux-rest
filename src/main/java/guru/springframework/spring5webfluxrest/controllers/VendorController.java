package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class VendorController {
    private final VendorRepository vendorRepository;

    public VendorController(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @GetMapping("/api/v1/vendors")
    public Flux<Vendor> list() {
        return vendorRepository.findAll();
    }

    @GetMapping("/api/v1/vendors/{id}")
    public Mono<Vendor> findById(@PathVariable final String id) {
        return vendorRepository.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/vendors")
    public Mono<Void> create(@RequestBody final Publisher<Vendor> vendorStream) {
        return vendorRepository.saveAll(vendorStream).then();
    }

    @PutMapping("/api/v1/vendors/{id}")
    public Mono<Vendor> update(@PathVariable final String id, @RequestBody final Vendor vendor) {
        vendor.setId(id);

        return vendorRepository.save(vendor);
    }

    @PatchMapping("/api/v1/vendors/{id}")
    public Mono<Vendor> patch(@PathVariable final String id, @RequestBody final Vendor vendor) {
        //Note: This would go into a Service class due to business logic
        final Vendor foundVendor = vendorRepository.findById(id).block();

        if (!foundVendor.getFirstName().equals(vendor.getFirstName()) ||
                !foundVendor.getLastName().equals(vendor.getLastName())) {
            foundVendor.setFirstName(vendor.getFirstName());
            foundVendor.setLastName(vendor.getLastName());
            return vendorRepository.save(foundVendor);
        }

        return Mono.just(foundVendor);
    }
}
