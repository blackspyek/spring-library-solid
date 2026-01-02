# Library Microservices - Kubernetes Deployment

## Prerequisites
- Kubernetes cluster (minikube, Docker Desktop, or cloud)
- kubectl configured
- NGINX Ingress Controller installed

## Quick Start

### 1. Apply manifests in order:

```bash
# Create namespace
kubectl apply -f k8s/namespace.yaml

# Create config and secrets
kubectl apply -f k8s/config.yaml

# Deploy PostgreSQL
kubectl apply -f k8s/postgres.yaml

# Wait for PostgreSQL to be ready
kubectl wait --for=condition=ready pod -l app=postgres -n library --timeout=120s

# Create databases (run this in postgres pod)
kubectl exec -it postgres-0 -n library -- psql -U postgres -c "CREATE DATABASE auth_db; CREATE DATABASE user_db; CREATE DATABASE branch_db; CREATE DATABASE catalog_db; CREATE DATABASE rental_db; CREATE DATABASE reservation_db; CREATE DATABASE feedback_db;"

# Deploy all services
kubectl apply -f k8s/user-service.yaml
kubectl apply -f k8s/auth-service.yaml
kubectl apply -f k8s/branch-service.yaml
kubectl apply -f k8s/catalog-service.yaml
kubectl apply -f k8s/rental-service.yaml
kubectl apply -f k8s/reservation-service.yaml
kubectl apply -f k8s/feedback-service.yaml

# Deploy Ingress
kubectl apply -f k8s/ingress.yaml
```

### 2. Build Docker images (from project root):

```bash
# Build all services
./mvnw clean package -DskipTests
docker build -t k8libraryproject/user-service:latest ./user-service
docker build -t k8libraryproject/auth-service:latest ./auth-service
docker build -t k8libraryproject/branch-service:latest ./branch-service
docker build -t k8libraryproject/catalog-service:latest ./catalog-service
docker build -t k8libraryproject/rental-service:latest ./rental-service
docker build -t k8libraryproject/reservation-service:latest ./reservation-service
docker build -t k8libraryproject/feedback-service:latest ./feedback-service
```

### 3. Access the services:

Add to `/etc/hosts` (or C:\Windows\System32\drivers\etc\hosts):
```
127.0.0.1 library.local
```

Then access: `http://library.local/api/<service>`

## Verify Deployment

```bash
# Check all pods
kubectl get pods -n library

# Check services
kubectl get svc -n library

# Check ingress
kubectl get ingress -n library
```
