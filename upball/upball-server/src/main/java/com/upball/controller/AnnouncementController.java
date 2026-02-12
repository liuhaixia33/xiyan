package com.upball.controller;

import com.upball.dto.AnnouncementCreateDTO;
import com.upball.service.AnnouncementService;
import com.upball.vo.AnnouncementVO;
import com.upball.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    /**
     * 发布公告
     */
    @PostMapping
    public Result<AnnouncementVO> createAnnouncement(
            @Valid @RequestBody AnnouncementCreateDTO dto,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        var announcement = announcementService.createAnnouncement(userId, dto);
        return Result.success(announcementService.getAnnouncementDetail(announcement.getId(), userId));
    }

    /**
     * 获取球队公告列表
     */
    @GetMapping("/team/{teamId}")
    public Result<List<AnnouncementVO>> getTeamAnnouncements(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<AnnouncementVO> list = announcementService.getTeamAnnouncements(teamId, page, size);
        return Result.success(list);
    }

    /**
     * 获取最新公告
     */
    @GetMapping("/team/{teamId}/latest")
    public Result<List<AnnouncementVO>> getLatestAnnouncements(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "5") int limit) {
        List<AnnouncementVO> list = announcementService.getLatestAnnouncements(teamId, limit);
        return Result.success(list);
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/{announcementId}")
    public Result<AnnouncementVO> getAnnouncementDetail(
            @PathVariable Long announcementId,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        AnnouncementVO vo = announcementService.getAnnouncementDetail(announcementId, userId);
        return Result.success(vo);
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{announcementId}")
    public Result<Void> deleteAnnouncement(
            @PathVariable Long announcementId,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        announcementService.deleteAnnouncement(announcementId, userId);
        return Result.success();
    }

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/{announcementId}/like")
    public Result<Void> likeAnnouncement(
            @PathVariable Long announcementId,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        announcementService.likeAnnouncement(announcementId, userId);
        return Result.success();
    }

    /**
     * 置顶/取消置顶
     */
    @PostMapping("/{announcementId}/top")
    public Result<Void> toggleTop(
            @PathVariable Long announcementId,
            @RequestParam Integer isTop,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        announcementService.toggleTop(announcementId, userId, isTop);
        return Result.success();
    }
}
