-- Une base isolée par microservice (exigence "persistance isolée").
-- Exécuté automatiquement au premier démarrage du conteneur Postgres.
CREATE DATABASE customer_db;
CREATE DATABASE account_db;
CREATE DATABASE transaction_db;
CREATE DATABASE transfer_db;
CREATE DATABASE loan_db;
CREATE DATABASE notification_db;
