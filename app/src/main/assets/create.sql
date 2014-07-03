CREATE TABLE config_table(
   _id INTEGER PRIMARY KEY AUTOINCREMENT,
   Ciprofile TEXT NOT NULL UNIQUE,
   Hostname TEXT NOT NULL,
   Domain TEXT NOT NULL,
   Portnumber INTEGER NOT NULL,
   Username TEXT NOT NULL,
   Password TEXT NOT NULL
);

 