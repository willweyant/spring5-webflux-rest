package guru.springframework.spring5webfluxrest.bootstrap;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements CommandLineRunner {

    private final VendorRepository vendorRepository;
    private final CategoryRepository categoryRepository;

    public Bootstrap(VendorRepository vendorRepository, CategoryRepository categoryRepository) {
        this.vendorRepository = vendorRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {

        if (categoryRepository.count().block() == 0) {
            //load data
            System.out.println("LOADING CATEGORY DATA");
            categoryRepository.save(Category.builder().description("Fruits").build()).block();
            categoryRepository.save(Category.builder().description("Nuts").build()).block();
            categoryRepository.save(Category.builder().description("Breads").build()).block();
            categoryRepository.save(Category.builder().description("Meats").build()).block();
            categoryRepository.save(Category.builder().description("Eggs").build()).block();

            System.out.println("LOADED CATEGORIES: " + categoryRepository.count().block());
        }

        if (vendorRepository.count().block() == 0) {
            //load data
            System.out.println("LOADING VENDOR DATA");
            vendorRepository.save(Vendor.builder().firstName("Vendor1").lastName("Smith").build()).block();
            vendorRepository.save(Vendor.builder().firstName("Vendor2").lastName("Jones").build()).block();

            System.out.println("LOADED VENDORS: " + vendorRepository.count().block());
        }
    }
}
