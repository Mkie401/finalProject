package com.rehome.main.utils.mapper;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rehome.main.dto.response.CaseCardResponseDTO;
import com.rehome.main.dto.response.CaseContactResponseDTO;
import com.rehome.main.dto.response.CaseDetailResponseDTO;
import com.rehome.main.dto.response.CaseInfoResponseDTO;
import com.rehome.main.dto.response.CasePageResponseDTO;
import com.rehome.main.dto.response.PetInfoResponseDTO;
import com.rehome.main.entity.AdoptionMember;
import com.rehome.main.entity.AdoptionPetArea;
import com.rehome.main.entity.Case;
import com.rehome.main.entity.City;
import com.rehome.main.entity.Contact;
import com.rehome.main.entity.PetDetail;
import com.rehome.main.entity.PetImage;
import com.rehome.main.entity.PetInfo;
import com.rehome.main.entity.Region;
import com.rehome.main.entity.Shelter;
import com.rehome.main.repository.FavoriteRepository;

@Component
public class CaseMapper {
    @Autowired
    private FavoriteRepository favoriteRepository;

    public CaseCardResponseDTO toCaseCardDTO(Case entity, Long memberId, boolean isAdoption) {
        CaseCardResponseDTO dto = new CaseCardResponseDTO();

        dto.setId(entity.getId());
        dto.setCaseNumber(entity.getCaseNumber());
        dto.setCaseDateStart(entity.getCaseDateStart());

        List<AdoptionMember> adoptionMembers = entity.getAdoptionMembers();
        boolean isFavorites = favoriteRepository.existsByMemberIdAndPetCaseId(memberId, entity.getId());

        dto.setIsFavorites(isFavorites);
        dto.setIsPublic(entity.getCaseType().getId() == 3);
        dto.setIsOpen(adoptionMembers.size() < 3);

        Optional.ofNullable(entity.getPetImage())          // 如果 list 為 null，就用 empty list
				.orElse(Collections.emptyList())
				.stream()
				.filter(Objects::nonNull)                   // 過濾 list 裡的 null 元素
				.filter(imageEntity -> imageEntity.getSortOrder() != null && imageEntity.getSortOrder() == 1)
				.findFirst()
				.ifPresent(imageEntity -> dto.setPhoto(imageEntity.getPhoto()));

        PetInfo petInfo = entity.getPetInfo();

        String species = Optional.ofNullable(
                petInfo.getAnimalSpeciesOther())
                .orElse(petInfo.getAnimalSpecies().getName());

        dto.setPetName(petInfo.getName());
        dto.setSpecies(species);
        dto.setBreed(petInfo.getBreed());
        dto.setSize(petInfo.getSize());

        if (isAdoption) {
            Region region = petInfo.getRegion();
            City city = region.getCity();

            dto.setRegion(city.getName() + " " + region.getName());
        } else {
            PetDetail petDetail = entity.getPetDetail();
            Region region = petDetail.getRegion();
            City city = region.getCity();
            dto.setLostDate(petDetail.getLostDate());
            dto.setLostRegion(city.getName() + " " + region.getName());
        }

        return dto;
    }

