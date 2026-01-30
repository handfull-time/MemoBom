package com.utime.memoBom.user.dto;

import lombok.Data;

/**
 * 사용자 이용 통계
 */
@Data
public class UsageStatisticsDto {
	/** Topic 생성 수 */
	int topicCreatedCount;
	/** Topic follow 수 */
	int topicFollowCount;
	/** 로그인 한 수 */
	int loginCount;
	/** Fragment 작성 수 */
	int fragmentWriteCount;
	/** 댓글 작성 수 */
	int commentWriteCount;
	/** Fragment emotion 참여 수 */
	int fragmentEmotionCount;
	/** 댓글 emotion 참여 수 */
	int commentEmotionCount;
}
