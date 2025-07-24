package org.example.fanzip.membership.controller;


import lombok.RequiredArgsConstructor;
import org.example.fanzip.membership.dto.MembershipSubscribeRequestDTO;
import org.example.fanzip.membership.dto.MembershipSubscribeResponseDTO;
import org.example.fanzip.membership.service.MembershipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/memberships")
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/subscribe")
    public ResponseEntity<MembershipSubscribeResponseDTO> subscribe(
            @RequestHeader("X-USER-ID") long userId,
            @RequestBody MembershipSubscribeRequestDTO requestDTO
    ) {
        MembershipSubscribeResponseDTO responseDTO =
                membershipService.subscribe(requestDTO, userId);

        return ResponseEntity.ok(responseDTO);
    }

}