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
		sensor.setSensorName(rs.getString("sensor_name"));
		sensor.setSensorClass(rs.getString("sensor_class"));
		sensor.setSensorFile(rs.getString("sensor_file"));
		return sensor;
	}

}
