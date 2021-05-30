CREATE DATABASE udee;
USE udee;


CREATE TABLE users
(
    id             INT AUTO_INCREMENT,
    email          VARCHAR(80)                NOT NULL,
    dni            INT                        NOT NULL,
    user_type_enum ENUM ('CLIENT','EMPLOYEE') NOT NULL,
    NAME           VARCHAR(80)                NOT NULL,
    last_name      VARCHAR(80)                NOT NULL,
    pass           VARCHAR(100)               NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);


CREATE TABLE payments
(
    id      INT AUTO_INCREMENT,
    bill_id INT NOT NULL,
    amount  DECIMAL(13, 1),
    DATE    DATE,
    CONSTRAINT pk_payment PRIMARY KEY (id),
    CONSTRAINT fk_payment_bill FOREIGN KEY (bill_id) REFERENCES bills (id) ON DELETE CASCADE
);

CREATE TABLE bills
(
    id                 INT AUTO_INCREMENT,
    user_id            INT   NOT NULL,
    electric_meter_id  INT   NOT NULL,
    initial_measure_id BIGINT,
    final_measure_id   BIGINT,
    `usage`            INT,
    rate_id            INT,
    total              FLOAT NOT NULL,
    DATE               DATE,
    expiration		DATE,
    CONSTRAINT pk_bill PRIMARY KEY (id),
    CONSTRAINT fk_user_bill FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_electric_meter_bill FOREIGN KEY (electric_meter_id) REFERENCES electric_meters (id),
    CONSTRAINT fk_initial_measure FOREIGN KEY (initial_measure_id) REFERENCES measures (id),
    CONSTRAINT fk_final_measure FOREIGN KEY (final_measure_id) REFERENCES measures (id),
    CONSTRAINT fk_rate_bill FOREIGN KEY (rate_id) REFERENCES rates (id)
);


CREATE TABLE addresses
(
    id          INT AUTO_INCREMENT,
    street      VARCHAR(60) NOT NULL,
    num         INT         NOT NULL,
    postal_code VARCHAR(15) NOT NULL,
    floor_unit  VARCHAR(10),
    residence_id INT,
    CONSTRAINT pk_address PRIMARY KEY (id),
    CONSTRAINT fk_address_residence FOREIGN KEY(residence_id) REFERENCES residences(id) ON DELETE CASCADE
);

