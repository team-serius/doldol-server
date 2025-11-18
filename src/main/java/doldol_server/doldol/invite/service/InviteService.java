package doldol_server.doldol.invite.service;

import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.invite.dto.request.InviteCommentCreateRequest;
import doldol_server.doldol.invite.dto.request.InviteCreateRequest;
import doldol_server.doldol.invite.dto.response.InviteCommentResponse;
import doldol_server.doldol.invite.dto.response.InviteResponse;
import doldol_server.doldol.invite.entity.Invite;
import doldol_server.doldol.invite.entity.InviteComment;
import doldol_server.doldol.invite.errorCode.InviteErrorCode;
import doldol_server.doldol.invite.repository.InviteCommentRepository;
import doldol_server.doldol.invite.repository.InviteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;
    private final InviteCommentRepository inviteCommentRepository;

    @Transactional
    public InviteResponse createInvite(InviteCreateRequest request) {
        Invite invite = Invite.builder()
            .title(request.getTitle())
            .eventDateTime(request.getEventDateTime())
            .location(request.getLocation())
            .content(request.getContent())
            .sender(request.getSender())
            .theme(request.getTheme())
            .inviteCode(UUID.randomUUID().toString())
            .build();

        Invite saved = inviteRepository.save(invite);
        return InviteResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public InviteResponse getInvite(Long inviteId) {
        Invite invite = inviteRepository.findWithCommentsByInviteId(inviteId)
            .orElseThrow(() -> new CustomException(InviteErrorCode.INVITE_NOT_FOUND));
        return InviteResponse.from(invite);
    }

    @Transactional
    public InviteCommentResponse addComment(Long inviteId, InviteCommentCreateRequest request) {
        Invite invite = inviteRepository.findById(inviteId)
            .orElseThrow(() -> new CustomException(InviteErrorCode.INVITE_NOT_FOUND));

        InviteComment comment = InviteComment.builder()
            .author(request.getAuthor())
            .content(request.getContent())
            .build();
        invite.addComment(comment);
        inviteCommentRepository.save(comment);

        return InviteCommentResponse.from(comment);
    }

    @Transactional(readOnly = true)
    public List<InviteCommentResponse> getComments(Long inviteId) {
        Invite invite = inviteRepository.findWithCommentsByInviteId(inviteId)
            .orElseThrow(() -> new CustomException(InviteErrorCode.INVITE_NOT_FOUND));
        return invite.getComments()
            .stream()
            .map(InviteCommentResponse::from)
            .toList();
    }
}

