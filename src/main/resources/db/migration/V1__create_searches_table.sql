CREATE TABLE searches (
    id          NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    search_id   VARCHAR2(36)  NOT NULL,
    hotel_id    VARCHAR2(100) NOT NULL,
    check_in    DATE          NOT NULL,
    check_out   DATE          NOT NULL,
    ages        VARCHAR2(255) NOT NULL,
    CONSTRAINT uk_search_id UNIQUE (search_id)
);

CREATE INDEX idx_searches_hotel_checkin_checkout_ages
    ON searches (hotel_id, check_in, check_out, ages);

COMMIT;