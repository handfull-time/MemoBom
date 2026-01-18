package com.utime.memoBom.board.typeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import com.utime.memoBom.board.vo.EmojiSetType;

@MappedTypes(EmojiSetType.class)
public class EmojiSetTypeHandler extends BaseTypeHandler<EmojiSetType> {
	
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    EmojiSetType parameter, JdbcType jdbcType)
            throws SQLException {
    	
        ps.setInt(i, parameter.code);
    }

    @Override
    public EmojiSetType getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return EmojiSetType.of(rs.getInt(columnName));
    }

	@Override
	public EmojiSetType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		
		return EmojiSetType.of(rs.getInt(columnIndex));
	}

	@Override
	public EmojiSetType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		
		return EmojiSetType.of(cs.getInt(columnIndex));
	}
}
