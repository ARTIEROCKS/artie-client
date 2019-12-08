package artie.sensor.client.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import artie.sensor.client.model.Sensor;

@Repository
public interface SensorRepository extends CrudRepository<Sensor, Long>{
	Optional<Sensor> findById(Long id);
	Optional<Sensor> findByOrderBySensorPortDesc();
}
