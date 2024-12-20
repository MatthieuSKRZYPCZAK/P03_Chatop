INSERT INTO users (created_at, email, name, password, updated_at, token_version)
    VALUES (CURRENT_TIMESTAMP,'test@test.fr' , 'MrTest1', '$2a$10$YeiT4ydjp6lHWuVpXQh9nO1sid7uqOxgpWne9ufnT8TuCfUwcd9ou', NULL, 1),
           (CURRENT_TIMESTAMP,'test2@test.fr' , 'MrTest2', '$2a$10$YeiT4ydjp6lHWuVpXQh9nO1sid7uqOxgpWne9ufnT8TuCfUwcd9ou', NULL, 1),
           (CURRENT_TIMESTAMP,'test3@test.fr' , 'MrTest3', '$2a$10$YeiT4ydjp6lHWuVpXQh9nO1sid7uqOxgpWne9ufnT8TuCfUwcd9ou', NULL, 1);

INSERT INTO rentals (created_at, name, surface, price, picture, description, owner_id)
    VALUES(CURRENT_TIMESTAMP, "Cozy Apartment", 45, 800, "http://localhost:3001/uploads/rentals/example1.jpg", "A small cozy apartment in the city center.", 1),
          (CURRENT_TIMESTAMP, "Luxury Villa", 200, 3500, "http://localhost:3001/uploads/rentals/example2.jpg", "A luxurious villa with a private pool and garden.", 2),
          (CURRENT_TIMESTAMP, "Beach House", 120, 1500, "http://localhost:3001/uploads/rentals/example3.jpg", "A house with stunning ocean views, perfect for vacations.", 3),
          (CURRENT_TIMESTAMP, "Modern Studio", 30, 600, "http://localhost:3001/uploads/rentals/example4.jpg", "A modern studio with all amenities included.", 1),
          (CURRENT_TIMESTAMP, "Rustic Cabin", 75, 1000, "http://localhost:3001/uploads/rentals/example5.jpg", "A charming rustic cabin in the woods.", 2);
