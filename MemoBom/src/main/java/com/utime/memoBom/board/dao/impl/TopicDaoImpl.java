package com.utime.memoBom.board.dao.impl;

import org.springframework.stereotype.Repository;

import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.mapper.TopicMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class TopicDaoImpl implements TopicDao{

	final TopicMapper topicMapper;
}
