package com.gherald.springboot.controller;

import com.gherald.springboot.dao.LineRepository;
import com.gherald.springboot.dao.MethodRepository;
import com.gherald.springboot.dto.*;
import com.gherald.springboot.model.*;
import com.gherald.springboot.dao.ParticipantRepository;
import com.gherald.springboot.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class ParticipantController {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MethodRepository methodRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/api/participants/add")
    public ParticipantDto createParticipant(@RequestParam String tool, @RequestParam String project, @RequestParam Integer reviewOrder) {
        Participant participant = applicationService.createParticipant(tool, project, reviewOrder);
//        String id = participant.getId();
//        participant = applicationService.initiateReview(id);
        return convertToDto(participant);
    }

    @PostMapping("/api/participants/addWithChange")
    public ParticipantDto createParticipantWithChangeId(@RequestParam String tool, @RequestParam String project, @RequestParam Integer reviewOrder, @RequestParam String change1, @RequestParam String change2, @RequestParam String change3) {
        List<String> changes = Arrays.asList(change1, change2, change3);
        Participant participant = applicationService.createParticipantWithChangeId(tool, project, reviewOrder, changes);
//        String id = participant.getId();
//        participant = applicationService.initiateReview(id);
        return convertToDto(participant);
    }

//    @PostMapping("/api/participants/{id}")
//    public ParticipantDto initiateReview(@PathVariable String id) {
//        Participant participant = applicationService.initiateReview(id);
//        return convertToDto(participant);
//    }

    @GetMapping("/api/participants")
    public List<ParticipantDto> getParticipants() {
        List<ParticipantDto> participants = new ArrayList<>();
        for (Participant participant : participantRepository.findAll()) {
            participants.add(convertToDto(participant));
        }
        return participants;
    }

    @GetMapping("/api/participants/{id}")
    public ParticipantDto findParticipantById(@PathVariable String id) {
        return convertToDto(participantRepository.findParticipantById(id));
    }

//    @PostMapping("/api/risk-assessment")
//    public ParticipantDto updateRiskLevel(@RequestParam Integer reviewId, @RequestParam Integer riskLevel) {
//        Participant participant = applicationService.updateRiskLevel(reviewId, riskLevel);
//        return convertToDto(participant);
//    }

    @PostMapping("/api/risk-assessment")
    public void submitTaskA(@RequestBody ParticipantDto participant) {
        Integer taskATime = participant.getTaskATime();
        applicationService.updateTaskATime(participant.getId(), taskATime);
        List<ChangeReviewDto> changeReviews = participant.getChangeReviews();
        applicationService.updateRiskLevel(changeReviews);
    }

    @PostMapping("/api/code-review")
    public ChangeReviewDto createCodeInspection(@RequestBody ChangeReviewDto changeReviewDto) {
        ChangeReview changeReview = applicationService.createCodeInspection(changeReviewDto.getId(), changeReviewDto.getReviewTime(), changeReviewDto.getCodeInspections());
        return convertToDto(changeReview);
    }

    @PostMapping("/api/questionnaire")
    public void createQuestionnaire(@RequestBody QuestionnaireDto questionnaireDto) {
        applicationService.createQuestionnaire(questionnaireDto);
    }

    private ParticipantDto convertToDto(Participant participant)  {
        Integer taskATime = (participant.getTaskATime() == null) ? null : participant.getTaskATime();
        List<ChangeReviewDto> changeReviews = new ArrayList<>();
        if (participant.getChangeReviews() != null) {
            for (ChangeReview changeReview : participant.getChangeReviews()) {
                changeReviews.add(convertToDto(changeReview));
            }
        }
        ParticipantDto participantDto = new ParticipantDto(participant.getId(), participant.getTool(), participant.getProject(), taskATime, participant.getCompleted(), changeReviews, participant.getReviewOrder());
        return participantDto;
    }

    public ChangeDto convertToDto(Change change) {
        if (change == null) {
            return null;
        }
        ChangeDto changeDto = new ChangeDto(change.getId(), change.getRepo(), change.getBranch(), change.getSubject(),
                change.getCreated(), change.getUpdated(), change.getInsertions(), change.getDeletions(),
                change.getNumber(), change.getParent(), change.getCommitMsg(), change.getProject(),
                change.getAuthorPriorChanges(), change.getAuthorPriorBugs(), change.getRiskScore(),
                change.getBugDensity(), change.getPractice(), change.getAuthorRiskScore(), change.getFileRiskScore(),
                change.getMethodRiskScore(), change.getAuthorPriorChangeScore(), change.getAuthorRecentChangeScore(),
                change.getAuthorFileAwareness(), change.getAuthorRecentChanges(), change.getAuthorFilePriorChanges());
        List<FileDto> files = new ArrayList<>();
        for (File file : change.getFiles()) {
            FileDto fileDto = new FileDto(file.getFilename(), file.getStatus(), file.getInsertions(), file.getDeletions(), file.getCodeA(), file.getCodeB(), file.getDiff(), file.getPriorBugs(), file.getPriorChanges());
            List<MethodDto> methods = new ArrayList<>();
            for (Method method : methodRepository.findAllByFileIdAndChangeId(file.getId(), change.getId())) {
                MethodDto methodDto = new MethodDto(method.getName(), method.getStartLine(), method.getEndLine(), method.getPriorChanges(), method.getPriorBugs());
                methods.add(methodDto);
            }
            fileDto.setMethods(methods);
            List<LineDto> lines = new ArrayList<>();
            for (Line line : lineRepository.findAllByFileIdAndChangeId(file.getId(), change.getId())) {
                LineDto lineDto = new LineDto(line.getLineNumber(), line.getCode(), line.getRiskScore(), line.getRiskTokens());
                lines.add(lineDto);
            }
            fileDto.setLines(lines);
            files.add(fileDto);
        }
        changeDto.setFiles(files);
        AuthorDto authorDto = new AuthorDto(change.getAuthor().getAccountId(), change.getAuthor().getName(), change.getAuthor().getEmail(), change.getAuthor().getUsername(), change.getAuthor().getProject());
        changeDto.setAuthor(authorDto);
        return changeDto;
    }

    private ChangeReviewDto convertToDto(ChangeReview changeReview) {
        Integer riskLevel = (changeReview.getRiskLevel() == null) ? null : changeReview.getRiskLevel();
        Integer reviewTime = (changeReview.getReviewTime() == null) ? null : changeReview.getReviewTime();
        ChangeDto change = convertToDto(changeReview.getChange());
        List<CodeInspectionDto> codeInspections = new ArrayList<>();
        if (changeReview.getCodeInspections() != null) {
            for (CodeInspection codeInspection : changeReview.getCodeInspections()) {
                codeInspections.add(convertToDto(codeInspection));
            }
        }
        ChangeReviewDto changeReviewDto = new ChangeReviewDto(changeReview.getId(), change, riskLevel, reviewTime, codeInspections);
        return changeReviewDto;
    }

    private CodeInspectionDto convertToDto(CodeInspection codeInspection) {
        CodeInspectionDto codeInspectionDto = new CodeInspectionDto(codeInspection.getFile(), codeInspection.getLine(), codeInspection.getComment());
        return codeInspectionDto;
    }
}
