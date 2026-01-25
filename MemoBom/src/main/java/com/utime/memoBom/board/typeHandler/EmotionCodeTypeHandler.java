package com.utime.memoBom.board.typeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import com.utime.memoBom.board.vo.EEmotionCode;

@MappedTypes(EEmotionCode.class)
public class EmotionCodeTypeHandler extends BaseTypeHandler<EEmotionCode> {
	
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
    		EEmotionCode parameter, JdbcType jdbcType)
            throws SQLException {
    	
        ps.setString(i, parameter.getCode());
    }

    @Override
    public EEmotionCode getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return EEmotionCode.fromCode(rs.getString(columnName));
    }

	@Override
	public EEmotionCode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		
		return EEmotionCode.fromCode(rs.getString(columnIndex));
	}

	@Override
	public EEmotionCode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		
		return EEmotionCode.fromCode(cs.getString(columnIndex));
	}
}
