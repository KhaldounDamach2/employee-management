# Employee Management System

## Schnellstart

### Voraussetzungen
- Docker
- Docker Compose

### Anwendung ausführen
1. Repository klonen
2. Umgebungsdatei kopieren: `cp .env.example .env`
3. Ausführen: `docker-compose up --build`
4. Anwendung aufrufen: http://localhost:8081


## Architektur-Highlights

### 🗄️ Datenbank-zentrierte Architektur
Diese Anwendung verwendet **Oracle PL/SQL-Pakete** für alle Datenbankoperationen:
- **Gespeicherte Prozeduren & Funktionen** - Geschäftslogik in der Datenbank
- **Optimierte Abfragen** - Komplexe Operationen durch PL/SQL
- **CRUD-Operationen** - Alle Create, Read, Update, Delete-Operationen via Pakete
- **Datenintegrität** - Validierung und Constraints auf Datenbankebene

### 📊 PL/SQL-Pakete beinhalten:
- **Mitarbeiterverwaltung** - CRUD-Operationen für Mitarbeiter
- **Datenvalidierung** - Durchsetzung von Geschäftsregeln

### CI/CD Pipeline
Dieses Projekt verwendet **GitHub Actions** für Continuous Integration:
- ✅ **Automatisierte Builds** bei jedem Push
- ✅ **Docker Image Building** und Testing
- ✅ **Integrationstests** mit Oracle-Datenbank
- ✅ **Status-Badges** zeigen Build-Status an

### Technologien
- Java Spring Boot
- Oracle Database + PL/SQL
- Docker & Docker Compose
- Maven
- Thymeleaf
- GitHub Actions (CI/CD)

### Architektur
- Geschäftslogik in Oracle PL/SQL-Paketen
- Spring Boot orchestriert Datenbankaufrufe
- Vollständig containerisiert mit Docker