package com.utime.memoBom.board.service.impl;

import org.springframework.stereotype.Service;

import com.utime.memoBom.board.dao.BoardDao;
import com.utime.memoBom.board.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class BoardServiceImpl implements BoardService {

	final BoardDao boardDao;
}