    public CasePageResponseDTO toCasePageDTO(Case entity, Long memberId) {
        Long caseType = entity.getCaseType().getId();
        List<byte[]> photos = Optional.ofNullable(entity.getPetImage()) // 判斷 list 是否為 null
				.orElse(Collections.emptyList())
				.stream()
				.filter(Objects::nonNull) // 避免 list 裡有 null
				.map(PetImage::getPhoto)
				.filter(Objects::nonNull) // 避免 photo 為 null
				.collect(Collectors.toList());


        CaseInfoResponseDTO caseInfo = CaseInfoResponseDTO.builder()
                .id(entity.getId())
                .caseNumber(entity.getCaseNumber())
                .caseDateStart(entity.getCaseDateStart())
                .isFavorites(favoriteRepository.existsByMemberIdAndPetCaseId(memberId, entity.getId()))
				.isMissing(caseType == 1)
                .isPublic(caseType == 3)
                .isOpen(entity.getAdoptionMembers().size() < 3)
                .photo(photos)
                .build();

        Contact contactTemp = entity.getContact();
        CaseContactResponseDTO.CaseContactResponseDTOBuilder caseContactBuilder = CaseContactResponseDTO.builder()
                .isPhoneDisplay(contactTemp.getIsPhoneDisplay())
                .isEmailDisplay(contactTemp.getIsEmailDisplay());

        if (caseType != 3) {
            caseContactBuilder
                    .name(contactTemp.getName())
                    .tel(contactTemp.getTel())
                    .mail(contactTemp.getMail());
        } else {
            Shelter shelterTemp = entity.getContact().getShelter();
            caseContactBuilder
                    .name(shelterTemp.getName())
                    .tel(shelterTemp.getPhone())
                    .addr(shelterTemp.getAddress());
        }

        PetInfo petInfoTemp = entity.getPetInfo();
        Region regionTemp = petInfoTemp.getRegion();
        City cityTemp = regionTemp.getCity();
        PetInfoResponseDTO petInfo = PetInfoResponseDTO.builder()
                .petName(petInfoTemp.getName())
                .species(
                        Optional.ofNullable(petInfoTemp.getAnimalSpeciesOther())
                                .orElse(petInfoTemp.getAnimalSpecies().getName()))
                .breed(petInfoTemp.getBreed())
                .gender(petInfoTemp.getGender())
                .size(petInfoTemp.getSize())
                .age(petInfoTemp.getAge())
                .color(petInfoTemp.getColor())
                .feature(petInfoTemp.getFeature())
                .isEarTipping(petInfoTemp.getIsEarTipping())
                .isChip(petInfoTemp.getIsChip())
                .chipNumber(petInfoTemp.getChipNumber())
                .region(cityTemp.getName() + " " + regionTemp.getName())
                .build();

        PetDetail petDetailTemp = entity.getPetDetail();
        CaseDetailResponseDTO.CaseDetailResponseDTOBuilder detailBuilder = CaseDetailResponseDTO.builder()
                .description(petDetailTemp.getDescription());

        switch (caseType.intValue()) {
            case 1:
                Region lostRegionTemp = petDetailTemp.getRegion();
                City lostCityTemp = lostRegionTemp.getCity();
                detailBuilder.lostDetail(
                        CaseDetailResponseDTO.LostDetail.builder()
                                .lostDate(petDetailTemp.getLostDate())
                                .lostRegion(lostCityTemp.getName() + " " + lostRegionTemp.getName())
                                .lostAddr(petDetailTemp.getLostAddr())
                                .lng(petDetailTemp.getLng())
                                .lat(petDetailTemp.getLat())
                                .lostProcess(petDetailTemp.getLostProcess())
                                .build());
                break;
            case 2:
				List<AdoptionPetArea> adoptionPetAreas = entity.getAdoptionPetAreas();
				List<String> cityList = adoptionPetAreas.stream()
						.filter(Objects::nonNull)
						.map(item -> item.getCity().getName()).toList();
				
                detailBuilder.adoptionDetail(
                        CaseDetailResponseDTO.AdoptionDetail.builder()
                                .medicalInfo(petDetailTemp.getMedicalInfo())
                                .adoptionRequ(petDetailTemp.getAdoptionRequ())
                                .isFollowAger(petDetailTemp.getIsFollowAger())
                                .isFamilyAger(petDetailTemp.getIsFamilyAger())
                                .isAgeLimit(petDetailTemp.getIsAgeLimit())
								.cityList(cityList)
                                .build());
                break;
            case 3:
                detailBuilder.shelterDetail(
                        CaseDetailResponseDTO.ShelterDetail.builder()
                                .entryDates(petDetailTemp.getEntryDate())
                                .entryDays(ChronoUnit.DAYS.between(petDetailTemp.getEntryDate().toLocalDate(), LocalDate.now()))
                                .foundPlace(petDetailTemp.getFoundPlace())
                                .build());
                break;

            default:
                break;
        }

        return CasePageResponseDTO.builder()
                .caseInfo(caseInfo)
                .caseContact(caseContactBuilder.build())
                .petInfo(petInfo)
                .detail(detailBuilder.build())
                .build();
    }
}
