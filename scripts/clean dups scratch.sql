select count(*) from weather_sensor_messages wsm group by "timestamp" having count(*) > 1


delete from weather_sensor_messages a using (
	select min(id) id, "timestamp" from weather_sensor_messages wsm group by "timestamp" having count(*) > 1
) b
where a."timestamp" = b."timestamp" 
and a.id <> b.id



delete from weather_sensor_messages 
where voltage < 0 
or voltage > 4