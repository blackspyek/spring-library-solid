# Pull Request — Wzorce projektowe: Facade + Proxy

## Opis

Implementacja dwóch wzorców projektowych **Facade** i **Proxy** w projekcie Spring Library Platform. Zmiany obejmują 3 mikroserwisy dla każdego wzorca (łącznie 6 refaktoryzacji).

---

## Wzorzec Facade

Uproszczenie interfejsów kontrolerów/serwisów poprzez ukrycie złożoności wielu podsystemów za jednym spójnym API.

### 1. CatalogFacade (`catalog-service`)

**Problem:** `ItemController` bezpośrednio zależał od dwóch serwisów (`ICatalogService`, `IBranchInventoryService`) i zawierał logikę mapowania DTO (`toDto()`, `toBranchInventoryDto()`).

**Rozwiązanie:** Nowa klasa `CatalogFacade` ukrywa oba serwisy i centralizuje logikę mapowania.

| Plik | Zmiana |
|------|--------|
| `catalog-service/.../facade/CatalogFacade.java` | **NOWY** — fasada łącząca 2 serwisy + mapping DTO |
| `catalog-service/.../controller/ItemController.java` | Uproszczony — 1 zależność zamiast 2, bez metod mapowania |

### 2. AuthFacade (`auth-service`)

**Problem:** `AuthController` zależał od `IAuthService` i `JwtTokenProvider`, zawierał logikę tworzenia cookie JWT.

**Rozwiązanie:** Nowa klasa `AuthFacade` łączy auth + JWT + cookie w jeden interfejs.

| Plik | Zmiana |
|------|--------|
| `auth-service/.../facade/AuthFacade.java` | **NOWY** — fasada auth + JWT + cookie |
| `auth-service/.../facade/LoginResult.java` | **NOWY** — DTO wyniku logowania |
| `auth-service/.../controller/AuthController.java` | Uproszczony — 1 zależność zamiast 2 |

### 3. RentalHistoryFacade (`rental-service`)

**Problem:** `RentalHistoryExportService` koordynował 3 podsystemy i zawierał zduplikowaną logikę łączenia danych historii z danymi katalogowymi.

**Rozwiązanie:** Nowa klasa `RentalHistoryFacade` eliminuje duplikację i upraszcza pobieranie wzbogaconych danych.

| Plik | Zmiana |
|------|--------|
| `rental-service/.../facade/RentalHistoryFacade.java` | **NOWY** — fasada eliminująca duplikację |
| `rental-service/.../service/RentalHistoryExportService.java` | Uproszczony z 113 → 35 linii |

---

## Wzorzec Proxy

Wydzielenie logiki przekrojowej (caching, kontrola dostępu) do dedykowanych klas proxy opakowujących oryginalne serwisy.

### 1. CachingBookServiceProxy — Caching Proxy (`catalog-service`)

**Problem:** `BookService` zawierał ręczną logikę cachowania (`CatalogCacheManager.INSTANCE.get/put`) wmieszaną w metody biznesowe `getTopGenres()` i `getOtherGenres()`.

**Rozwiązanie:** Nowa klasa `CachingBookServiceProxy` implementuje `IBookService` z `@Primary`, dodaje cache dla 3 metod, `BookService` staje się czysty.

| Plik | Zmiana |
|------|--------|
| `catalog-service/.../proxy/CachingBookServiceProxy.java` | **NOWY** — caching proxy z `@Primary` |
| `catalog-service/.../service/BookService.java` | Usunięto logikę `CatalogCacheManager` |

### 2. FeedbackServiceProtectionProxy — Protection Proxy (`feedback-service`)

**Problem:** `FeedbackService.submitFeedback()` mieszał logikę ochrony (rate limiting, IP blacklist) z logiką biznesową tworzenia feedbacku.

**Rozwiązanie:** Nowa klasa `FeedbackServiceProtectionProxy` przechwytuje `submitFeedback()` i sprawdza rate limit + IP blacklist przed delegowaniem.

| Plik | Zmiana |
|------|--------|
| `feedback-service/.../proxy/FeedbackServiceProtectionProxy.java` | **NOWY** — protection proxy z `@Primary` |
| `feedback-service/.../service/FeedbackService.java` | Usunięto rate limit + IP blacklist z `submitFeedback()` |

### 3. CachingBranchServiceProxy — Caching Proxy (`user-service`)

**Problem:** `BranchServiceClient.getBranchById()` wykonywał kosztowne wywołanie HTTP za każdym razem, nawet dla tych samych oddziałów.

**Rozwiązanie:** Nowy interfejs `IBranchServiceClient` + klasa `CachingBranchServiceProxy` cachuje wyniki HTTP w `ConcurrentHashMap`.

| Plik | Zmiana |
|------|--------|
| `user-service/.../client/IBranchServiceClient.java` | **NOWY** — interfejs dla wzorca Proxy |
| `user-service/.../proxy/CachingBranchServiceProxy.java` | **NOWY** — caching proxy z `ConcurrentHashMap` |
| `user-service/.../client/BranchServiceClient.java` | Implementacja `IBranchServiceClient` |
| `user-service/.../service/UserService.java` | Zmiana typu pola na `IBranchServiceClient` |

---

## Podsumowanie zmian

| Kategoria | Liczba |
|-----------|--------|
| Nowe pliki | **9** |
| Zmodyfikowane pliki | **7** |
| Wzorce | Facade (3), Proxy (3) |
| Mikroserwisy | catalog-service, auth-service, rental-service, feedback-service, user-service |

## Weryfikacja

Kompilacja Maven wszystkich zmodyfikowanych modułów przeszła pomyślnie (`exit code 0`).
