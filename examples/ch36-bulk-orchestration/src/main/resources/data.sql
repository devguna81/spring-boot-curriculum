INSERT INTO member (id, name, joined_year, dormant) VALUES
    (1, 'kim',  2018, FALSE),
    (2, 'lee',  2024, FALSE),
    (3, 'park', 2019, TRUE),
    (4, 'choi', 2025, FALSE),
    (5, 'jung', 2020, FALSE);

INSERT INTO reward_ledger (member_id, amount) VALUES
    (1, 500), (1, 300), (1, -200),
    (2, 100),
    (4, 1000), (4, -400);

INSERT INTO orders (member_id, payable) VALUES
    (1, 12000), (1, 8000), (1, 30000),
    (2, 5000),
    (5, 7000), (5, 9000);
