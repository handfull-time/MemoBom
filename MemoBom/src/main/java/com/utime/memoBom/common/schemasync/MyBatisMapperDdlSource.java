package com.utime.memoBom.common.schemasync;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;

public class MyBatisMapperDdlSource {

	public Map<String, String> loadCreateTableDdls(String mapperLocations) {
		
		try {
			Resource[] resources = new PathMatchingResourcePatternResolver().getResources(mapperLocations);
			Map<String, String> tableToDdl = new LinkedHashMap<>();

			for (Resource r : resources) {
				if (!r.exists())
					continue;

				try (InputStream is = r.getInputStream()) {
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
					NodeList inserts = doc.getElementsByTagName("insert");

					for (int i = 0; i < inserts.getLength(); i++) {
						Element el = (Element) inserts.item(i);
						String sql = el.getTextContent();
						if (sql == null)
							continue;

						String normalized = normalizeSql(sql);
						if (normalized.toUpperCase(Locale.ROOT).contains("CREATE TABLE")) {
							Optional<String> table = CreateTableParser.tryExtractTableName(normalized);
							table.ifPresent(tn -> tableToDdl.put(tn, normalized));
						}
					}
				}
			}
			return tableToDdl;
		} catch (Exception e) {
			throw new RuntimeException("Failed to load mapper XML ddl from: " + mapperLocations, e);
		}
	}

	private String normalizeSql(String s) {
		// MyBatis XML 주석/공백 정리: <!-- --> 는 XML 파서가 제거해주긴 하지만 혹시 몰라 최소 정리
		String x = s.replace("\r", " ").replace("\n", " ").replace("\t", " ");
		while (x.contains("  "))
			x = x.replace("  ", " ");
		return x.trim();
	}
}
