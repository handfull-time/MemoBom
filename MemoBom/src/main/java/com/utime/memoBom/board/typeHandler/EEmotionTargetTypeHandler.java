package com.utime.memoBom.board.typeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import com.utime.memoBom.board.vo.EEmotionTargetType;

@MappedTypes(EEmotionTargetType.class)
public class EEmotionTargetTypeHandler extends BaseTypeHandler<EEmotionTargetType> {
	
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
    		EEmotionTargetType parameter, JdbcType jdbcType)
            throws SQLException {
    	
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public EEmotionTargetType getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return EEmotionTargetType.of(rs.getInt(columnName));
    }

	@Override
	public EEmotionTargetType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		
		return EEmotionTargetType.of(rs.getInt(columnIndex));
	}

	@Override
	public EEmotionTargetType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		
		return EEmotionTargetType.of(cs.getInt(columnIndex));
	}
}
