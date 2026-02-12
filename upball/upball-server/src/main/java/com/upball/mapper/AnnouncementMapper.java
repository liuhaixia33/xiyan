package com.upball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upball.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    @Select("SELECT * FROM announcements WHERE team_id = #{teamId} AND deleted = 0 " +
            "ORDER BY is_top DESC, created_at DESC")
    List<Announcement> selectByTeamId(Long teamId);

    @Select("SELECT * FROM announcements WHERE team_id = #{teamId} AND deleted = 0 " +
            "ORDER BY is_top DESC, created_at DESC LIMIT #{limit}")
    List<Announcement> selectLatestByTeamId(@Param("teamId") Long teamId, @Param("limit") int limit);

    @Update("UPDATE announcements SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);

    @Update("UPDATE announcements SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(Long id);

    @Update("UPDATE announcements SET comment_count = comment_count + 1 WHERE id = #{id}")
    int incrementCommentCount(Long id);
}
