select * from "values" v, messages m, names n 
where 
	v.sensormessage_id = m.id 
	and n."name" = 'voltage' 
	and n.id = v.parametername_id 
	and v.value > 10;
	

select * from messages m 
join "values" v2 on v2.sensormessage_id = m.id 
where
m.rssi < -77;


select * from "values" v 
join names n on n.id v.parametername_id 
where n."name" = 'voltage' and v.value > 10;


delete from "values" where id = 1641 or id = 1644;



delete from weather_sensor_messages where voltage > 10 or voltage < 0;
delete from weather_sensor_messages where pressure > 130000;
delete from weather_sensor_messages where humidity > 100 or humidity < 0;



select pressure from weather_sensor_messages wsm order by pressure desc limit 10;





SELECT
  messages.timestamp AS "time",
  values.value
FROM "values",  messages, names
where
	
  names.name = 'voltage' AND
  values.parametername_id = names.id
ORDER BY 1 limit 1;

  $__timeFilter("timestamp")


SELECT
  "timestamp" AS "time",
  "values".value
FROM messages,values,names
where
  names.name = 'voltage' and
  values.parametername_id = names.id and
  messages.id = "values".sensormessage_id
ORDER BY 1 limit 10;



select * from names n 


delete from "values" 
where value < 96000 and parametername_id = 17













