DROP TABLE IF EXISTS sensors;
 
CREATE TABLE sensors (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  sensor_name VARCHAR(255) NOT NULL,
  sensor_class VARCHAR(255) NOT NULL,
  sensor_version VARCHAR(255) NOT NULL,
  sensor_author VARCHAR(255) NULL
);
 
INSERT INTO sensors (sensor_name, sensor_class, sensor_version, sensor_author) VALUES
  ('Keyboard And Mouse', 'KeyboardMouseSensor', '0.0.2', 'Luis-Eduardo Imbernón');