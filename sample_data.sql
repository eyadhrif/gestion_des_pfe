-- ============================================
-- SAMPLE DATA FOR PFE MANAGER DATABASE
-- Database: gestion_pfe
-- Run this in MySQL Workbench or phpMyAdmin
-- ============================================

-- ============================================
-- 1. ETUDIANTS (Students)
-- ============================================
INSERT INTO etudiant (nom, prenom, email, classe) VALUES
('Dupont', 'Marie', 'marie.dupont@email.com', '3A-DS'),
('Martin', 'Paul', 'paul.martin@email.com', '3A-DS'),
('Laurent', 'Sophie', 'sophie.laurent@email.com', '3A-SE'),
('Bernard', 'Lucas', 'lucas.bernard@email.com', '3A-SE'),
('Lefebvre', 'Emma', 'emma.lefebvre@email.com', '3A-IA'),
('Moreau', 'Hugo', 'hugo.moreau@email.com', '3A-DS'),
('Petit', 'Lea', 'lea.petit@email.com', '3A-SE'),
('Dubois', 'Nathan', 'nathan.dubois@email.com', '3A-IA'),
('Leroy', 'Camille', 'camille.leroy@email.com', '3A-DS'),
('Roux', 'Ines', 'ines.roux@email.com', '3A-SE'),
('Thomas', 'Louis', 'louis.thomas@email.com', '3A-DS'),
('Robert', 'Chloe', 'chloe.robert@email.com', '3A-IA'),
('Richard', 'Gabriel', 'gabriel.richard@email.com', '3A-SE'),
('Girard', 'Manon', 'manon.girard@email.com', '3A-DS'),
('Bonnet', 'Arthur', 'arthur.bonnet@email.com', '3A-IA');

-- ============================================
-- 2. ENCADREURS (Supervisors)
-- ============================================
INSERT INTO encadreur (nom, prenom, email, specialite) VALUES
('Lefevre', 'Catherine', 'c.lefevre@univ.com', 'Data Science'),
('Fontaine', 'Michel', 'm.fontaine@univ.com', 'Intelligence Artificielle'),
('Chevalier', 'Anne', 'a.chevalier@univ.com', 'Systèmes Embarqués'),
('Rousseau', 'Pierre', 'p.rousseau@univ.com', 'Réseaux'),
('Vincent', 'Claire', 'c.vincent@univ.com', 'Big Data');

-- ============================================
-- 3. PFE (Projects)
-- ============================================
-- Note: Adjust idetudiant IDs based on your actual etudiant IDs
INSERT INTO pfe (titre, description, etat, date_soutenance, idetudiant) VALUES
('Système IoT Intelligent', 'Développement d''un système IoT pour la maison connectée', 'En cours', NULL, 1),
('Analyse de Big Data', 'Analyse prédictive sur données massives', 'En cours', NULL, 2),
('Réseau de capteurs', 'Mise en place d''un réseau de capteurs sans fil', 'En retard', NULL, 3),
('Application Mobile Santé', 'App de suivi santé avec ML', 'En cours', NULL, 4),
('Chatbot IA', 'Chatbot intelligent pour service client', 'Terminé', '2026-01-10', 5),
('Plateforme E-Learning', 'Système de formation en ligne', 'En cours', NULL, 6),
('Système de Recommandation', 'Moteur de recommandation personnalisé', 'En cours', NULL, 7),
('Reconnaissance Faciale', 'Système de sécurité par reconnaissance faciale', 'En retard', NULL, 8),
('Dashboard Analytics', 'Tableau de bord d''analyse de données', 'Terminé', '2026-01-08', 9),
('Blockchain pour Votes', 'Système de vote électronique sécurisé', 'En cours', NULL, 10),
('Robot Autonome', 'Robot de navigation autonome', 'En cours', NULL, 11),
('NLP Sentiment Analysis', 'Analyse de sentiments en temps réel', 'En cours', NULL, 12),
('Cloud Migration Tool', 'Outil de migration vers le cloud', 'En retard', NULL, 13),
('Cybersécurité AI', 'Détection d''intrusion par IA', 'En cours', NULL, 14),
('Smart Agriculture', 'Agriculture intelligente avec IoT', 'En cours', NULL, 15);

-- ============================================
-- 4. PFE_ENCADREUR (Project-Supervisor Links)
-- ============================================
-- Note: Adjust IDs based on actual pfe and encadreur IDs
INSERT INTO pfe_encadreur (idpfe, idencadreur) VALUES
(1, 3), (2, 5), (3, 4), (4, 2), (5, 2),
(6, 1), (7, 5), (8, 2), (9, 1), (10, 4),
(11, 3), (12, 2), (13, 5), (14, 4), (15, 3);

-- ============================================
-- 5. SOUTENANCES (Defenses)
-- ============================================
-- Upcoming soutenances (future dates)
INSERT INTO soutenance (date, salle, note, idpfe) VALUES
('2026-01-15', 'Salle A101', 0, 1),
('2026-01-16', 'Salle B202', 0, 2),
('2026-01-17', 'Salle A103', 0, 4),
('2026-01-18', 'Salle C301', 0, 6),
('2026-01-20', 'Salle A101', 0, 7),
('2026-01-22', 'Salle B202', 0, 10),
('2026-01-25', 'Salle A101', 0, 11),
('2026-01-28', 'Salle C301', 0, 12);

-- Past soutenances (completed)
INSERT INTO soutenance (date, salle, note, idpfe) VALUES
('2026-01-08', 'Salle A101', 16.5, 9),
('2026-01-10', 'Salle B202', 17.0, 5);

-- ============================================
-- VERIFICATION QUERIES
-- ============================================
-- Run these to verify data was inserted:
-- SELECT COUNT(*) as total_etudiants FROM etudiant;
-- SELECT COUNT(*) as total_pfe FROM pfe;
-- SELECT COUNT(*) as total_soutenances FROM soutenance;
-- SELECT classe, COUNT(*) as count FROM etudiant GROUP BY classe;
-- SELECT etat, COUNT(*) as count FROM pfe GROUP BY etat;
