package com.upball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upball.entity.MatchAlbum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MatchAlbumMapper extends BaseMapper<MatchAlbum> {

    @Select("SELECT * FROM match_albums WHERE match_id = #{matchId} AND deleted = 0 ORDER BY is_cover DESC, created_at DESC")
    List<MatchAlbum> selectByMatchId(Long matchId);

    @Select("SELECT * FROM match_albums WHERE match_id = #{matchId} AND deleted = 0 ORDER BY is_cover DESC, created_at DESC LIMIT #{limit}")
    List<MatchAlbum> selectByMatchIdLimit(@Param("matchId") Long matchId, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM match_albums WHERE match_id = #{matchId} AND deleted = 0")
    int countByMatchId(Long matchId);

    @Update("UPDATE match_albums SET is_cover = 0 WHERE match_id = #{matchId} AND is_cover = 1")
    int clearCover(Long matchId);

    @Update("UPDATE match_albums SET is_cover = 1 WHERE id = #{id}")
    int setAsCover(Long id);

    @Update("UPDATE match_albums SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(Long id);

    @Update("UPDATE match_albums SET like_count = like_count - 1 WHERE id = #{id}")
    int decrementLikeCount(Long id);
}
