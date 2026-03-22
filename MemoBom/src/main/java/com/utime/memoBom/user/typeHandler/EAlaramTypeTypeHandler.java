package com.utime.memoBom.user.typeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.utime.memoBom.user.vo.EAlaramType;

public class EAlaramTypeTypeHandler extends BaseTypeHandler<EAlaramType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EAlaramType parameter, JdbcType jdbcType) throws SQLException {
    	ps.setString(i, parameter.name() );
    }

    @Override
    public EAlaramType getNullableResult(ResultSet rs, String columnName) throws SQLException {
    	return EAlaramType.fromCode( rs.getString(columnName) );
    }

    @Override
    public EAlaramType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return EAlaramType.fromCode( rs.getString(columnIndex) );
    }

    @Override
    public EAlaramType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    	return EAlaramType.fromCode( cs.getString(columnIndex) );
    }
}