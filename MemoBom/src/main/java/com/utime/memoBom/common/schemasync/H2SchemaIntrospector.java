package com.utime.memoBom.common.schemasync;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

public class H2SchemaIntrospector {

    private final DataSource dataSource;

    public H2SchemaIntrospector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, ExistingColumn> loadColumns(String tableNameUpper) {
        String sql = """
            SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE,
                   IS_NULLABLE, COLUMN_DEFAULT
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_NAME = ?
        """;

        Map<String, ExistingColumn> map = new HashMap<>();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tableNameUpper);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("COLUMN_NAME").toUpperCase(Locale.ROOT);
                    String dataType = rs.getString("DATA_TYPE"); // e.g. VARCHAR, BIGINT
                    Long charLen = getNullableLong(rs, "CHARACTER_MAXIMUM_LENGTH");
                    Long prec = getNullableLong(rs, "NUMERIC_PRECISION");
                    Integer scale = getNullableInt(rs, "NUMERIC_SCALE");
                    boolean nullable = "YES".equalsIgnoreCase(rs.getString("IS_NULLABLE"));
                    String def = rs.getString("COLUMN_DEFAULT");

                    map.put(name, new ExistingColumn(name, dataType, charLen, prec, scale, nullable, def));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read INFORMATION_SCHEMA for table=" + tableNameUpper, e);
        }
        return map;
    }

    private Long getNullableLong(ResultSet rs, String col) throws SQLException {
        long v = rs.getLong(col);
        return rs.wasNull() ? null : v;
    }

    private Integer getNullableInt(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }

    public record ExistingColumn(
            String name,
            String typeName,
            Long charLen,
            Long precision,
            Integer scale,
            boolean nullable,
            String defaultExpr
    ) {}
}
