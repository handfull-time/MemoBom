package com.utime.memoBom.board.dto;

import com.utime.memoBom.common.vo.ReturnBasic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true )
public class FragmentListDto extends ReturnBasic{

	String title;
}
