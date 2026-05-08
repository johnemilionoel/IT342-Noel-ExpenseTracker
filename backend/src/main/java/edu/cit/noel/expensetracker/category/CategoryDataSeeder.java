package edu.cit.noel.expensetracker.category;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategoryDataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public CategoryDataSeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {

        String[] defaultCategories = {
                "Food & Dining",
                "Transportation",
                "Utilities",
                "Office Supplies",
                "Healthcare",
                "Entertainment",
                "Other"
        };

        for (String name : defaultCategories) {

            if (!categoryRepository.existsByName(name)) {

                Category cat = new Category();
                cat.setName(name);

                categoryRepository.save(cat);
            }
        }
    }
}