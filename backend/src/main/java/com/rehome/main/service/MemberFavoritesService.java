package com.rehome.main.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rehome.main.dto.request.FavoritesAddRequestDTO;
import com.rehome.main.dto.response.CaseCardResponseDTO;
import com.rehome.main.dto.response.MemberCenterFavoritesListResponseDTO;
import com.rehome.main.entity.Case;
import com.rehome.main.entity.Favorite;
import com.rehome.main.entity.Member;
import com.rehome.main.repository.CaseRepository;
import com.rehome.main.repository.FavoriteRepository;
import com.rehome.main.repository.MemberRepository;
import com.rehome.main.utils.mapper.CaseMapper;

@Service
public class MemberFavoritesService {

    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private CaseMapper caseMapper;

    @Transactional
    public Boolean putFavorites(FavoritesAddRequestDTO dto, Long memberId) {
        // 先檢查是否已收藏
        if (favoriteRepository.existsByMemberIdAndPetCaseCaseNumber(memberId, dto.getCaseNumber())) {
            return false;
        }

        // 新增收藏
        Favorite favorite = new Favorite();
        Member member = memberRepository.getReferenceById(memberId);
        Case petCase = caseRepository.getReferenceByCaseNumber(dto.getCaseNumber());

        favorite.setMember(member);
        favorite.setPetCase(petCase);
        favoriteRepository.save(favorite);

        return true;
    }

    @Transactional
    public Boolean deleteFavorites(String caseNumber, Long memberId) {
        return favoriteRepository.deleteByMemberIdAndPetCaseCaseNumber(memberId, caseNumber) > 0;
    }

    @Transactional(readOnly = true)
    public List<MemberCenterFavoritesListResponseDTO> getFavoritesList(Long memberId, boolean isAdoption, Integer page, Integer size) {
        List<Long> caseTypeIds = isAdoption ? Arrays.asList(2L, 3L) : Arrays.asList(1L);
        List<Long> caseStatusIds = Arrays.asList(2L, 4L);
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : page, size, Sort.by("favoritesDate").descending());
        List<Favorite> favorites = favoriteRepository.findByMemberIdAndPetCase_CaseTypeIdInAndPetCase_CaseStatusIdIn(memberId, caseTypeIds, caseStatusIds, pageable);

        List<MemberCenterFavoritesListResponseDTO> responseDTOs = favorites.stream().map(entity -> {
            CaseCardResponseDTO cardDto = caseMapper.toCaseCardDTO(entity.getPetCase(), memberId, isAdoption);
            return MemberCenterFavoritesListResponseDTO.builder()
                    .isRemove(entity.getPetCase().getCaseStatus().getId() == 4L)
                    .favoritesDate(entity.getFavoritesDate())
                    .card(cardDto)
                    .build();
        }).toList();

        return responseDTOs;
    }
}
