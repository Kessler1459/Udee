CREATE DATABASE udee;
USE udee;


DROP TABLE users;
CREATE TABLE users(
	id INT AUTO_INCREMENT,
	email VARCHAR(80) NOT NULL,
	dni INT NOT NULL,
	user_type_enum ENUM('CLIENT','EMPLOYEE') NOT NULL,
	NAME VARCHAR (80) NOT NULL,
	last_name VARCHAR(80) NOT NULL,
	pass VARCHAR(100) NOT NULL,
	CONSTRAINT pk_users PRIMARY KEY(id)
	);
SELECT * FROM users;
	
#talvez verificacion de user type con trigger
#index hash para users

DROP TABLE payments
CREATE TABLE payments(
	id INT AUTO_INCREMENT,
	bill_id INT NOT NULL,
	amount DECIMAL(13,1),
	DATE DATE,
	CONSTRAINT pk_payment PRIMARY KEY(id),
	CONSTRAINT fk_payment_bill FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE);

DROP TABLE bills;
CREATE TABLE bills(
	id INT AUTO_INCREMENT,
	user_id INT NOT NULL,
	electric_meter_id INT NOT NULL,
	initial_measure_id BIGINT,
	final_measure_id BIGINT,
	`usage` INT,
	rate_id INT,
	total FLOAT NOT NULL,
	DATE DATE,
	CONSTRAINT pk_bill PRIMARY KEY(id),
	CONSTRAINT fk_user_bill FOREIGN KEY (user_id) REFERENCES users(id),
	CONSTRAINT fk_electric_meter_bill FOREIGN KEY(electric_meter_id) REFERENCES electric_meters(id),
	CONSTRAINT fk_initial_measure FOREIGN KEY(initial_measure_id) REFERENCES measures (id),
	CONSTRAINT fk_final_measure FOREIGN KEY(final_measure_id) REFERENCES measures (id),
	CONSTRAINT fk_rate_bill FOREIGN KEY(rate_id) REFERENCES rates(id)
);

CREATE TABLE addresses(
	id INT AUTO_INCREMENT,
	street VARCHAR(60) NOT NULL,
	num INT NOT NULL,
	postal_code VARCHAR(15) NOT NULL,
	floor_unit VARCHAR(10),
	CONSTRAINT pk_address PRIMARY KEY(id));

SELECT * FROM addresses;
DELETE FROM addresses;

DROP TABLE IF EXISTS residences;
CREATE TABLE residences(
	id INT AUTO_INCREMENT,
	address_id INT,
	electric_meter_id INT,
	rate_id INT,
	user_id INT,
	CONSTRAINT pk_residence PRIMARY KEY (id),
	CONSTRAINT fk_electric_meter_id FOREIGN KEY (electric_meter_id) REFERENCES electric_meters(id),
	CONSTRAINT fk_address FOREIGN KEY (address_id) REFERENCES addresses(id),
	CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
	);


CREATE TABLE rates(
	id INT AUTO_INCREMENT,
	NAME VARCHAR(30) NOT NULL,
	priceXKW FLOAT NOT NULL,
	CONSTRAINT pk_rates PRIMARY KEY (id)
);

DROP TABLE brands;
CREATE TABLE brands(
	id INT AUTO_INCREMENT,
	NAME VARCHAR(40) UNIQUE,
	CONSTRAINT pk_brand PRIMARY KEY(id));
SELECT * FROM brands;
	
DELETE FROM brands WHERE id=4;
SELECT * FROM brands;
	
DROP TABLE electric_meters;
CREATE TABLE electric_meters(
	id INT AUTO_INCREMENT,
	SERIAL VARCHAR(80) UNIQUE NOT NULL,  
	brand_id INT ,
	model VARCHAR(50),
	pass VARCHAR(100) NOT NULL,
	CONSTRAINT pk_electric_meter PRIMARY KEY(id),
	CONSTRAINT fk_brand FOREIGN KEY (brand_id) REFERENCES brands (id)
	);
	
