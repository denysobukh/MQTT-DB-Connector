CREATE OR REPLACE FUNCTION cleanup()
RETURNS VOID
LANGUAGE plpgsql
AS $$
DECLARE
	start_time timestamp;
	end_time timestamp;
	time_ranges_arr timestamp[];
	time_range timestamp;
	ids integer[];
	t numeric;
	p numeric;
	h numeric;
BEGIN
	start_time := (SELECT timestamp FROM weather_sensor_messages ORDER BY timestamp ASC LIMIT 1);
	end_time := (SELECT timestamp FROM weather_sensor_messages ORDER BY timestamp DESC LIMIT 1);
	RAISE NOTICE '% - %', start_time, end_time;

	time_ranges_arr := ARRAY(
		SELECT generate_series(start_time, end_time, interval '1' minute)
	);
	FOREACH time_range IN ARRAY time_ranges_arr
	LOOP
   		--RAISE NOTICE 'M = %', date_trunc('minute', time_range);
		ids := ARRAY ( SELECT id FROM weather_sensor_messages WHERE date_trunc('minute', timestamp) = date_trunc('minute', time_range));
		IF array_length(ids, 1) > 1 THEN
			SELECT AVG(temperature), AVG(pressure), AVG(humidity) into t, p, h FROM weather_sensor_messages WHERE id = ANY(ids);
			--RAISE NOTICE 'id = %', ids;
			UPDATE weather_sensor_messages SET (temperature, pressure, humidity) = (t, p, h) WHERE id = ids[0];
			DELETE FROM weather_sensor_messages WHERE id = ANY(ids) AND id != ids[0];
		END IF;
	END LOOP;


	-- RAISE NOTICE '%', minutes;
	-- COMMIT;
END;
$$;
