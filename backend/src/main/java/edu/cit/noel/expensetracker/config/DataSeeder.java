package edu.cit.noel.expensetracker.config;

import edu.cit.noel.expensetracker.entity.Category;
import edu.cit.noel.expensetracker.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public DataSeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        String[] defaultCategories = {
            "Food & Dining", "Transportation", "Utilities",
            "Office Supplies", "Healthcare", "Entertainment", "Other"
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