EXPLAIN SELECT * FROM `electric_meters`;

CREATE TABLE measures(
	electric_meter_id INT NOT NULL,
	id BIGINT AUTO_INCREMENT,
	`datetime` DATETIME NOT NULL,
	`usage`INT NOT NULL,
	CONSTRAINT pk_measure PRIMARY KEY (id),
	CONSTRAINT fk_electric_meter_id_meas FOREIGN KEY (electric_meter_id) REFERENCES electric_meters(id)
	);

DROP TABLE measures	


#--------------------------------------------------------------------------------------------------------------------------

SELECT b.id AS brandId,b.name AS brandName,E.id AS id,E.model AS model,E.serial AS SERIAL FROM electric_meters E JOIN brands b ON b.id=E.brand_id WHERE E.serial="sdf4445"

SELECT * FROM addresses
DELETE FROM residences WHERE id=7
SELECT * FROM residences
SELECT * FROM rates
SELECT * FROM payments;
SELECT * FROM measures	
SELECT * FROM bills;
SELECT * FROM users;	
SELECT * FROM electric_meters



DROP PROCEDURE billing;

#------------------------------PROCEDURES---------------------------------------------------------------------------------------------------------
DELIMITER $$
CREATE PROCEDURE billing(electricId VARCHAR(80),userId INT, rateId INT)
proc_label:BEGIN
DECLARE first_measure_id INT;
DECLARE last_measure_id INT;
DECLARE usg INT;

START TRANSACTION;
#guardo id de ultima medicion de la ultima factura
SELECT m.id INTO first_measure_id FROM bills b
JOIN measures m ON b.final_measure_id=m.id
WHERE b.electric_meter_id=electricId
ORDER BY m.`datetime` DESC LIMIT 1;

#guardo id de ultima medicion 	
SELECT id INTO last_measure_id FROM measures WHERE electric_meter_id=electricId ORDER BY `datetime` DESC LIMIT 1;

#si es la primer factura hace la resta de la medicion mas nueva, sino usa la nueva directo 
IF(first_measure_id IS NOT NULL )THEN
	#Si la ultima medicion no es la ultima facturada (por si paso un mes sin mediciones..)
	IF(last_measure_id!=first_measure_id)THEN  
		SET usg=(SELECT `usage` FROM measures WHERE id=last_measure_id)-(SELECT `usage` FROM measures WHERE id=first_measure_id);
		ELSE
		LEAVE proc_label;
		ROLLBACK;
	END IF;
	
ELSE
	SET usg=(SELECT `usage` FROM measures WHERE id=last_measure_id);

END IF;

INSERT INTO bills(user_id,rate_id,electric_meter_id,initial_measure_id,final_measure_id,`usage`,total,DATE)
VALUES (userId,rateId,electricId,first_measure_id,last_measure_id,usg,(SELECT `priceXKW` FROM rates WHERE id=rateId)*usg,CURDATE());
COMMIT;
END $$
DELIMITER ;

#recorro residences con cursor
CALL billing(1,143234,1);
DELETE FROM bills;

SELECT * FROM bills

	

DELIMITER $$
CREATE PROCEDURE billing_all()
BEGIN
DECLARE electric_meter_id INT;
DECLARE rate_id INT;
DECLARE user_id INT;
DECLARE vFinished INTEGER DEFAULT 0;
DECLARE cur_residences CURSOR FOR SELECT residences.electric_meter_id,residences.rate_id,residences.user_id FROM residences;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET vFinished = 1;
OPEN cur_residences;

billing_residences: LOOP
	FETCH cur_residences INTO electric_meter_id,rate_id,user_id;
	IF vFinished =1 THEN LEAVE billing_residences; 
	END IF;
	CALL billing(electric_meter_id,user_id,rate_id);

END LOOP billing_residences;
CLOSE cur_residences;

END $$
DELIMITER ;

DROP PROCEDURE billing_all;

CALL billing_all();

#------------------------------------------------INDEX----------------------------------------------------------------------------------


