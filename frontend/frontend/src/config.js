
export const API_BASE_URL = "http://localhost:8080"

export const MODULES_API = `${API_BASE_URL}/modules`;
/** ===== GET PARAMETERS =====
 * count (int) -> [Module]: The maximum number of returned items
 * code (String) -> Module: Returns just the module with that code
 * user (userID) -> [(Module, Role)]: Returns the modules where the user has a role in it i.e. Moderator, Module Lead
 * user (userId), count(int) -> [(Module, Role)]: Combines both GET functions
 * 
 * ===== RETURN LAYOUT ======
 * [Module]
 *  [
 *      {
 *          moduleCode: String
 *          moduleName: String
 *          school: string
 *          degreeLevel: string
 *          moderatorID: int
 *          moduleLeadID: int
 *   
 *      }
 * ]
 * [(Module, Role)]
 * [
 *      {
 *          module: 
 *          {
 *               moduleCode: String
 *               moduleName: String
 *               school: string
 *               degreeLevel: string
 *               moderatorID: int
 *               moduleLeadID: int
 *          }
 *          role: String
 *      }
 * ]
**/

export const USERS_API = `${API_BASE_URL}/users`;
/**
 * ===== GET PARAMETERS =====
 * count (int) -> [User]: The maximum number of returned items
 * userId (int) -> User: Returns the staff member with that ID.
 * 
 * ====== RETURN LAYOUT =====
 * [Users]
 *  [
 *      {
 *          id: Int
 *          forename: String
 *          surname: String
 *          email: String
 *      }
 *  ]
 */


export const ASSESSMENTS_API = `${API_BASE_URL}/assessments/`;
/**
 * ===== GET PARAMETERS =====
 * module (String) -> [Assessment]: Returns the assessments for a module
 * module (String), count(int) -> [Assessment]: Returns the assessments for a module
 * title (String) -> Assessment: Returns the specific assessment with that title
 * userId (int) -> [(Assessment, Role)]: Returns the assessments where the user has a role i.e. Setter, Checker
 * title (String), 
 * 
 * ====== POST METHODS =====
 * ====== RETURN LAYOUT ======
 *  [Assessment]
 *  [
 *      {
 *          title: String
 *          type: String
 *          moduleCode: String
 *          setDate: String
 *          autograded: Bool
 *          status: String
 *          lastUpdated: String
 *          AssessmentByDate: String 
 *          setterID: int
 *          checkerID: int
 *          externalExaminerID: int
 *      }
 *  ]
 */

export const PROGRESS_API = `${API_BASE_URL}/assessments/progress`;
/**
 * ===== GET PARAMETERS ======
 * assessmentTitle (String) -> Progress
 * 
 * ==== RETURN LAYOUT ========
 * Progress 
 *  {
 *      progress: int
 *      maxProgress: int
 *      name: String
 *      roleRequired: userID
 *  }
 */

export const FEEDBACK_API = `${API_BASE_URL}/assessment/feedback/`;
/**
 * ===== GET PARAMETERS ======
 * module (String) -> [Feedback]
 * 
 * ==== POST PARAMETERS ======
 * 
 * 
 * ====== RETURN LAYOUT ======
 *  [Assessment]
 *  [
 *      {
 *          userId: Int
 *          moduleCode: String
 *          body: String
 *          date: String
 *      }
 *  ]
 */

export const AUTH_API = `${API_BASE_URL}/auth/`;


export const USER_ROLES_API = `${USERS_API}/roles/`;

export const ALL_ASSESSMENTS_API = `${MODULES_API}/all/assessments`