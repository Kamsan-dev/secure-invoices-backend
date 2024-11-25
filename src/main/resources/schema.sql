/*
 * --- GENERAL Rules ---
 * USER underscores_names instead OF CamelCase
 * TABLE names should be plural
 * Spell OUT id fields (item_id instead of id)
 * Don't use ambiguous COLUMN names
 * Name FOREIGN KEY columns the same AS the columns they refer TO
 * Use caps for all SQL queries 
*/

CREATE SCHEMA IF NOT EXISTS secureinvoices;

SET NAMES 'UTF8MB4';
USE secureinvoices;

DROP TABLE IF EXISTS AccountVerification;
DROP TABLE IF EXISTS TwoFactorVerifications;
DROP TABLE IF EXISTS ResetPasswordVerification;
DROP TABLE IF EXISTS UserEvents;
DROP TABLE IF EXISTS UserRoles;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users 
(
	user_id			BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	first_name 		VARCHAR(50) NOT NULL,
	last_name 		VARCHAR(50) NOT NULL,
	email 			VARCHAR(100) NOT NULL,
	password 		VARCHAR(255) DEFAULT NULL,
	address  		VARCHAR(255) DEFAULT NULL,
	phone 			VARCHAR(30) DEFAULT NULL,
	title 			VARCHAR(50) DEFAULT NULL,
	bio 			VARCHAR(255) DEFAULT NULL,
	enabled 		BOOLEAN DEFAULT FALSE,
	non_locked 		BOOLEAN DEFAULT TRUE,
	using_mfa 		BOOLEAN DEFAULT FALSE,
	created_at 		DATETIME DEFAULT CURRENT_TIMESTAMP,
	image_url 		VARCHAR(255) DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
	CONSTRAINT UQ_Users_Email UNIQUE (email)
);

/*INSERT INTO Users (first_name, last_name, email, password) 
VALUES ('John', 'Doe', 'john.doe@gmail.com', '1234');*/

-----------------------------------------------------------------

DROP TABLE IF EXISTS Roles;
CREATE TABLE Roles 
(
	role_id 		BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name 			VARCHAR(50) NOT NULL,
	permission 	VARCHAR(255) NOT NULL, -- user:read,user:delete,customer:READ
	CONSTRAINT UQ_Roles_Name UNIQUE (name)
);

INSERT INTO Roles (name, permission)
VALUES ('ROLE_USER', 'READ:USER, READ:CUSTOMER'),
	   ('ROLE_MANAGER', 'READ:USER, READ:CUSTOMER, UPDATE:USER, UPDATE:CUSTOMER'),
	   ('ROLE_ADMIN', 'READ:USER, READ:CUSTOMER, UPDATE:USER, UPDATE:CUSTOMER, CREATE:USER, CREATE:CUSTOMER'),
	   ('ROLE_SYSADMIN', 'READ:USER, READ:CUSTOMER, UPDATE:USER, UPDATE:CUSTOMER, CREATE:USER, CREATE:CUSTOMER, DELETE:USER, DELETE:CUSTOMER');


CREATE TABLE UserRoles 
(
	user_role_id 	BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id			BIGINT UNSIGNED NOT NULL,
	role_id			BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY (user_id) REFERENCES Users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (role_id) REFERENCES Roles (role_id) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT UQ_UserRoles_User_Id UNIQUE (user_id)
);

-----------------------------------------------------------------

DROP TABLE IF EXISTS Events;
CREATE TABLE Events 
(
	event_id 		BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	type 			VARCHAR(50) NOT NULL CHECK(type IN ('LOGIN_ATTEMPT', 'LOGIN_ATTEMPT_FAILURE', 'LOGIN_ATTEMPT_SUCCESS', 'PROFILE_UPDATE', 'PROFILE_PICTURE_UPDATE', 'ROLE_UPDATE', 'ACCOUNT_SETTINGS_UPDATE', 'PASSWORD_UPDATE', 'MFA_UPDATE')),
	description 	VARCHAR(255) NOT NULL,
	CONSTRAINT UQ_Events_Type UNIQUE (type)
);

CREATE TABLE UserEvents 
(
	user_event_id 	BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id			BIGINT UNSIGNED NOT NULL,
	event_id		BIGINT UNSIGNED NOT NULL,
	device 			VARCHAR(100) DEFAULT NULL,
	ip_address 		VARCHAR(100) DEFAULT NULL,
	occured_at 		DATETIME DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (user_id) REFERENCES Users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (event_id) REFERENCES Events (event_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-----------------------------------------------------------------

CREATE TABLE AccountVerification 
(
	account_verification_id 	BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id						BIGINT UNSIGNED NOT NULL,
	url 						VARCHAR(255) NOT NULL,
	FOREIGN KEY (user_id) REFERENCES Users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT UQ_AccountVerification_User_Id UNIQUE (user_id),
	CONSTRAINT UQ_AccountVerification_Url UNIQUE (url)
);

-----------------------------------------------------------------


CREATE TABLE ResetPasswordVerification 
(
	reset_password_verification_id 	BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id							BIGINT UNSIGNED NOT NULL,
	url 							VARCHAR(255) NOT NULL,
	expiration_date 				DATETIME NOT NULL,
	FOREIGN KEY (user_id) REFERENCES Users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT UQ_ResetPasswordVerification_User_Id UNIQUE (user_id),
	CONSTRAINT UQ_ResetPasswordVerification_Url UNIQUE (url)
);

-----------------------------------------------------------------


CREATE TABLE TwoFactorVerifications 
(
	two_factor_verification_id 		BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id							BIGINT UNSIGNED NOT NULL,
	code 							VARCHAR(10) NOT NULL,
	expiration_date 				DATETIME NOT NULL,
	FOREIGN KEY (user_id) REFERENCES Users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT UQ_TwoFactorVerifications_User_Id UNIQUE (user_id),
	CONSTRAINT UQ_TwoFactorVerifications_Code UNIQUE (code)
);

