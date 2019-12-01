package artie.sensor.client.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import artie.sensor.client.mapper.SensorRowMapper;
import artie.sensor.client.model.Sensor;

@Repository
public class SensorRepository {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	public List<Sensor> findAll() {
        return jdbcTemplate.query("select * from sensors", new SensorRowMapper());
    }
	
	public Optional<Sensor> findById(long id) {
        return Optional.of(jdbcTemplate.queryForObject("select * from sensors where id=?", new Object[] {id}, new BeanPropertyRowMapper<Sensor> (Sensor.class)));
    }
	
	public int deleteById(long id) {
        return jdbcTemplate.update("delete from sensors where id=?", new Object[] {id});
    }
	
	public int insert(Sensor sensor) {
        return jdbcTemplate.update("insert into sensors (id, sensor_name, sensor_file, sensor_class) " + "values(?, ?, ?, ?)",
            new Object[] {
                sensor.getId(), sensor.getSensor_name(), sensor.getSensor_file(), sensor.getSensor_class()
            });
    }
	
	public int update(Sensor sensor) {
        return jdbcTemplate.update("update sensors " + " set sensor_name = ?, sensor_file = ?, sensor_class = ? " + " where id = ?",
            new Object[] {
                sensor.getSensor_name(), sensor.getSensor_file(), sensor.getSensor_class(), sensor.getId()
            });
    }

}
