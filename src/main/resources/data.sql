-- ============================================================================
-- Seed Data for E-Bookstore Platform (PostgreSQL)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Categories (5 categories)
-- ----------------------------------------------------------------------------
INSERT INTO categories (id, name, description, image_url) VALUES
(1, 'Technology', 'Books on software development, cloud computing, AI, and systems engineering.', 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=600&q=80'),
(2, 'Fiction', 'Engaging literary fiction, classics, thrillers, and science fiction novels.', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=600&q=80'),
(3, 'Self-Help', 'Guides for personal growth, productivity, psychology, and habit transformation.', 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=600&q=80'),
(4, 'Science', 'Explorations in physics, astronomy, biology, and scientific discoveries.', 'https://images.unsplash.com/photo-1507668077129-56e32842fceb?auto=format&fit=crop&w=600&q=80'),
(5, 'Business & Economics', 'Insightful books on entrepreneurship, management, leadership, and markets.', 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=600&q=80')
ON CONFLICT (id) DO NOTHING;

-- ----------------------------------------------------------------------------
-- Brands / Publishers (5 publishers)
-- ----------------------------------------------------------------------------
INSERT INTO brands (id, name, logo_url) VALUES
(1, 'O''Reilly Media', 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/OReilly_Media_logo.svg/320px-OReilly_Media_logo.svg.png'),
(2, 'Penguin Books', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Penguin_logo.svg/320px-Penguin_logo.svg.png'),
(3, 'HarperCollins', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/HarperCollins_logo.svg/320px-HarperCollins_logo.svg.png'),
(4, 'John Wiley & Sons', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/John_Wiley_%26_Sons_logo.svg/320px-John_Wiley_%26_Sons_logo.svg.png'),
(5, 'Packt Publishing', 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/Packt_logo.svg/320px-Packt_logo.svg.png')
ON CONFLICT (id) DO NOTHING;

-- ----------------------------------------------------------------------------
-- Products / Books (22 books)
-- ----------------------------------------------------------------------------
INSERT INTO products (id, title, author, description, price, stock_qty, image_url, category_id, brand_id, tentative_delivery_days, created_at) VALUES
(1, 'Designing Data-Intensive Applications', 'Martin Kleppmann', 'The definitive guide to the principles and architectures of reliable, scalable, and maintainable systems.', 45.99, 50, 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80', 1, 1, 3, NOW()),
(2, 'Building Microservices: Designing Fine-Grained Systems', 'Sam Newman', 'Comprehensive guide to distributed systems architecture, service decomposition, and containerization.', 49.50, 40, 'https://images.unsplash.com/photo-1532012164546-f432f2e3777f?auto=format&fit=crop&w=600&q=80', 1, 1, 3, NOW()),
(3, 'Cloud Native Patterns', 'Cornelia Davis', 'Designing change-tolerant, resilient software on modern cloud infrastructure platforms.', 42.00, 30, 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=600&q=80', 1, 4, 4, NOW()),
(4, 'Clean Code: A Handbook of Agile Software Craftsmanship', 'Robert C. Martin', 'Best practices for writing readable, maintainable, and robust object-oriented code.', 39.95, 60, 'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?auto=format&fit=crop&w=600&q=80', 1, 4, 2, NOW()),
(5, 'Mastering Spring Boot 3.0', 'Gheorghe Vancea', 'Build production-ready, cloud-native microservices with Spring Boot 3 and Java 17/21.', 36.99, 45, 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?auto=format&fit=crop&w=600&q=80', 1, 5, 3, NOW()),

(6, 'To Kill a Mockingbird', 'Harper Lee', 'Pulitzer Prize-winning classic novel set in the American South exploring justice and racial injustice.', 14.99, 100, 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=600&q=80', 2, 3, 2, NOW()),
(7, '1984', 'George Orwell', 'Dystopian masterpiece depicting a totalitarian regime under the omnipresent surveillance of Big Brother.', 12.50, 85, 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=600&q=80', 2, 2, 2, NOW()),
(8, 'The Great Gatsby', 'F. Scott Fitzgerald', 'Tale of ambition, love, and illusion set against the jazz-age backdrop of Long Island in the 1920s.', 11.99, 90, 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=600&q=80', 2, 2, 3, NOW()),
(9, 'Dune', 'Frank Herbert', 'Epic science-fiction saga set on the desert planet Arrakis revolving around politics, religion, and ecology.', 18.00, 75, 'https://images.unsplash.com/photo-1495640388908-05fa85288e61?auto=format&fit=crop&w=600&q=80', 2, 3, 3, NOW()),
(10, 'The Alchemist', 'Paulo Coelho', 'An inspiring fable following an Andalusian shepherd boy on a transformative quest across the desert.', 13.50, 110, 'https://images.unsplash.com/photo-1476275466078-4007374efbbe?auto=format&fit=crop&w=600&q=80', 2, 3, 2, NOW()),

(11, 'Atomic Habits', 'James Clear', 'An easy and proven framework for building good habits, breaking bad ones, and mastering daily routines.', 21.99, 120, 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=600&q=80', 3, 2, 2, NOW()),
(12, 'Deep Work: Rules for Focused Success', 'Cal Newport', 'Mastering the superpower of distracted-free concentration to achieve peak performance in a noisy world.', 19.99, 65, 'https://images.unsplash.com/photo-1457369804613-52c61a468e7d?auto=format&fit=crop&w=600&q=80', 3, 4, 3, NOW()),
(13, 'Thinking, Fast and Slow', 'Daniel Kahneman', 'Nobel laureate explores the dual systems driving human thought, judgment, and decision-making.', 22.50, 55, 'https://images.unsplash.com/photo-1532012164546-f432f2e3777f?auto=format&fit=crop&w=600&q=80', 3, 2, 4, NOW()),
(14, 'Mindset: The New Psychology of Success', 'Carol S. Dweck', 'Discovering the transformative power of a growth mindset in achieving personal and professional potential.', 16.99, 80, 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=600&q=80', 3, 3, 3, NOW()),

(15, 'A Brief History of Time', 'Stephen Hawking', 'Landmark popular science book exploring black holes, the Big Bang, general relativity, and quantum mechanics.', 17.50, 70, 'https://images.unsplash.com/photo-1507668077129-56e32842fceb?auto=format&fit=crop&w=600&q=80', 4, 2, 3, NOW()),
(16, 'Cosmos', 'Carl Sagan', 'A poetic journey through scientific knowledge exploring fourteen billion years of cosmic evolution.', 20.00, 50, 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=600&q=80', 4, 3, 4, NOW()),
(17, 'The Selfish Gene', 'Richard Dawkins', 'A seminal perspective on evolution through the lens of genetic replication and natural selection.', 18.99, 40, 'https://images.unsplash.com/photo-1532094349884-543bc11b234d?auto=format&fit=crop&w=600&q=80', 4, 4, 3, NOW()),

(18, 'The Lean Startup', 'Eric Ries', 'How continuous innovation, rapid prototyping, and validated learning create radically successful businesses.', 24.00, 85, 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=600&q=80', 5, 2, 2, NOW()),
(19, 'Zero to One: Notes on Startups', 'Peter Thiel', 'Insights and strategies for building innovative businesses that create singular value from scratch.', 21.00, 95, 'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&w=600&q=80', 5, 3, 2, NOW()),
(20, 'Principles: Life and Work', 'Ray Dalio', 'Unconventional principles developed over 40 years that created one of the worlds top hedge funds.', 27.99, 60, 'https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=600&q=80', 5, 4, 4, NOW()),
(21, 'Python Crash Course, 3rd Edition', 'Eric Matthes', 'A hands-on, project-based introduction to programming with Python 3.', 38.50, 45, 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=600&q=80', 1, 5, 3, NOW()),
(22, 'Modern Java in Action', 'Raoul-Gabriel Urma', 'Comprehensive guide to functional programming, streams, lambdas, and modern reactive patterns in Java.', 44.00, 35, 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?auto=format&fit=crop&w=600&q=80', 1, 1, 3, NOW())
ON CONFLICT (id) DO NOTHING;

-- Synchronize sequence values so subsequent inserts work seamlessly
SELECT setval('categories_id_seq', COALESCE((SELECT MAX(id) FROM categories), 1));
SELECT setval('brands_id_seq', COALESCE((SELECT MAX(id) FROM brands), 1));
SELECT setval('products_id_seq', COALESCE((SELECT MAX(id) FROM products), 1));
