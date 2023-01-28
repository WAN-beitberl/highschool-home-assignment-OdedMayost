CREATE TABLE Students (
     ID INT,
     LastName VARCHAR(255) NOT NULL,
     FirstName VARCHAR(255) NOT NULL,
     Email VARCHAR(255),
     Gender VARCHAR(255),
     IP VARCHAR(255) NOT NULL,
     cmHeight INT NOT NULL,
     Age INT NOT NULL,
     HasCar BOOL NOT NULL,
     CarColor VARCHAR(255),
     Grade INT,
     GradeAVG FLOAT,
     IdentificationCard INT UNIQUE NOT NULL,
     PRIMARY KEY (ID)
);

CREATE TABLE StudentsFriendships (
	ID INT, 
	FriendID INT NOT NULL, 
	OtherFriendID INT NOT NULL, 
	PRIMARY KEY (ID)
);

CREATE VIEW StudentGradesView AS 
	SELECT Students.IdentificationCard AS Identification, 
	Students.GradeAVG AS GradeAVG 
	FROM Students 
	ORDER BY Students.ID;