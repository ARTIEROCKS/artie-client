DROP TABLE IF EXISTS sensors;
 
CREATE TABLE sensors (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  sensor_name VARCHAR(255) NOT NULL,
  sensor_file VARCHAR(255) NOT NULL,
  sensor_class VARCHAR(255) NOT NULL
);
 
INSERT INTO sensors (sensor_name, sensor_file, sensor_class) VALUES
  ('Keyboard And Mouse', '/home/luis/test.jar' ,'KeyboardMouseSensor');