package uk.ac.sheffield.com2008_team_27.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface AssessmentRepository extends JpaRepository<Assessment, String> {
    List<Assessment> findAllByType(Assessment.Type type);
    List<Assessment> findAllBySetterID(int setterID);
    List<Assessment> findAllByCheckerID(int checkerID);
    List<Assessment> findAllById_ModuleCode(String moduleCode);
    Optional<Assessment> findById_ModuleCodeAndId_Title(String moduleCode, String assessmentTitle);
    List<Assessment> findAllBySetDate(LocalDateTime setDate);
    List<Assessment> findAllByStatus(Assessment.AssessmentStatus status);

    List<Assessment> getAssessmentsByCheckerIDAndSetterID(int checkerID, int setterID);

    List<Assessment> findAllById_ModuleCodeAndSetterID(String moduleCode, int setterID);
    List<Assessment> findAllById_ModuleCodeAndCheckerID(String moduleCode, int checkerID);

//    List<Assessment> findAllByModulecode_AndSetterID(String moduleCode, Integer userId);
}
