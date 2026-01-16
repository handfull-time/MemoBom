package com.utime.memoBom.common.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 최초 필수 테이블 관련 Mapper
 */
@Mapper
public interface CommonMapper {
	
	/**
	 * 테이블 존재 확인
	 * @return
	 */
	public boolean existTable(@Param("tableName") String name);
	
	/**
	 * 인덱스 생성
	 * @param indexName
	 * @param tableName
	 * @param columns
	 * @return
	 */
	public int createIndex(@Param("indexName") String indexName, @Param("tableName") String tableName, @Param("columns") String columns);

	/**
	 * 유니크 인덱스 생성
	 * @param indexName
	 * @param tableName
	 * @param columns
	 * @return
	 */
	public int createUniqueIndex(@Param("indexName") String indexName, @Param("tableName") String tableName, @Param("columns") String columns);

	/**
	 * 테이블 삭제
	 * @param string
	 * @return
	 */
	public int dropTable(@Param("tableName") String tableName);

	/**
	 * 컬럼 정보 조회
	 * @param tableName
	 * @return
	 */
	public List<Map<String, String>> getColumnInfo(@Param("tableName") String tableName);
	
	/**
	 * 컬럼 제거
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	public int dropColumn(@Param("tableName") String tableName, @Param("columnName") String columnName);

	/**
	 * 컬럼 추가
	 * @param tableName
	 * @param columnName
	 * @param columnType
	 * @param columnDefault
	 * @param isNullable
	 * @return
	 */
	public int addColumn(@Param("tableName") String tableName,
                   @Param("columnName") String columnName,
                   @Param("columnType") String columnType,
                   @Param("columnDefault") String columnDefault,
                   @Param("isNullable") Boolean isNullable);

    /**
     * 오버로딩 - 기본값 및 null 허용 여부 없이 컬럼 추가
     * @param tableName
     * @param columnName
     * @param columnType
     * @return
     */
    default public int addColumn(@Param("tableName") String tableName,
                           @Param("columnName") String columnName,
                           @Param("columnType") String columnType) {
        return addColumn(tableName, columnName, columnType, null, null);
    }

}