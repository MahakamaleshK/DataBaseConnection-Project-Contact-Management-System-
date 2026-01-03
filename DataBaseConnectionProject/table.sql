DROP DATABASE IF EXISTS ContactManagementSystem;

CREATE DATABASE ContactManagementSystem;
USE ContactManagementSystem;

CREATE TABLE users (
    userId INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE contacts (
    contactId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    mobileNumber VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    dob DATE,
    secondNumber VARCHAR(15),
    insta_id VARCHAR(50),
    memory TEXT,
    status ENUM('active','inactive') DEFAULT 'active',
    isEmergency TINYINT(1) DEFAULT 0,
    `group` VARCHAR(50),

    FOREIGN KEY (userId) REFERENCES users(userId)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE trash (
    id INT AUTO_INCREMENT PRIMARY KEY,
    contactId INT NOT NULL,
    userId INT NOT NULL,
    deletedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (contactId) REFERENCES contacts(contactId),
    FOREIGN KEY (userId) REFERENCES users(userId)
    ON DELETE CASCADE ON UPDATE CASCADE
);

