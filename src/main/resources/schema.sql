CREATE TABLE IF NOT EXISTS public.addresses
(
    address_id    VARCHAR(10) PRIMARY KEY,
    address_name  VARCHAR(100),
    address_level INTEGER,
    left_index    INTEGER,
    right_index   INTEGER,
    slug          VARCHAR(100)
);

CREATE OR REPLACE FUNCTION insert_data(parent_id VARCHAR, id VARCHAR, name VARCHAR, level INT)
    RETURNS VOID AS
$$
DECLARE
    myRightIndex INTEGER;
BEGIN
    SELECT right_index INTO myRightIndex FROM public.addresses WHERE address_id = parent_id;

    UPDATE public.addresses SET right_index = right_index + 2 WHERE right_index >= myRightIndex;
    UPDATE public.addresses SET left_index = left_index + 2 WHERE left_index > myRightIndex;

    INSERT INTO public.addresses (address_id, address_name, address_level, left_index, right_index)
    VALUES (id, name, level, myRightIndex, myRightIndex + 1);
END;
$$
    LANGUAGE plpgsql;

-- SELECT insert_data('001', '0001', 'Address 3', 3);

-- CREATE EXTENSION pg_trgm;
-- CREATE EXTENSION IF NOT EXISTS "unaccent";
--
-- CREATE OR REPLACE FUNCTION slugify("value" TEXT)
--     RETURNS TEXT AS
-- $$
-- WITH "unaccented" AS (SELECT unaccent("value") AS "value"),
--      "lowercase" AS (SELECT lower("value") AS "value"
--                      FROM "unaccented"),
--      "removed_quotes" AS (SELECT regexp_replace("value", '[''"]+', '', 'gi') AS "value"
--                           FROM "lowercase"),
--      "hyphenated" AS (SELECT regexp_replace("value", '[^a-z0-9\\-_]+', '-', 'gi') AS "value"
--                       FROM "removed_quotes"),
--      "trimmed" AS (SELECT regexp_replace(regexp_replace("value", '\-+$', ''), '^\-', '') AS "value"
--                    FROM "hyphenated")
-- SELECT "value"
-- FROM "trimmed";
-- $$ LANGUAGE SQL STRICT
--                 IMMUTABLE;


-- SELECT *
-- FROM public.addresses
-- WHERE SIMILARITY(address_name, 'Ha Loi') > 0.3;


-- find path
-- SELECT parent.*
-- FROM public.addresses,
--      public.addresses parent
-- WHERE public.addresses.left_index BETWEEN parent.left_index AND parent.right_index
--   AND public.addresses.address_id = '299'
-- ORDER BY public.addresses.left_index;

-- find children
-- SELECT child
-- FROM public.addresses node,
--      public.addresses child
-- WHERE (child.left_index BETWEEN node.left_index AND node.right_index)
--   AND (child.right_index BETWEEN node.left_index AND node.right_index)
--   AND child.address_id != '30'
--   AND node.address_id = '30';


-- WITH similarity_calc AS (SELECT *, SIMILARITY(slug, slugify('ha noi')) AS similarity_score FROM public.addresses)
-- SELECT *
-- FROM similarity_calc
-- WHERE similarity_score > 0.3
-- ORDER BY similarity_score DESC;

-- UPDATE public.addresses
-- SET slug = slugify(address_name);