package com.utime.memoBom.board.dao.impl;

import org.springframework.stereotype.Repository;

import com.utime.memoBom.board.dao.BoardDao;
import com.utime.memoBom.board.mapper.BoardMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class BoardDaoImpl implements BoardDao{

	final BoardMapper boardMapper;
}
