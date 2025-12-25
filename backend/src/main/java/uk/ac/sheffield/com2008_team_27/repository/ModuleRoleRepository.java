package uk.ac.sheffield.com2008_team_27.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.sheffield.com2008_team_27.domain.Module;
import uk.ac.sheffield.com2008_team_27.domain.ModuleRole;

import java.util.List;
import java.util.Optional;

public interface ModuleRoleRepository extends JpaRepository<ModuleRole, Long> {
    // Find by userID
    List<ModuleRole> findByUserId(long id);

    // Find by module's moduleCode
    List<ModuleRole> findByModule_ModuleCode(String moduleCode);

    ModuleRole findByUserIdAndModule_ModuleCodeAndRole(long userId, String moduleCode, ModuleRole.Role role);

    Optional<ModuleRole> findByUserIdAndRole(int userId,  ModuleRole.Role role);

    List<ModuleRole> findByModule_ModuleCodeAndRole(String moduleCode, ModuleRole.Role role);
    Optional<ModuleRole> findFirstByModule_ModuleCodeAndRole(String moduleCode, ModuleRole.Role role);

    void deleteByUserIdAndModule_ModuleCode(long userId, String moduleCode);

    void deleteByModule_ModuleCode(String moduleCode);

    Optional<ModuleRole> findByModule_ModuleCodeAndUser_Id(String moduleModuleCode, Integer userId);
}
