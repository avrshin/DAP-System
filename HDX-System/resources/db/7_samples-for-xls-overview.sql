INSERT INTO import_from_ckan(
            id, resource_id, revision_id, "timestamp")
    VALUES (1, 'dummy', 'dummy', '1970-01-01');

INSERT INTO text(id, default_value) VALUES (1000, 'Wikipedia: geography');
/*
INSERT INTO text(id, default_value) VALUES (1001, 'Url');
INSERT INTO hdx_unit(id, code, text_id) VALUES (7, 'Url', 1001);
INSERT INTO indicator_type(id, code, text_id, unit_id, value_type) VALUES (8, 'CD010', 1000, 13, 'STRING');
*/

INSERT INTO hdx_indicator(
            id, end_time, initial_value, periodicity, source_link, start_time, 
            string_value, entity_id, 
            import_from_ckan_id, source_id, type_id)
    VALUES (100000, '2011-01-01', 'Url for USA', 'YEAR', 'Wikipedia: geography', '2010-01-01', 
               'Url for USA', 230, 
            1, 1, 19);

ALTER SEQUENCE text_seq RESTART WITH 1100;
/*
ALTER SEQUENCE indicator_type_seq RESTART WITH 9;
ALTER SEQUENCE hdx_unit_seq RESTART WITH 8;
*/