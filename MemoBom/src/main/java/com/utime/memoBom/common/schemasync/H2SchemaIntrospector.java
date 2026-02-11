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
				    SELECT COLUMN_NAME, DATA_TYPE,
				           CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE,
				           IS_NULLABLE, COLUMN_DEFAULT
				    FROM INFORMATION_SCHEMA.COLUMNS
				    WHERE TABLE_NAME = ?
				""";

		Map<String, ExistingColumn> map = new HashMap<>();
		try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

			ps.setString(1, tableNameUpper);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					String name = rs.getString("COLUMN_NAME").toUpperCase(Locale.ROOT);
					String dataType = rs.getString("DATA_TYPE"); // <-- 핵심 (CHARACTER VARYING 등)

					Long charLen = getNullableLong(rs, "CHARACTER_MAXIMUM_LENGTH");
					Long prec = getNullableLong(rs, "NUMERIC_PRECISION");
					Integer scale = getNullableInt(rs, "NUMERIC_SCALE");

					boolean nullable = "YES".equalsIgnoreCase(rs.getString("IS_NULLABLE"));
					String def = rs.getString("COLUMN_DEFAULT");

					String canonicalType = toCanonicalDdlType(dataType, charLen, prec, scale);

					map.put(name,
							new ExistingColumn(name, dataType, canonicalType, charLen, prec, scale, nullable, def));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to read INFORMATION_SCHEMA for table=" + tableNameUpper, e);
		}
		return map;
	}

	/** INFORMATION_SCHEMA의 DATA_TYPE + 길이/정밀도를 "DDL 비교용 표준 타입"으로 만든다 */
	private String toCanonicalDdlType(String dataType, Long charLen, Long prec, Integer scale) {
		if (dataType == null)
			return null;
		String dt = dataType.toUpperCase(Locale.ROOT).trim();

		// 문자열 계열
		if (dt.equals("CHARACTER VARYING")) {
			return (charLen != null) ? "VARCHAR(" + charLen + ")" : "VARCHAR";
		}
		if (dt.equals("CHARACTER")) {
			return (charLen != null) ? "CHAR(" + charLen + ")" : "CHAR";
		}

		// LOB
		if (dt.equals("CHARACTER LARGE OBJECT"))
			return "CLOB";
		if (dt.equals("BINARY LARGE OBJECT"))
			return "BLOB";

		// 숫자(필요하면 확장)
		if (dt.equals("BIGINT"))
			return "BIGINT";
		if (dt.equals("INTEGER"))
			return "INT";
		if (dt.equals("SMALLINT"))
			return "SMALLINT";

		// 기타
		if (dt.equals("BOOLEAN"))
			return "BOOLEAN";
		if (dt.equals("TIMESTAMP"))
			return "TIMESTAMP";
		if (dt.equals("UUID"))
			return "UUID";

		// 기본 fallback: 그대로
		return dt;
	}

	private Long getNullableLong(ResultSet rs, String col) throws SQLException {
		long v = rs.getLong(col);
		return rs.wasNull() ? null : v;
	}

	private Integer getNullableInt(ResultSet rs, String col) throws SQLException {
		int v = rs.getInt(col);
		return rs.wasNull() ? null : v;
	}

	public record ExistingColumn(String name, String dataType, // 원본 (CHARACTER VARYING 등)
			String canonicalType, // 비교용 표준 (VARCHAR(32), CLOB 등)
			Long charLen, Long precision, Integer scale, boolean nullable, String defaultExpr) {
	}
}
