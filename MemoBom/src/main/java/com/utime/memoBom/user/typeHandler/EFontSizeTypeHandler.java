package com.utime.memoBom.user.typeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.utime.memoBom.user.vo.EFontSize;

public class EFontSizeTypeHandler extends BaseTypeHandler<EFontSize> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EFontSize parameter, JdbcType jdbcType) throws SQLException {
    	ps.setString(i, parameter.name() );
    }

    @Override
    public EFontSize getNullableResult(ResultSet rs, String columnName) throws SQLException {
    	return EFontSize.of( rs.getString(columnName) );
    }

    @Override
    public EFontSize getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return EFontSize.of( rs.getString(columnIndex) );
    }

    @Override
    public EFontSize getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    	return EFontSize.of( cs.getString(columnIndex) );
    }
}