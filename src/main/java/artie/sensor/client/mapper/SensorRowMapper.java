package artie.sensor.client.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import artie.sensor.client.model.Sensor;

public class SensorRowMapper implements RowMapper <Sensor> {

	@Override
	public Sensor mapRow(ResultSet rs, int rowNum) throws SQLException {
		Sensor sensor = new Sensor();
		sensor.setId(rs.getLong("id"));
		sensor.setSensor_name(rs.getString("sensor_name"));
		sensor.setSensor_class(rs.getString("sensor_class"));
		sensor.setSensor_file(rs.getString("sensor_file"));
		return sensor;
	}

}
