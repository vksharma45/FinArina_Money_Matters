-- =============================================================
-- SAMPLE DATA — run manually after the app starts with ddl-auto: update
-- =============================================================

-- Stock Categories
INSERT INTO stock_categories (category_name, description) VALUES
('Technology',  'Tech sector stocks'),
('Banking',     'Banking sector stocks'),
('Pharma',      'Pharmaceutical stocks'),
('FMCG',        'Fast-moving consumer goods');

-- Portfolios
INSERT INTO portfolios (portfolio_name, created_date, initial_investment) VALUES
('My Main Portfolio',  '2024-01-15', 100000.00),
('Side Investments',   '2024-03-01',  50000.00);

-- Assets  (portfolio 1)
-- Holding stocks
INSERT INTO assets (portfolio_id, asset_name, asset_type, quantity, buy_price, current_price, is_wishlist, category_id) VALUES
(1, 'Reliance Industries',  'STOCK',       10,  2800.00, 3100.00, false, 1),
(1, 'HDFC Bank',            'STOCK',       20,  1600.00, 1750.00, false, 2),
(1, 'Sun Pharma',           'STOCK',       15,   900.00,  980.00, false, 3);

-- Holding non-stock
INSERT INTO assets (portfolio_id, asset_name, asset_type, quantity, buy_price, current_price, is_wishlist, category_id) VALUES
(1, 'SBI ETF Nifty',       'ETF',          5,  550.00,  610.00, false, NULL),
(1, 'HDFC Flexi Cap Fund', 'MUTUAL_FUND',  2, 5000.00, 5800.00, false, NULL);

-- Wishlist (buyPrice = NULL)
INSERT INTO assets (portfolio_id, asset_name, asset_type, quantity, buy_price, current_price, is_wishlist, category_id) VALUES
(1, 'Tata Consultancy',    'STOCK',        5,   NULL,  3800.00, true,  1),
(1, 'ICICI Prudential MF', 'MUTUAL_FUND', 1,   NULL, 12000.00, true,  NULL);

-- Assets  (portfolio 2)
INSERT INTO assets (portfolio_id, asset_name, asset_type, quantity, buy_price, current_price, is_wishlist, category_id) VALUES
(2, 'Wipro',               'STOCK',       25,  400.00,  460.00, false, 1),
(2, 'Axis Bank',           'STOCK',       30,  700.00,  780.00, false, 2);

-- Asset Groups (global)
INSERT INTO asset_groups (group_name, description, created_date) VALUES
('Large Cap Picks',  'Blue-chip holdings',            '2024-02-01'),
('Pharma & Tech',    'Sector diversification pick',   '2024-02-10'),
('Watchlist',        'Stocks under evaluation',       '2024-03-05');

-- Group Memberships
-- "Large Cap Picks" → Reliance (1), HDFC Bank (2), Wipro (9)
INSERT INTO asset_group_members (group_id, asset_id) VALUES (1, 1), (1, 2), (1, 9);
-- "Pharma & Tech"  → Sun Pharma (3), Reliance (1)
INSERT INTO asset_group_members (group_id, asset_id) VALUES (2, 3), (2, 1);
-- "Watchlist"      → TCS wishlist (7), ICICI MF wishlist (8)
INSERT INTO asset_group_members (group_id, asset_id) VALUES (3, 7), (3, 8);

-- Asset History (a few BUY records matching the holdings above)
INSERT INTO asset_history (asset_id, action_type, quantity_changed, price_at_that_time, action_date, remarks) VALUES
(1, 'BUY',          10,   2800.00, '2024-01-20', 'Initial purchase'),
(2, 'BUY',          20,   1600.00, '2024-01-22', 'Initial purchase'),
(3, 'BUY',          15,    900.00, '2024-02-05', 'Initial purchase'),
(1, 'PRICE_UPDATE', NULL,  3100.00, '2024-06-01', 'Price changed from 2950.00 to 3100.00'),
(2, 'QUANTITY_UPDATE', 5, 1700.00, '2024-05-10', 'Quantity changed from 15 to 20');

-- Credit Cards
INSERT INTO credit_cards (portfolio_id, card_name, credit_limit, outstanding_amount, due_date) VALUES
(1, 'HDFC MoneyBack',  200000.00,  45000.00, '2026-02-10'),
(1, 'ICICI Signature', 150000.00,  12000.00, '2026-01-28'),
(2, 'Axis Flipkart',   100000.00,      0.00, '2026-02-20');
