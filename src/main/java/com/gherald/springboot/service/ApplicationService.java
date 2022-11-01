package com.gherald.springboot.service;

import com.gherald.springboot.dao.*;
import com.gherald.springboot.dto.ChangeReviewDto;
import com.gherald.springboot.dto.CodeInspectionDto;
import com.gherald.springboot.dto.QuestionnaireDto;
import com.gherald.springboot.model.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class ApplicationService {

    @Autowired
    ChangeRepository changeRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    ParticipantRepository participantRepository;

    @Autowired
    CodeInspectionRepository codeInspectionRepository;

    @Autowired
    ChangeReviewRepository changeReviewRepository;

    @Autowired
    QuestionnaireRepository questionnaireRepository;

    @Transactional
    public Participant createParticipant(String tool, String project, Integer reviewOrder) {
        Participant participant = new Participant();
        participant.setTool(tool);
        participant.setProject(project);
        participant.setReviewOrder(reviewOrder);
        participant.setCompleted(false);
        participantRepository.save(participant);
        initiateReview(participant.getId());
        return participant;
    }

    @Transactional
    public Participant createParticipantWithChangeId(String tool, String project, Integer reviewOrder, List<String> changes) {
        Participant participant = new Participant();
        participant.setTool(tool);
        participant.setProject(project);
        participant.setReviewOrder(reviewOrder);
        participant.setCompleted(false);
        participantRepository.save(participant);
        initiateReviewWithChangeId(participant.getId(), changes);
        return participant;
    }

    @Transactional
    public void initiateReview(String id) {
        Participant participant = participantRepository.findParticipantById(id);
        String project = participant.getProject();
        List<Change> practiceChanges = changeRepository.findAllByProjectAndPractice(project, true);
        List<Change> experimentChanges = changeRepository.findAllByProjectAndPractice(project, false);
        Set<Integer> authorIds = new HashSet<>();
        Boolean cleanChangeAssigned = false;
        for (Change practiceChange : practiceChanges) {
            ChangeReview changeReview = new ChangeReview();
            changeReview.setChange(practiceChange);
            changeReview.setParticipant(participant);
            changeReviewRepository.save(changeReview);
        }
        while (authorIds.size() < 3) {
            int randomIndex = new Random().nextInt(experimentChanges.size());
            Change randomChange = experimentChanges.get(randomIndex);
            if ((!cleanChangeAssigned || (randomChange.getBugDensity() > 0)) && !authorIds.contains(randomChange.getAuthor().getAccountId())) {
                experimentChanges.remove(randomIndex);
                ChangeReview changeReview = new ChangeReview();
                changeReview.setChange(randomChange);
                changeReview.setParticipant(participant);
                changeReviewRepository.save(changeReview);
                authorIds.add(randomChange.getAuthor().getAccountId());
                if (randomChange.getBugDensity() == 0) {
                    cleanChangeAssigned = true;
                }
            }
        }
    }

    @Transactional
    public void initiateReviewWithChangeId(String id, List<String> changes) {
        Participant participant = participantRepository.findParticipantById(id);
        String project = participant.getProject();
        List<Change> practiceChanges = changeRepository.findAllByProjectAndPractice(project, true);
        for (Change practiceChange : practiceChanges) {
            ChangeReview changeReview = new ChangeReview();
            changeReview.setChange(practiceChange);
            changeReview.setParticipant(participant);
            changeReviewRepository.save(changeReview);
        }
        for (String changeId : changes) {
            Change change = changeRepository.findChangeById(changeId);
            ChangeReview changeReview = new ChangeReview();
            changeReview.setChange(change);
            changeReview.setParticipant(participant);
            changeReviewRepository.save(changeReview);
        }
    }

//    @Transactional
//    public Participant updateRiskLevel(Integer reviewId, Integer riskLevel) {
//        ChangeReview changeReview = changeReviewRepository.findChangeReviewById(reviewId);
//        changeReview.setRiskLevel(riskLevel);
//        changeReviewRepository.save(changeReview);
//        Participant participant = changeReview.getParticipant();
//        return participant;
//    }

    @Transactional
    public void updateRiskLevel(List<ChangeReviewDto> changeReviews) {
        for (ChangeReviewDto changeReviewDto : changeReviews) {
            ChangeReview changeReview = changeReviewRepository.findChangeReviewById(changeReviewDto.getId());
            changeReview.setRiskLevel(changeReviewDto.getRiskLevel());
            changeReviewRepository.save(changeReview);
        }
    }

    @Transactional
    public void updateTaskATime(String participantId, Integer time) {
        Participant participant = participantRepository.findParticipantById(participantId);
        participant.setTaskATime(time);
        participantRepository.save(participant);
    }

    @Transactional
    public ChangeReview createCodeInspection(Integer reviewId, Integer reviewTime, List<CodeInspectionDto> codeInspections) {
        ChangeReview changeReview = changeReviewRepository.findChangeReviewById(reviewId);
        changeReview.setReviewTime(reviewTime);
        for (CodeInspectionDto codeInspectionDto : codeInspections) {
            CodeInspection codeInspection = new CodeInspection();
            codeInspection.setFile(codeInspectionDto.getFile());
            codeInspection.setLine(codeInspectionDto.getLine());
            codeInspection.setComment(codeInspectionDto.getComment());
            codeInspection.setChangeReview(changeReview);
            codeInspectionRepository.save(codeInspection);
        }
        return changeReview;
    }

    @Transactional
    public void createQuestionnaire(QuestionnaireDto questionnaireDto) {
        Participant participant = participantRepository.findParticipantById(questionnaireDto.getParticipantId());
        participant.setCompleted(true);
        participantRepository.save(participant);
        if (questionnaireRepository.findQuestionnaireByParticipantId(participant.getId()) == null) {
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setUnderstandability(questionnaireDto.getUnderstandability());
            questionnaire.setDifficulty(questionnaireDto.getDifficulty());
            questionnaire.setFitness(questionnaireDto.getFitness());
            questionnaire.setUsability(questionnaireDto.getUsability());
            questionnaire.setOtherTool(questionnaireDto.getOtherTool());
            questionnaire.setProblem(questionnaireDto.getProblem());
            questionnaire.setFeedback(questionnaireDto.getFeedback());
            questionnaire.setAllowInterview(questionnaireDto.getAllowInterview());
            questionnaire.setParticipant(participant);
            questionnaireRepository.save(questionnaire);
        }
    }
}
