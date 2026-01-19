package com.utime.memoBom.board.dao;

import com.utime.memoBom.board.vo.BoardReqVo;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.vo.UserVo;

public interface BoardDao {
	
	/**
	 * Fragment(편린) 저장
	 * @param reqVo
	 * @return
	 */
	int saveFragment(UserVo user, UserDevice device, BoardReqVo reqVo) throws Exception;

}
