package doldol_server.doldol.invite.controller;

import doldol_server.doldol.common.response.ApiResponse;
import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.invite.dto.request.InviteCommentCreateRequest;
import doldol_server.doldol.invite.dto.request.InviteCreateRequest;
import doldol_server.doldol.invite.dto.request.InviteUpdateRequest;
import doldol_server.doldol.invite.dto.response.InviteCommentResponse;
import doldol_server.doldol.invite.dto.response.InviteResponse;
import doldol_server.doldol.invite.service.InviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "초대장", description = "파티/모임 초대장 API")
@RestController
@RequestMapping("/invites")
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;

    @Operation(
        summary = "초대장 생성",
        security = {@SecurityRequirement(name = "jwt")}
    )
    @PostMapping
    public ApiResponse<InviteResponse> createInvite(
        @Valid @RequestBody InviteCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.created(inviteService.createInvite(request, userDetails.getUserId()));
    }

    @Operation(summary = "초대장 상세 조회")
    @GetMapping("/{inviteId}")
    public ApiResponse<InviteResponse> getInvite(@PathVariable Long inviteId) {
        return ApiResponse.ok(inviteService.getInvite(inviteId));
    }

    @Operation(summary = "초대장 댓글 등록")
    @PostMapping("/{inviteId}/comments")
    public ApiResponse<InviteCommentResponse> addComment(
        @PathVariable Long inviteId,
        @Valid @RequestBody InviteCommentCreateRequest request
    ) {
        return ApiResponse.created(inviteService.addComment(inviteId, request));
    }

    @Operation(summary = "초대장 댓글 목록 조회")
    @GetMapping("/{inviteId}/comments")
    public ApiResponse<List<InviteCommentResponse>> getComments(@PathVariable Long inviteId) {
        return ApiResponse.ok(inviteService.getComments(inviteId));
    }

    @Operation(
        summary = "초대장 수정",
        security = {@SecurityRequirement(name = "jwt")}
    )
    @PutMapping("/{inviteId}")
    public ApiResponse<Void> updateInvite(
        @PathVariable Long inviteId,
        @Valid @RequestBody InviteUpdateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        inviteService.updateInvite(inviteId, request, userDetails.getUserId());
        return ApiResponse.noContent();
    }

    @Operation(
        summary = "초대장 삭제",
        security = {@SecurityRequirement(name = "jwt")}
    )
    @DeleteMapping("/{inviteId}")
    public ApiResponse<Void> deleteInvite(
        @PathVariable Long inviteId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        inviteService.deleteInvite(inviteId, userDetails.getUserId());
        return ApiResponse.noContent();
    }
}

