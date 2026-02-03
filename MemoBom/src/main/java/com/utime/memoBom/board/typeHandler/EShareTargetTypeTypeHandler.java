package com.utime.memoBom.board.typeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import com.utime.memoBom.board.vo.EShareTargetType;

@MappedTypes(EShareTargetType.class)
public class EShareTargetTypeTypeHandler extends BaseTypeHandler<EShareTargetType> {
	
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
    		EShareTargetType parameter, JdbcType jdbcType)
            throws SQLException {
    	
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public EShareTargetType getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return EShareTargetType.of(rs.getInt(columnName));
    }

	@Override
	public EShareTargetType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		
		return EShareTargetType.of(rs.getInt(columnIndex));
	}

	@Override
	public EShareTargetType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		
		return EShareTargetType.of(cs.getInt(columnIndex));
	}
}
