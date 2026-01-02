#!/bin/bash
# Script to initialize databases for each service

# Wait for PostgreSQL to be ready
until pg_isready -h postgres -p 5432 -U postgres; do
  echo "Waiting for PostgreSQL..."
  sleep 2
done

echo "Creating databases for microservices..."

PGPASSWORD=$POSTGRES_PASSWORD psql -h postgres -U postgres <<EOF
CREATE DATABASE auth_db;
CREATE DATABASE user_db;
CREATE DATABASE branch_db;
CREATE DATABASE catalog_db;
CREATE DATABASE rental_db;
CREATE DATABASE reservation_db;
CREATE DATABASE feedback_db;
EOF

echo "Databases created successfully!"
