# Jak włączyć aplikację
## 🛠️ Kroki przygotowawcze
Zanim uruchomisz terminale, upewnij się, że:
1. W pliku `hosts` (`C:\Windows\System32\drivers\etc\hosts`) masz wpis: 
   `127.0.0.1 library.local`.
2. Masz zainstalowane narzędzie **Skaffold**. 


5. minikube addons enable ingress

# 1️⃣ Utwórz namespace
kubectl apply -f k8s/namespace.yaml

# 2️⃣ Utwórz ConfigMap i Secrets
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secrets.yaml

# 3️⃣ Utwórz Postgresa i inicjalizację bazy
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/postgres-init.yaml
kubectl apply -f k8s/postgres-service.yaml


### Terminal 1: 
## minbikube start
## minikube docker-env | Invoke-Expression 
## skaffold dev -p dev

### Terminal 2: kubectl port-forward svc/postgres 5432:5432 -n library

### Terminal 3: minikube tunnel

>> wsl --shutdown 
>> diskpart
> DISKPART> select vdisk file="<path to vhdx file>"
> DISKPART> compact vdisk