CREATE INDEX meter_serial_index ON electric_meters(SERIAL) USING HASH

CREATE INDEX user_name_lastName ON users(NAME,last_name) USING HASH

CREATE INDEX user_email ON users(email) USING HASH

CREATE INDEX measure_date ON measures(DATETIME) USING BTREE

CREATE INDEX bill_date ON bills(DATE) USING BTREE

CREATE INDEX payment_date ON payments(DATE) USING BTREE



/*

● Cliente
● Medidor
● Fecha medición
● Medicion
● Consumo Kwh
● Consumo precio
PREGUNTAR SI POR RESIDENCE O POR USUARIO PORQUE NO TIENE SENTIDO POR USUARIO
*/
EXPLAIN SELECT * FROM measures m 
JOIN electric_meters e ON e.id=m.electric_meter_id
JOIN residences r ON r.electric_meter_id=e.id
JOIN users u ON r.user_id=u.id
JOIN rates ra ON ra.id=r.rate_id
WHERE u.id=1 AND (m.datetime BETWEEN "2020-01-01" AND "2022-01-01")

#TRIGGER PARA QUE AL ACTUALIZAR UN RATE SE ACTUALICEN LAS FACTURAS YA HECHAS
DROP TRIGGER update_rate
DELIMITER $$
CREATE TRIGGER update_rate AFTER UPDATE ON rates FOR EACH ROW
	BEGIN
		UPDATE bills b SET b.total=new.priceXKW*b.usage;
		CALL billing_all_adjustment(new.priceXKW,old.id);
	END; $$
DELIMITER ;

SELECT * FROM bills
SELECT * FROM payments
INSERT INTO payments(bill_id,amount,DATE) VALUES (26,40,CURDATE())
UPDATE rates r SET r.priceXKW=2 WHERE r.id=1

DELIMITER $$
CREATE PROCEDURE billing_all_adjustment(priceXKW FLOAT,pRate_Id INT)
BEGIN
DECLARE newTotal FLOAT;
DECLARE vElectric_meter_id INT;
DECLARE rate_id INT;
DECLARE user_id INT;
DECLARE vUsage INT;
DECLARE vFinished INTEGER DEFAULT 0;
DECLARE cur_residences CURSOR FOR SELECT residences.electric_meter_id,residences.rate_id,residences.user_id FROM residences;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET vFinished = 1;
OPEN cur_residences;
billing_residences: LOOP
	FETCH cur_residences INTO vElectric_meter_id,rate_id,user_id;
	IF vFinished =1 THEN LEAVE billing_residences; 
	END IF;
	#
	SET newTotal = NULL;
	SET vUsage = NULL;
	
	#select del usage not null de la ultima factura guardada     *      nuevo precio del rate
	SELECT `usage` FROM bills b  WHERE b.electric_meter_id=vElectric_meter_id AND `usage` IS NOT NULL ORDER BY DATE DESC LIMIT 1 INTO vUsage;
	#si la ultima bill no es tambien de ajuste
	IF(vUsage IS NOT NULL)THEN
		SELECT (SELECT (vUsage*priceXKW)-	
		(SELECT SUM(p.amount) FROM payments p JOIN bills b ON b.id=p.bill_id WHERE b.rate_id=pRate_Id AND b.electric_meter_id=vElectric_meter_id) ) INTO newTotal;
		IF (newTotal IS NOT NULL) THEN 
			INSERT INTO bills (user_id,electric_meter_id,rate_id,total,DATE) VALUES (user_id,vElectric_meter_id,rate_id,newTotal,CURDATE());
		END IF;
	#si la ultima es de ajuste la actualiza
	ELSE
		UPDATE bills b SET b.total=b.`usage`*priceXKW WHERE b.electric_meter_id=vElectric_meter_id AND b.usage IS NULL ORDER BY b.date DESC LIMIT 1;
	END IF;
	#
END LOOP billing_residences;
CLOSE cur_residences;
END $$
DELIMITER ;
#tabla modelos

