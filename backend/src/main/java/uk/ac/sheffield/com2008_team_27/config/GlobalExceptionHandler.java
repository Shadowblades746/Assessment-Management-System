package uk.ac.sheffield.com2008_team_27.config;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import uk.ac.sheffield.com2008_team_27.dto.ApiErrorDTO;
import uk.ac.sheffield.com2008_team_27.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AssessmentDoesNotExistException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(AssessmentDoesNotExistException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    @ExceptionHandler(ForbidModuleLeadDowngradeException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(ForbidModuleLeadDowngradeException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDTO);
    }

    @ExceptionHandler(ModuleExistsException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(ModuleExistsException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(ForbidCheckingException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(ForbidCheckingException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDTO);
    }


    @ExceptionHandler(ModuleDoesNotExist.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(ModuleDoesNotExist ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    @ExceptionHandler(ModuleLeadForModuleException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(ModuleLeadForModuleException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(ModuleModeratorForModuleException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(ModuleModeratorForModuleException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(NotAcademicStaffException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(NotAcademicStaffException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(NotExamsOfficerException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(NotExamsOfficerException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(NotFormalExamException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(NotFormalExamException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(UserDeletedException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(UserDeletedException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(AssessmentExistsException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(AssessmentExistsException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(UserDoesNotExistException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    @ExceptionHandler(NotExternalExaminerException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(NotExternalExaminerException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(ForbiddenToModerateException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(ForbiddenToModerateException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(EmailExistsException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(ModuleDoesNotExistException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(ModuleDoesNotExistException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }


    @ExceptionHandler(RoleDoesNotExistException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(RoleDoesNotExistException ex)
    {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldname = error instanceof FieldError fieldError
                    ? fieldError.getField()
                    : error.getObjectName();
            errors.computeIfAbsent(fieldname, k -> new ArrayList<>())
                    .add(error.getDefaultMessage());
        });
        ApiErrorDTO errorDTO = new ApiErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Some of the form values were invalid",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }
}
