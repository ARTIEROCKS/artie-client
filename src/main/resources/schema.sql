DROP TABLE IF EXISTS sensors;
 
CREATE TABLE sensors (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  sensor_file VARCHAR(255) NOT NULL,
  sensor_port BIGINT(255) NOT NULL,
  management_port BIGINT(255) NOT NULL,	
  sensor_name VARCHAR(255) NOT NULL
);