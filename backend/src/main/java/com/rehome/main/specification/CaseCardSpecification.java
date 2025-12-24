package com.rehome.main.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.rehome.main.dto.request.CaseFormRequestDTO;
import com.rehome.main.entity.AdoptionPetArea;
import com.rehome.main.entity.AnimalSpecies;
import com.rehome.main.entity.Case;
import com.rehome.main.entity.CaseStatus;
import com.rehome.main.entity.CaseType;
import com.rehome.main.entity.City;
import com.rehome.main.entity.Contact;
import com.rehome.main.entity.PetInfo;
import com.rehome.main.entity.Region;
import com.rehome.main.entity.Shelter;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class CaseCardSpecification {
    public static Specification<Case> searchCardList(CaseFormRequestDTO dto) {
        return (root, query, cb) -> {
            query.distinct(true); // 避免多對多、多筆 join 重複資料

            List<Predicate> predicates = new ArrayList<>();

            if (dto.getFilters() == null) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            var f = dto.getFilters();

            // ====== JOIN SECTION ======
            Join<Case, CaseType> caseTypeJoin = root.join("caseType", JoinType.LEFT);
            Join<Case, CaseStatus> caseStatusJoin = root.join("caseStatus", JoinType.LEFT);

            Join<Case, PetInfo> petInfoJoin = root.join("petInfo", JoinType.LEFT);
            Join<Case, AnimalSpecies> animalSpeciesJoin = petInfoJoin.join("animalSpecies", JoinType.LEFT);
            Join<Case, Region> regionJoin = petInfoJoin.join("region", JoinType.LEFT);
            Join<Case, City> cityJoin = regionJoin.join("city", JoinType.LEFT);

            Join<Case, Contact> contactJoin = root.join("contact", JoinType.LEFT);
            Join<Case, Shelter> shelterJoin = contactJoin.join("shelter", JoinType.LEFT);

            Join<Case, AdoptionPetArea> adoptionPetAreasJoin = root.join("adoptionPetAreas", JoinType.LEFT);
            Join<Case, City> adoptionPetAreasCityJoin = adoptionPetAreasJoin.join("city", JoinType.LEFT);

            predicates.add(cb.equal(caseStatusJoin.get("id"), 2L));

            // keyword
            // case => caseNumber
            // petInfo => name、animal_species_other、breed、color、feature、chip_number
            if (dto.getFilters().getKeyword() != null && !dto.getFilters().getKeyword().isEmpty()) {
                String keyword = dto.getFilters().getKeyword();
                String patten = "%" + keyword.toLowerCase() + "%"; // 大小寫不敏感
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("caseNumber")), patten),
                        cb.like(cb.lower(petInfoJoin.get("name")), patten),
                        cb.like(cb.lower(petInfoJoin.get("animalSpeciesOther")), patten),
                        cb.like(cb.lower(petInfoJoin.get("breed")), patten),
                        cb.like(cb.lower(petInfoJoin.get("color")), patten),
                        cb.like(cb.lower(petInfoJoin.get("feature")), patten),
                        cb.equal(petInfoJoin.get("chipNumber"), keyword),
                        cb.like(petInfoJoin.get("chipNumber"), patten)));
            }

            // cities
            if (dto.getFilters().getCities() != null && !dto.getFilters().getCities().isEmpty()) {
                predicates.add(cityJoin.get("id").in(dto.getFilters().getCities()));
            }

            // source => 0:any / 1:private / 2:公立
            if (dto.getFilters().getSource() != null) {
                switch (dto.getFilters().getSource()) {
                    case 0:
                        predicates.add(caseTypeJoin.get("id").in(List.of(2, 3)));
                        break;

                    case 1:
                        predicates.add(cb.equal(caseTypeJoin.get("id"), 2));
                        break;

                    case 2:
                        predicates.add(cb.equal(caseTypeJoin.get("id"), 3));

                        // shelters
                        if (dto.getFilters().getShelters() != null && !dto.getFilters().getShelters().isEmpty()) {
                            predicates.add(shelterJoin.get("id").in(dto.getFilters().getShelters()));
                        }
                        break;

                    default:
                        break;
                }
            } else {
                predicates.add(caseTypeJoin.get("id").in(List.of(1)));
            }

            // species
            if (dto.getFilters().getSpecies() != null && !dto.getFilters().getSpecies().isEmpty()) {
                predicates.add(animalSpeciesJoin.get("id").in(dto.getFilters().getSpecies()));
            }

            // gender => male / female / "空字串"
            if (dto.getFilters().getGender() != null && !dto.getFilters().getGender().isEmpty()) {
                predicates.add(cb.equal(petInfoJoin.get("gender"), dto.getFilters().getGender()));
            }

            // sizes => small / medium / big / "空陣列"
            if (dto.getFilters().getSizes() != null && !dto.getFilters().getSizes().isEmpty()) {
                predicates.add(petInfoJoin.get("size").in(dto.getFilters().getSizes()));
            }

            // ages => child / adult / old / "空陣列"
            if (dto.getFilters().getAges() != null && !dto.getFilters().getAges().isEmpty()) {
                predicates.add(petInfoJoin.get("age").in(dto.getFilters().getAges()));
            }

            // status => 0:any / 1:開放領養中 / 2:領養媒合中
            if (dto.getFilters().getStatus() != null && dto.getFilters().getStatus() != 0) {
                if (dto.getFilters().getStatus() == 1) {
                    predicates.add(cb.lt(cb.size(root.get("adoptionMembers")), 3));
                } else if (dto.getFilters().getStatus() == 2) {
                    predicates.add(cb.equal(cb.size(root.get("adoptionMembers")), 3));
                }
            }

            // neuteredStatus
            if (dto.getFilters().getNeuteredStatus() != null && dto.getFilters().getNeuteredStatus() != 0) {
                predicates.add(cb.equal(petInfoJoin.get("isEarTipping"), dto.getFilters().getNeuteredStatus() == 1));
            }

            // hasChip
            if (dto.getFilters().getHasChip() != null && dto.getFilters().getHasChip() != 0) {
                predicates.add(cb.equal(petInfoJoin.get("isChip"), dto.getFilters().getHasChip() == 1));
            }

            // adoptionAreas
            if (dto.getFilters().getAdoptionAreas() != null && !dto.getFilters().getAdoptionAreas().isEmpty()) {
                predicates.add(adoptionPetAreasCityJoin.get("id").in(dto.getFilters().getAdoptionAreas()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
