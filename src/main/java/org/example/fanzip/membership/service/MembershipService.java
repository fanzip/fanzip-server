package org.example.fanzip.membership.service;


import org.example.fanzip.membership.dto.MembershipSubscribeRequestDTO;
import org.example.fanzip.membership.dto.MembershipSubscribeResponseDTO;


public interface MembershipService{

    MembershipSubscribeResponseDTO subscribe(MembershipSubscribeRequestDTO requestDTO, long userId);

}