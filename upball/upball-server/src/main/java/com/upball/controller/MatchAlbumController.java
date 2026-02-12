package com.upball.controller;

import com.upball.dto.AlbumUploadDTO;
import com.upball.service.MatchAlbumService;
import com.upball.vo.MatchAlbumVO;
import com.upball.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/match-albums")
public class MatchAlbumController {

    @Autowired
    private MatchAlbumService albumService;

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    /**
     * 上传照片
     */
    @PostMapping("/upload")
    public Result<MatchAlbumVO> uploadPhoto(
            @Valid @RequestBody AlbumUploadDTO dto,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        MatchAlbumVO vo = albumService.uploadPhoto(userId, dto);
        return Result.success(vo);
    }

    /**
     * 批量上传
     */
    @PostMapping("/batch-upload")
    public Result<List<MatchAlbumVO>> batchUpload(
            @RequestBody List<AlbumUploadDTO> dtoList,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        List<MatchAlbumVO> list = albumService.batchUpload(userId, dtoList);
        return Result.success(list);
    }

    /**
     * 获取赛事相册
     */
    @GetMapping("/match/{matchId}")
    public Result<List<MatchAlbumVO>> getMatchAlbums(
            @PathVariable Long matchId,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        List<MatchAlbumVO> list = albumService.getMatchAlbums(matchId, userId);
        return Result.success(list);
    }

    /**
     * 获取相册预览
     */
    @GetMapping("/match/{matchId}/preview")
    public Result<List<MatchAlbumVO>> getAlbumPreview(
            @PathVariable Long matchId,
            @RequestParam(defaultValue = "4") int limit) {
        List<MatchAlbumVO> list = albumService.getAlbumPreview(matchId, limit);
        return Result.success(list);
    }

    /**
     * 获取照片数量
     */
    @GetMapping("/match/{matchId}/count")
    public Result<Integer> getPhotoCount(@PathVariable Long matchId) {
        int count = albumService.getPhotoCount(matchId);
        return Result.success(count);
    }

    /**
     * 删除照片
     */
    @DeleteMapping("/{albumId}")
    public Result<Void> deletePhoto(
            @PathVariable Long albumId,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        albumService.deletePhoto(albumId, userId);
        return Result.success();
    }

    /**
     * 设为封面
     */
    @PostMapping("/{albumId}/cover")
    public Result<Void> setAsCover(
            @PathVariable Long albumId,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        albumService.setAsCover(albumId, userId);
        return Result.success();
    }

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/{albumId}/like")
    public Result<Void> likePhoto(
            @PathVariable Long albumId,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        albumService.likePhoto(albumId, userId);
        return Result.success();
    }

    /**
     * 更新描述
     */
    @PutMapping("/{albumId}/description")
    public Result<Void> updateDescription(
            @PathVariable Long albumId,
            @RequestParam String description,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        albumService.updateDescription(albumId, userId, description);
        return Result.success();
    }
}
