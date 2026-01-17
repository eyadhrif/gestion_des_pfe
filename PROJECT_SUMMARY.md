# PFE Manager - Project Documentation

## Project Overview

**Project Type:** Java Swing Desktop Application  
**Purpose:** Managing Projets de Fin d'Études (PFE) - Final Year Projects for students

---

## Technology Stack

| Component | Technology |
|-----------|------------|
| Language | Java (JDK 8+) |
| UI Framework | Java Swing + FlatLaf |
| Database | MySQL |
| IDE | NetBeans |

### External Libraries
- `flatlaf-3.6.2.jar` - Modern look and feel theme
- `mysql-connector-java-8.0.24.jar` - Database connectivity

---

## Project Structure

```
Pfe_Manager/
├── src/
│   ├── frontend/ui/
│   │   ├── AppLauncher.java       # Main entry point
│   │   ├── main/
│   │   │   ├── MainFrame.java     # Main window
│   │   │   └── Sidebar.java       # Navigation
│   │   └── dashboard/
│   │       └── DashboardPanel.java # Dashboard UI
│   └── backend/
│       ├── dao/                    # Data Access Objects
│       ├── dao/impl/               # DAO implementations
│       └── models/                 # Data models
├── build/classes/                  # Compiled classes
└── dist/                           # Distribution JAR
```

---

## Dashboard Components

### 1. Stats Cards (Row 1)
- **Étudiants** - Total students count (blue accent)
- **PFEs en cours** - Active PFE projects (purple accent)
- **Soutenances à venir** - Upcoming defenses (green accent)
- **Alertes** - Alerts summary (red accent)

**Styling:** Colored left border, black icons, subtle shadows

### 2. Activity Chart (Row 2)
- Bar chart: "Activité des PFEs"
- Dual bars: Projets Débutés (blue) + Projets Finalisés (purple)
- Blue trend line with circle markers
- Y-axis scale: 0, 5, 10, 15, 20
- Time filters: "6 derniers mois", "Cette année", "Personnalisé", "Voir tout"
- Centered bottom legend

### 3. Projets en Retard Table (Row 3)
- Columns: Étudiant, Projet, Jours de Retard
- Orange left border per row
- Colored badges (yellow/orange/red by severity)
- "Voir tout" navigation buttons

### 4. Supervision & Débordement Card (Row 3)
- Centered donut chart (120px)
- Horizontal legend below chart
- Categories: Encadrement OK, Débordé, encadreurs
- Supervisor info section

### 5. Soutenances à venir Table (Row 3)
- Columns: Encadré, Date, État
- Blue status badges

---

## Color Palette

| Color | Hex/RGB | Usage |
|-------|---------|-------|
| Blue | `rgb(99, 102, 241)` | Primary accent |
| Purple | `rgb(139, 92, 246)` | Secondary |
| Green | `rgb(34, 197, 94)` | Success |
| Red | `rgb(239, 68, 68)` | Danger/Alert |
| Text Primary | `rgb(51, 51, 51)` | Main text |
| Text Secondary | `rgb(120, 120, 120)` | Muted text |
| Background | `rgb(245, 247, 250)` | Page background |

---

## Custom Components

| Component | Description |
|-----------|-------------|
| `ModernCard` | White card with rounded corners and shadow |
| `VectorIcon` | Custom painted icons (user, people, calendar) |
| `BarChartPanel` | Bar chart with dual bars and trend line |
| `BigDonutPanel` | Donut/pie chart component |

---

## How to Run

```bash
cd c:\Users\ASUS\Documents\NetBeansProjects\Pfe_Manager
java -cp "dist\Pfe_Manager.jar;dist\lib\*" ui.AppLauncher
```

## How to Compile Changes

```bash
# Compile
javac -d build/classes -cp "path\to\mysql-connector.jar;path\to\flatlaf.jar;build/classes" src/frontend/ui/dashboard/DashboardPanel.java

# Deploy
xcopy /Y build\classes\ui\dashboard\DashboardPanel*.class dist\ui\dashboard\
```

---

## Data Architecture

- **Async Loading:** SwingWorker for non-blocking UI
- **Fallback Data:** Mock data when database is empty
- **DAOs:** EtudiantDAO, PfeDAO, SoutenanceDAO

---

## Key Files

| File | Purpose |
|------|---------|
| `AppLauncher.java` | Application entry point |
| `MainFrame.java` | Main window container |
| `Sidebar.java` | Navigation menu |
| `DashboardPanel.java` | Dashboard with all components |

---

*Generated: January 2026*
