package com.utime.memoBom.common.schemasync;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateTableParser {

	private static final Pattern TABLE_NAME = Pattern
			.compile("CREATE\\s+TABLE\\s+IF\\s+NOT\\s+EXISTS\\s+([A-Za-z0-9_]+)", Pattern.CASE_INSENSITIVE);

	public static Optional<String> tryExtractTableName(String ddl) {
		Matcher m = TABLE_NAME.matcher(ddl);
		if (!m.find())
			return Optional.empty();
		return Optional.of(m.group(1).toUpperCase(Locale.ROOT));
	}

	public static TableSpec parse(String ddl) {
		String table = tryExtractTableName(ddl).orElseThrow(() -> new IllegalArgumentException("No table name in ddl"));

		int open = ddl.indexOf('(');
		int close = ddl.lastIndexOf(')');
		if (open < 0 || close < 0 || close <= open) {
			throw new IllegalArgumentException("Invalid CREATE TABLE ddl (no parentheses): " + ddl);
		}

		String inside = ddl.substring(open + 1, close).trim();
		List<String> parts = splitTopLevelByComma(inside);

		Map<String, ColumnSpec> columns = new LinkedHashMap<>();
		for (String p : parts) {
			String seg = p.trim();
			if (seg.isEmpty())
				continue;

			// constraint 라인 제외
			if (seg.toUpperCase(Locale.ROOT).startsWith("CONSTRAINT ")
					|| seg.toUpperCase(Locale.ROOT).startsWith("PRIMARY KEY")
					|| seg.toUpperCase(Locale.ROOT).startsWith("UNIQUE")
					|| seg.toUpperCase(Locale.ROOT).startsWith("FOREIGN KEY")) {
				continue;
			}

			ColumnSpec c = ColumnSpec.parse(seg);
			columns.put(c.name(), c);
		}

		return new TableSpec(table, columns);
	}

	private static List<String> splitTopLevelByComma(String s) {
		List<String> out = new ArrayList<>();
		StringBuilder cur = new StringBuilder();
		int depth = 0;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch == '(')
				depth++;
			if (ch == ')')
				depth--;

			if (ch == ',' && depth == 0) {
				out.add(cur.toString());
				cur.setLength(0);
			} else {
				cur.append(ch);
			}
		}
		if (!cur.isEmpty())
			out.add(cur.toString());
		return out;
	}

	public record TableSpec(String tableName, Map<String, ColumnSpec> columns) {
	}
}
