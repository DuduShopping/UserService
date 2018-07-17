CREATE TABLE Users (
  UserId BIGINT PRIMARY KEY IDENTITY,
  Login VARCHAR(20) NOT NULL UNIQUE,
  Password VARCHAR(100) NOT NULL,
  CreatedOn DATETIME NOT NULL DEFAULT SYSDATETIME(),
  LastLogin DATETIME NULL,
  LoginAttempts INT NOT NULL DEFAULT 0,
)