
package dto;

import entities.User;

/** UserDTO is used to return our username for the specific user.
 * @author Daniel, Emil, Jannich, Jimmy
 */

public class UserDTO {
    
    private String username;

    public UserDTO(User u) {
        this.username = u.getUserName();
    }

    public UserDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
}
    
    

