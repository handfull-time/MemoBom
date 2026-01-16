package com.utime.memoBom.common.typeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import com.utime.memoBom.common.util.SeedCipherUtil;

/**
 * Description : SEED 암호화 데이터 핸들러 
 */
public class SeedEncryptTypeHandler implements TypeHandler<String>{

	@Override
	public void setParameter(PreparedStatement paramPreparedStatement, int paramInt, String paramT,
			JdbcType paramJdbcType) throws SQLException {
		
		final String inputStr;
		if( paramT == null || paramT.length() < 1 ){
			inputStr = ""; 
		}else{
			inputStr = SeedCipherUtil.encrypt( paramT );
		}
		
		paramPreparedStatement.setString(paramInt, inputStr);
	}

	/**
	 * 암호화 된 것을 복호화 한다.
	 * @param src
	 * @return
	 */
	private String decrypt( String src ){
		final String result;
		if( src == null || src.length() < 1 ){
			result = "";
		}else{
			result = SeedCipherUtil.decrypt( src );
		}
		return result;
	}

	@Override
	public String getResult(ResultSet paramResultSet, String paramString) throws SQLException {
		return this.decrypt( paramResultSet.getString(paramString) ); 
	}

	@Override
	public String getResult(ResultSet paramResultSet, int paramInt) throws SQLException {
		return this.decrypt( paramResultSet.getString(paramInt) ); 
	}

	@Override
	public String getResult(CallableStatement paramCallableStatement, int paramInt) throws SQLException {
		return this.decrypt( paramCallableStatement.getString(paramInt) ); 
	}
}