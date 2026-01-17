package org.unina.data;

import org.jsoup.nodes.Document;

import java.sql.*;
import java.util.*;

public class MutationDatabase {
    private static final String DB_URL = "jdbc:sqlite:mutations.db";

    public MutationDatabase() {
        String masterTableSQL = "CREATE TABLE IF NOT EXISTS mutations (" +
                "uuid TEXT PRIMARY KEY, " +
                "element TEXT, " +
                "mutation_name TEXT, " +
                "mutation_type TEXT, " +
                "mutation_id TEXT)";

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

    public void resetMutations() {
        String sql = "UPDATE mutants SET status = 'PENDING'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Set<Mutation> getPendingMutations() {
        Set<Mutation> mutations = new HashSet<>();

        String sql = "SELECT * FROM mutations"; //LIMIT 1

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                mutations.add(new Mutation(
                    rs.getString("uuid"),
                    rs.getString("element"),
                    rs.getString("mutation_name"),
                    rs.getString("mutation_type"),
                    rs.getString("mutation_id")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il recupero dati: " + e.getMessage());
        }

        return mutations;
    }

    public List<MutatedFile> getMutatedFiles(String uuid){
        List<MutatedFile> mutatedFiles = new ArrayList<>();

        String sql = "SELECT target_file_path, mutated_code FROM mutated_files WHERE mutation_uuid = ?";

        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, uuid);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MutatedFile mut = new MutatedFile();
                mut.filePath = rs.getString("target_file_path");
                mut.mutatedCode = rs.getString("mutated_code");
                mutatedFiles.add(mut);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return mutatedFiles;
    }

    public void saveMutation(String element, String mutation_name, String mutation_type, String mutation_id, List<Document> mutatedDocuments) {
        String mutantUUID = java.util.UUID.randomUUID().toString();

        String insertMutant = "INSERT INTO mutations(uuid, element, mutation_name, mutation_type, mutation_id) VALUES(?,?,?,?,?)";
        String insertFile   = "INSERT INTO mutated_files(uuid, target_file_path, mutated_code, mutation_uuid) VALUES(?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtMutant = conn.prepareStatement(insertMutant)){
                pstmtMutant.setString(1, mutantUUID);
                pstmtMutant.setString(2, element);
                pstmtMutant.setString(3, mutation_name);
                pstmtMutant.setString(4, mutation_type);
                pstmtMutant.setString(5, mutation_id);
                pstmtMutant.executeUpdate();

                for (Document doc : mutatedDocuments) {
                    try (PreparedStatement pstmtFile = conn.prepareStatement(insertFile)) {
                        String filePath = doc.baseUri();
                        String code = doc.body().html();

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

}