CREATE TABLE residences
(
    id                INT AUTO_INCREMENT,
    electric_meter_id INT,
    rate_id           INT,
    user_id           INT,
    CONSTRAINT pk_residence PRIMARY KEY (id),
    CONSTRAINT fk_electric_meter_id FOREIGN KEY (electric_meter_id) REFERENCES electric_meters (id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);


CREATE TABLE rates
(
    id       INT AUTO_INCREMENT,
    NAME     VARCHAR(30) NOT NULL,
    priceXKW FLOAT       NOT NULL,
    CONSTRAINT pk_rates PRIMARY KEY (id)
);

CREATE TABLE models
(
    id INT AUTO_INCREMENT,
    NAME VARCHAR(40),
    brand_id INT,
    CONSTRAINT pk_model PRIMARY KEY(id),
    CONSTRAINT fk_model_brand FOREIGN KEY(brand_id) REFERENCES brands(id)
);

CREATE TABLE brands
(
    id   INT AUTO_INCREMENT,
    NAME VARCHAR(40) UNIQUE,
    CONSTRAINT pk_brand PRIMARY KEY (id)
);

CREATE TABLE electric_meters
(
    id       INT AUTO_INCREMENT,
    SERIAL   VARCHAR(80) UNIQUE NOT NULL,
    model_id    INT,
    pass     VARCHAR(100)       NOT NULL,
    CONSTRAINT pk_electric_meter PRIMARY KEY (id),
    CONSTRAINT fk_meter_model FOREIGN KEY (model_id) REFERENCES models (id)
);


CREATE TABLE measures
(
    electric_meter_id INT      NOT NULL,
    id                BIGINT AUTO_INCREMENT,
    `datetime`        DATETIME NOT NULL,
    measure INT NOT NULL,
    `usage` INT,
    price             FLOAT DEFAULT 0,
    CONSTRAINT pk_measure PRIMARY KEY (id),
    CONSTRAINT fk_electric_meter_id_meas FOREIGN KEY (electric_meter_id) REFERENCES electric_meters (id)
);


#------------------------------BILLING EVENT---------------------------------------------------------------------------------------------------------
DELIMITER $$
CREATE PROCEDURE billing(electricId VARCHAR(80), userId INT, pRateId INT)
proc_label:
BEGIN
    DECLARE first_measure_id INT;
    DECLARE last_measure_id INT;
    DECLARE usg INT;
    START TRANSACTION;
#guardo id de ultima medicion de la ultima factura
    SELECT m.id
    INTO first_measure_id
    FROM bills b
             JOIN measures m ON b.final_measure_id = m.id
    WHERE b.electric_meter_id = electricId
    ORDER BY m.`datetime` DESC
    LIMIT 1;
#guardo id de ultima medicion 	
    SELECT id INTO last_measure_id FROM measures WHERE electric_meter_id = electricId ORDER BY `datetime` DESC LIMIT 1;
#si es la primer factura hace la resta de la medicion mas nueva, sino usa la nueva directo 
    IF(last_measure_id IS NOT NULL) THEN
	    IF (first_measure_id IS NOT NULL) THEN
		#Si la ultima medicion no es la ultima facturada (por si paso un mes sin mediciones..)
		IF (last_measure_id != first_measure_id) THEN
		    SET usg = (SELECT measure FROM measures WHERE id = last_measure_id) -
			      (SELECT measure FROM measures WHERE id = first_measure_id);
		ELSE
		    LEAVE proc_label;
		    ROLLBACK;
		END IF;

	    ELSE
		SET usg = (SELECT measure FROM measures WHERE id =last_measure_id);
	    END IF;

	    INSERT INTO bills(user_id, rate_id, electric_meter_id, initial_measure_id, final_measure_id, `usage`, total, DATE)
	    VALUES (userId, pRateId, electricId, first_measure_id, last_measure_id, usg,
		    (SELECT `priceXKW` FROM rates WHERE id = pRateId) * usg, CURDATE());
    END IF;
    COMMIT;
END $$
DELIMITER ;


DELIMITER $$
CREATE PROCEDURE billing_all()
BEGIN
    DECLARE electric_meter_id INT;
    DECLARE rate_id INT;
    DECLARE user_id INT;
    DECLARE vFinished INTEGER DEFAULT 0;
    DECLARE cur_residences CURSOR FOR SELECT residences.electric_meter_id, residences.rate_id, residences.user_id
                                      FROM residences;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET vFinished = 1;
    OPEN cur_residences;

    billing_residences:
    LOOP
        FETCH cur_residences INTO electric_meter_id,rate_id,user_id;
        IF vFinished = 1 THEN
            LEAVE billing_residences;
        END IF;
        CALL billing(electric_meter_id, user_id, rate_id);

    END LOOP billing_residences;
    CLOSE cur_residences;

END $$
DELIMITER ;

DELIMITER $$
CREATE EVENT billing_event ON SCHEDULE EVERY 1 MONTH
STARTS "2021-07-01 00:00:00" DO
BEGIN 
	CALL billing_all();
END $$
DELIMITER ;

#------------------------------------------------INDEXES----------------------------------------------------------------------------------


CREATE INDEX meter_serial_index ON electric_meters (SERIAL) USING HASH;

CREATE INDEX user_name_lastName ON users (NAME, last_name) USING HASH;

CREATE INDEX user_email ON users (email) USING HASH;

CREATE INDEX user_type ON users (user_type_enum) USING HASH;

CREATE INDEX measure_date ON measures (DATETIME) USING BTREE;

CREATE INDEX bill_date ON bills (DATE) USING BTREE;

CREATE INDEX payment_date ON payments (DATE) USING BTREE;

#--------------------REPORTE DE MEDICIONES POR FECHA Y USUARIO---------------------------------------------------------------------------------------------------
DROP PROCEDURE measure_report
DELIMITER $$
CREATE PROCEDURE measure_report(pUserId INT, pFrom DATE, pTo DATE)
BEGIN
	SELECT u.id AS user_id,u.email,u.dni,u.user_type_enum,u.name,u.last_name,
	em.id AS meter_id,em.serial,mo.id AS model_id,mo.name,br.name,
	m.id AS measure_id,m.datetime,m.measure,m.usage,m.price
	FROM measures m
		 JOIN electric_meters em ON em.id = m.electric_meter_id
		 JOIN models mo ON mo.id= em.model_id
		 JOIN brands br ON br.id=mo.brand_id
		 JOIN residences r ON r.electric_meter_id = em.id
		 JOIN users u ON r.user_id = u.id
	WHERE u.id = pUserId AND (m.datetime BETWEEN pFrom AND pTo)
	GROUP BY m.id;
END $$
DELIMITER ;
CALL measure_report(1,"2020-01-01","2022-01-01");

#TRIGGER AL AGREGAR MEASURE NUEVA----------------------------------------------------------------------------------------------------
DELIMITER $$
CREATE TRIGGER measure_price_update
    BEFORE INSERT
    ON measures
    FOR EACH ROW
BEGIN
    DECLARE vLDate DATETIME DEFAULT NULL;
    DECLARE vLMeasure INT DEFAULT 0;
    DECLARE vRatePrice FLOAT;
    SELECT r.priceXKW INTO vRatePrice FROM rates r JOIN residences res ON r.id = res.rate_id WHERE res.electric_meter_id = new.electric_meter_id;
    SELECT MAX(DATETIME) INTO vLDate FROM measures
    WHERE electric_meter_id = new.electric_meter_id
      AND DATETIME < new.datetime;
    IF (vLDate IS NOT NULL) THEN
        SELECT measure
        INTO vLMeasure
        FROM measures
        WHERE electric_meter_id = new.electric_meter_id AND DATETIME = vLDate;
        SET new.`usage`=(new.measure - vLMeasure);
    ELSE
	SET new.`usage`=new.measure;	
    END IF;
    SET new.price = new.`usage`  * vRatePrice;
END $$
DELIMITER ;

#TRIGGER DE UPDATE DE RATE------------------------------------------------------------------------------------------
DROP TRIGGER update_rate;
DELIMITER $$
CREATE TRIGGER update_rate
    AFTER UPDATE
    ON rates
    FOR EACH ROW
BEGIN
    UPDATE measures m JOIN residences res ON res.electric_meter_id = m.electric_meter_id
	SET m.price=(m.price/old.priceXKW)*new.priceXKW
	WHERE res.rate_id = old.id;
    CALL billing_all_adjustment(old.priceXKW,new.priceXKW, old.id);
END;
$$
DELIMITER ;


DELIMITER $$
CREATE PROCEDURE billing_all_adjustment(oldPriceXKW FLOAT,newPriceXKW FLOAT, pRate_Id INT)
BEGIN
    DECLARE newTotal FLOAT;
    DECLARE vElectric_meter_id INT;
    DECLARE user_id INT;
    DECLARE vUsage INT;
    DECLARE vFinished INTEGER DEFAULT 0;
    DECLARE cur_residences CURSOR FOR SELECT residences.electric_meter_id, residences.rate_id, residences.user_id FROM residences;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET vFinished = 1;
    OPEN cur_residences;
    billing_residences:
    LOOP
        FETCH cur_residences INTO vElectric_meter_id,user_id;
        IF vFinished = 1 THEN
            LEAVE billing_residences;
        END IF;
        #
        SET newTotal = NULL;
        SET vUsage = NULL;
        #suma de los usage not null de todas las facturas con ese rate    
        SELECT SUM(`usage`)
		FROM bills b
		WHERE b.electric_meter_id = vElectric_meter_id AND b.rate_id=pRate_Id
		INTO vUsage;
	#diferencia entre lo facturado con el precio viejo y el nuevo /talvez sumar aca las otras facturas de ajuste/
        SELECT (vUsage*newPriceXKW)-(vUsage*oldPriceXKW) INTO newTotal;
        IF (newTotal IS NOT NULL) THEN
            INSERT INTO bills (user_id, electric_meter_id, rate_id, total, DATE) VALUES (user_id, vElectric_meter_id, pRate_Id, newTotal, CURDATE());
        END IF;
    END LOOP billing_residences;
    CLOSE cur_residences;
END $$
DELIMITER ;

#TRIGGER FECHA DE VENCIMIENTO DE FACTURAS--------------------------------------------------------------------------------------------
DELIMITER $$
CREATE TRIGGER bill_expiration BEFORE INSERT ON bills FOR EACH ROW
BEGIN
	SET new.expiration=DATE_ADD(new.date,INTERVAL 15 DAY);
END $$
DELIMITER ;

#USUARIOS DE BASE DE DATOS------------------------------------------------------------------------------------------------------

CREATE USER 'Clients'@'%' IDENTIFIED BY 'guaymadefruta';

GRANT SELECT ON udee.measures TO 'Clients'@'%';
GRANT SELECT ON udee.electric_meters TO 'Clients'@'%';
GRANT SELECT ON udee.residences TO 'Clients'@'%';
GRANT SELECT ON udee.addresses TO 'Clients'@'%';
GRANT SELECT ON udee.rates TO 'Clients'@'%';
GRANT SELECT (dni,`email`,`NAME`,`last_name`) ON udee.users  TO 'Clients'@'%';


CREATE USER 'Backoffice'@'%' IDENTIFIED BY 'guaymadefruta';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.electric_meters TO 'Backoffice'@'%';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.brands TO 'Backoffice'@'%';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.models TO 'Backoffice'@'%';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.rates TO 'Backoffice'@'%';
GRANT SELECT (email,dni,NAME,last_name) ON udee.users TO 'Backoffice'@'%';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.residences TO 'Clients'@'%';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.addresses TO 'Clients'@'%';


CREATE USER 'Meters'@'%' IDENTIFIED BY 'guaymadefruta';
GRANT INSERT ON udee.measures TO 'Meters'@'%';


CREATE USER 'Billing'@'localhost' IDENTIFIED BY 'guaymadefruta';
GRANT EXECUTE ON PROCEDURE udee.billing TO 'Billing'@'localhost';
GRANT EXECUTE ON PROCEDURE udee.billing_all TO 'Billing'@'localhost';
GRANT EXECUTE ON PROCEDURE udee.billing_all_adjustment TO 'Billing'@'localhost';


