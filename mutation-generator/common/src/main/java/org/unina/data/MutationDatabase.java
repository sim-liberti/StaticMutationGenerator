package org.unina.data;

import org.jsoup.nodes.Document;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class MutationDatabase {
    private static final String DB_URL = "jdbc:sqlite:mutations.db";

    public MutationDatabase() {
        String masterTableSQL = "CREATE TABLE IF NOT EXISTS mutations (" +
                "uuid TEXT PRIMARY KEY, " +
                "element TEXT, " +
                "mutation_type TEXT, " +
                "mutation_id TEXT, " +
                "status TEXT DEFAULT 'PENDING', " + // PENDING, RUNNING, KILLED, SURVIVED, ERROR
                "error_log TEXT)";

        String filesTableSQL = "CREATE TABLE IF NOT EXISTS mutated_files (" +
                "uuid TEXT PRIMARY KEY, " +
                "target_file_path TEXT NOT NULL, " +
                "mutated_code TEXT NOT NULL, " +
                "mutation_uuid TEXT NOT NULL, " +
                "FOREIGN KEY(mutation_uuid) REFERENCES mutants(uuid) ON DELETE CASCADE)";

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (connection == null) {
                throw new RuntimeException("Failed to connect to the database.");
            }
            connection.setAutoCommit(false);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
                stmt.execute(masterTableSQL);
                stmt.execute(filesTableSQL);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveMutation(String element, String mutation_type, String mutation_id, List<Document> mutatedDocuments) {
        String mutantUUID = java.util.UUID.randomUUID().toString();

        String insertMutant = "INSERT INTO mutations(uuid, element, mutation_type, mutation_id) VALUES(?,?,?,?)";
        String insertFile   = "INSERT INTO mutated_files(uuid, target_file_path, mutated_code, mutation_uuid) VALUES(?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtMutant = conn.prepareStatement(insertMutant)){
                pstmtMutant.setString(1, mutantUUID);
                pstmtMutant.setString(2, element);
                pstmtMutant.setString(3, mutation_type);
                pstmtMutant.setString(4, mutation_id);
                pstmtMutant.executeUpdate();

                for (Document doc : mutatedDocuments) {
                    try (PreparedStatement pstmtFile = conn.prepareStatement(insertFile)) {
                        String filePath = doc.baseUri();
                        String code = doc.html();

                        pstmtFile.setString(1, java.util.UUID.randomUUID().toString());
                        pstmtFile.setString(2, filePath);
                        pstmtFile.setString(3, code);
                        pstmtFile.setString(4, mutantUUID);
                        pstmtFile.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public record MutantTask(int id, String filePath, String code) {}

    public synchronized Optional<MutantTask> getNextTask() {
        String selectSql = "SELECT id, target_file_path, mutated_code FROM mutants WHERE status = 'PENDING' LIMIT 1";
        String updateSql = "UPDATE mutants SET status = 'RUNNING' WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false); // Inizia transazione

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql)) {

                if (rs.next()) {
                    int id = rs.getInt("id");
                    String path = rs.getString("target_file_path");
                    String code = rs.getString("mutated_code");

                    // Segnalo subito come in esecuzione
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, id);
                        updateStmt.executeUpdate();
                    }

                    conn.commit(); // Conferma transazione
                    return Optional.of(new MutantTask(id, path, code));
                }
            } catch (SQLException e) {
                conn.rollback(); // Se qualcosa va storto, annulla tutto
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty(); // Nessun mutante rimasto
    }

    public void updateResult(int id, String newStatus, String errorLog) {
        String sql = "UPDATE mutants SET status = ?, error_log = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setString(2, errorLog); // Può essere null
            pstmt.setInt(3, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
