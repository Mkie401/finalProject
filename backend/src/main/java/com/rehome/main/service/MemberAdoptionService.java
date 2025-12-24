package com.rehome.main.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rehome.main.dto.request.MemberAdoptionFormDTO;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.CaseCardResponseDTO;
import com.rehome.main.dto.response.MemberCenterApplyAdoptionListResponseDTO;
import com.rehome.main.dto.response.MemberCenterFavoritesListResponseDTO;
import com.rehome.main.entity.AdoptionMember;
import com.rehome.main.entity.AdoptionQuestion;
import com.rehome.main.entity.AdoptionStatus;
import com.rehome.main.entity.Case;
import com.rehome.main.entity.Member;
import com.rehome.main.entity.Question;
import com.rehome.main.repository.AdoptionMemberRepository;
import com.rehome.main.repository.AdoptionStatusReopsitory;
import com.rehome.main.repository.CaseRepository;
import com.rehome.main.repository.MemberRepository;
import com.rehome.main.repository.QuestionRepository;
import com.rehome.main.utils.mapper.CaseMapper;

@Service
public class MemberAdoptionService {

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private CaseRepository caseRepository;
	@Autowired
	private AdoptionStatusReopsitory adoptionStatusReopsitory;
	@Autowired
	private AdoptionMemberRepository adoptionMemberRepository;
	@Autowired
	private QuestionRepository questionRepository;
	@Autowired
	private CaseMapper caseMapper;

	@Transactional
	public ApiResponse<?> saveAdoptionForm(MemberAdoptionFormDTO dto, Long memberId) {
		if (adoptionMemberRepository.existsByMemberIdAndPetCase_CaseNumber(memberId, dto.getCaseNumber())) {
			return ApiResponse.fail("你已經提交過申請，不能重複申請");
		}

		List<Long> adoptionStatusIds = Arrays.asList(1L, 2L, 3L);
		int count = adoptionMemberRepository.countByMemberIdAndAdoptionStatusIdIn(memberId, adoptionStatusIds);
		if (count >= 3) {
			return ApiResponse.fail("你的領養數量已達上限，不能再領養嚕");
		}

		// 取得相依
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new RuntimeException("Member not found"));

		Case petCase = caseRepository.findByCaseNumber(dto.getCaseNumber())
				.orElseThrow(() -> new RuntimeException("Case not found"));

		AdoptionStatus status = adoptionStatusReopsitory.findById(1L)
				.orElseThrow(() -> new RuntimeException("Status not found"));

		AdoptionMember adoptionMember = new AdoptionMember();
		adoptionMember.setMember(member);
		adoptionMember.setPetCase(petCase);
		adoptionMember.setAdoptionStatus(status);

		adoptionMember.setMaritalStatus(dto.getMaritalStatus());
		adoptionMember.setEmploymentStatus(dto.getEmploymentStatus());

		adoptionMember.setAdoptionQuestions(new ArrayList<>());

		if (dto.getQuestions() != null) {
			for (MemberAdoptionFormDTO.Question q : dto.getQuestions()) {
				// 取得相依
				Question question = questionRepository.findById(q.getQuestionId())
						.orElseThrow(() -> new RuntimeException("Question not found"));

				AdoptionQuestion adoptionQuestion = new AdoptionQuestion();
				adoptionQuestion.setQuestion(question);
				adoptionQuestion.setAnswer(q.getAnswer());

				// 雙向關聯
				adoptionQuestion.setAdoptionMember(adoptionMember);
				adoptionMember.getAdoptionQuestions().add(adoptionQuestion);
			}
		}

		adoptionMemberRepository.save(adoptionMember);
		return ApiResponse.success("OK");
	}

	@Transactional(readOnly = true)
	public List<MemberCenterApplyAdoptionListResponseDTO> getAdoptionList(Long memberId) {
		List<AdoptionMember> adoptionMembers = adoptionMemberRepository.findByMemberId(memberId);
		List<MemberCenterApplyAdoptionListResponseDTO> dtos = adoptionMembers.stream()
				.map(entity -> {
					CaseCardResponseDTO cardDto = caseMapper.toCaseCardDTO(entity.getPetCase(), memberId, true);
					return MemberCenterApplyAdoptionListResponseDTO.builder()
							.isRemove(entity.getPetCase().getCaseStatus().getId() == 4L)
							.createdAt(entity.getCreatedAt())
							.endAt(entity.getEndAt())
							.card(cardDto)
							.build();
				})
				.toList();
		return dtos;
	}
}
