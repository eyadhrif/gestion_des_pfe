package main;

import backend.dao.*;
import model.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("===== TEST GESTION PFE =====\n");

        try {
            EtudiantDAO etudiantDAO = new EtudiantDAOImpl();
            EncadreurDAO encadreurDAO = new EncadreurDAOImpl();
            PfeDAO pfeDAO = new PfeDAOImpl();
            SoutenanceDAO soutenanceDAO = new SoutenanceDAOImpl();

            // ===============================
            // 1. Test Étudiant
            // ===============================
            System.out.println("1️⃣  Testing Étudiant...");
            Etudiant etu = new Etudiant(
                    0,
                    "Ben Ali",
                    "Ahmed",
                    "ahmed@email.com",
                    "3A Info"
            );
            etudiantDAO.insert(etu);
            Etudiant etuDB = etudiantDAO.getAll().get(0);
            System.out.println("   ✔ Étudiant créé: " + etuDB.getPrenom() + " " + etuDB.getNom() + " (ID: " + etuDB.getIdEtudiant() + ")\n");

            // ===============================
            // 2. Test Encadreur
            // ===============================
            System.out.println("2️⃣  Testing Encadreur...");
            Encadreur enc = new Encadreur(
                    0,
                    "Dupont",
                    "Marie",
                    "Professeur",
                    "marie.dupont@university.tn"
            );
            encadreurDAO.insert(enc);
            Encadreur encDB = encadreurDAO.getAll().get(0);
            System.out.println("   ✔ Encadreur créé: " + encDB.getGrade() + " " + encDB.getPrenom() + " " + encDB.getNom() + " (ID: " + encDB.getIdEncadreur() + ")\n");

            // ===============================
            // 3. Test PFE
            // ===============================
            System.out.println("3️⃣  Testing PFE...");
            Pfe pfe = new Pfe(
                    0,
                    "IA et Détection de fraude",
                    "Machine Learning",
                    "En cours",
                    java.time.LocalDate.parse("2026-06-20"),
                    etuDB,
                    null
            );
            pfeDAO.insert(pfe);
            Pfe pfeDB = pfeDAO.getAll().get(0);
            System.out.println("   ✔ PFE créé: \"" + pfeDB.getTitre() + "\" (ID: " + pfeDB.getIdpfe() + ")");
            System.out.println("   ℹ État: " + pfeDB.getEtat() + " | Date soutenance: " + pfeDB.getDateSoutenance() + "\n");

            // ===============================
            // 4. Test Soutenance
            // ===============================
            System.out.println("4️⃣  Testing Soutenance...");
            Soutenance s = new Soutenance(
                    0,
                    java.time.LocalDate.now(),
                    "Salle A1",
                    15.5,
                    pfeDB
            );
            soutenanceDAO.insert(s);
            Soutenance sDB = soutenanceDAO.getAll().get(0);
            System.out.println("   ✔ Soutenance créée: " + sDB.getSalle() + " (ID: " + sDB.getIdsoutenance() + ")");
            System.out.println("   ℹ Date: " + sDB.getDate() + " | Note: " + sDB.getNote() + "/20\n");

            System.out.println("═════════════════════════════════════");
            System.out.println("✅ TOUS LES TESTS RÉUSSIS!");
            System.out.println("═════════════════════════════════════");

        } catch (Exception e) {
            System.err.println("\n❌ ERREUR DÉTECTÉE:");
            e.printStackTrace();
        }
    }
}
