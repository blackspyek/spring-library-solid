# 📚 System Biblioteczny (Microservices)

System zarządzania biblioteką oparty na architekturze mikroserwisów (Spring Boot) oraz Angularze, wdrażany na platformę Kubernetes.

## 🛠️ Wymagania wstępne (Prerequisites)
Przed rozpoczęciem upewnij się, że masz zainstalowane następujące narzędzia:
1. **Docker Desktop** (lub inny silnik kontenerowy).
2. **Minikube** (Lokalny klaster Kubernetes).
3. **Skaffold** (Narzędzie do automatyzacji cyklu wdrożeniowego).
4. **Kubectl** (CLI do Kubernetesa).
5. **Ingress** (dostępny jako dodatek w Minikube).

# Jak włączyć aplikację
## 🛠️ Kroki przygotowawcze
Zanim uruchomisz terminale, upewnij się, że:
1. W pliku `hosts` (`C:\Windows\System32\drivers\etc\hosts`) masz wpis: 
   `127.0.0.1 library.local`.
   * **Windows:** `C:\Windows\System32\drivers\etc\hosts` (uruchom Notatnik jako Administrator).
   * **Linux/macOS:** `/etc/hosts` (użyj `sudo nano /etc/hosts`).
2. UZUPEŁNIJ: 
W pliku `sectets.example` znajdują się placeholdery dla haseł uzupełnij plik `k8/secrets`.
* Upewnij się plik został uzupełniony i zapisany jako `secrets.yaml` w katalogu `k8/`.


# 🚀 Uruchomienie aplikacji (krok po kroku)
Potrzebujesz dwóch okien terminala.

1. Krok 1: Start klastra i Ingress (Terminal 1)
> minikube start --docker-opt dns=8.8.8.8 --dns-proxy=true 

> minikube addons enable ingress

W tym samym terminalu uruchom Skaffold. Narzędzie to automatycznie:

* Zbuduje obrazy Dockerowe.
* Utworzy Namespace, ConfigMapy i Sekrety.
* Uruchomi bazę danych i mikroserwisy w odpowiedniej kolejności.

> skaffold dev -p dev

Poczekaj, aż wszystkie serwisy (postgres, auth, user, frontend...) uzyskają status "Running".

2. Krok 2: Tunelowanie sieci (Terminal 2)

Ingress w Minikube wymaga tunelu, aby być dostępnym pod lokalnym IP. Otwórz nowe okno terminala i wpisz:

>minikube tunnel

⚠️ Ważne: Nie zamykaj tego terminala! Musi on działać w tle, aby strona się ładowała.

# 🌐 Dostęp do aplikacji
Gdy system działa (Skaffold i Tunnel są aktywne), możesz korzystać z usług:

| Usługa | Adres URL | Opis |
| :--- | :--- | :--- |
| **Aplikacja Frontend** | [http://library.local](http://library.local) | Główny interfejs dla czytelników i pracowników |
| **Admin Dashboard** | [http://localhost:8088](http://localhost:8088) | Monitoring statusu mikroserwisów (Spring Boot Admin) |
| **API Gateway** | `http://library.local/api/` | Punkt wejścia dla zapytań backendowych |

# 🧹 Zatrzymywanie projektu
Aby poprawnie wyłączyć system i zwolnić zasoby:

1. W terminalu ze Skaffold wciśnij Ctrl + C (automatycznie usunie wdrożone pody i serwisy).

2. W terminalu z Tunnel wciśnij Ctrl + C.

3. Zatrzymaj klaster Minikube:
> minikube stop