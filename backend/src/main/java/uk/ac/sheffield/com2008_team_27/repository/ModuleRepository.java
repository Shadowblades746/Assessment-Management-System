package uk.ac.sheffield.com2008_team_27.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.sheffield.com2008_team_27.domain.Module;
import java.util.List;
import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module, String> {
    List<Module> findAllBySchool(String school);
    Optional<Module> findByModuleName(String moduleName);
    Optional<Module> findByModuleCode(String moduleCode);



}
