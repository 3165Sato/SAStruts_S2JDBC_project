package org.seasar.sastruts.example.testsupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.seasar.extension.jdbc.JdbcManager;

public class SqlTestSupport {

    private final JdbcManager jdbcManager;

    public SqlTestSupport(JdbcManager jdbcManager) {
        this.jdbcManager = jdbcManager;
    }

    public void executeSqlFile(String path) {
        String sqlText = readSqlFile(path);
        String[] statements = sqlText.split(";");
        for (int i = 0; i < statements.length; i++) {
            String statement = statements[i].trim();
            if (statement.length() > 0) {
                jdbcManager.updateBySql(statement).execute();
            }
        }
    }

    private String readSqlFile(String path) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new IllegalArgumentException("SQL file does not exist: " + path);
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.length() > 0 && !trimmed.startsWith("--")) {
                    builder.append(line).append('\n');
                }
            }
            return builder.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read SQL file: " + path, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to close SQL file: " + path, e);
                }
            }
        }
    }
}
